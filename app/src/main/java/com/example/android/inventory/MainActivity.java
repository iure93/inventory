package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, ProductCursorAdapter.OnSaleButtonClickListener {
    private static final int LOADER_ID = 0;
    private Uri uri = ProductContract.ProductEntry.CONTENT_URI;
    ProductCursorAdapter mProductCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView productList = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        TextView emptyView = findViewById(R.id.empty_title_text);
        productList.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of products data in the Cursor.
        // There is no product data yet (until the loader finishes) so pass in null for the Cursor.
        mProductCursorAdapter = new ProductCursorAdapter(this, null);
        productList.setAdapter(mProductCursorAdapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);

        FloatingActionButton floatingActionButton = findViewById(R.id.float_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                Uri uriForDetail = ContentUris.withAppendedId(uri, id);
                intent.setData(uriForDetail);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //projection must include column_id because cursor needs id to function properly
        String[] projection = new String[]{ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_ID,
                ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_NAME,
                ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_PRICE,
                ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_QUANTITY};
        switch (id) {
            case LOADER_ID:
                return new CursorLoader(this, uri, projection, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mProductCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductCursorAdapter.swapCursor(null);
    }

    @Override
    public void onSaleButtonClick(int rowId) {
        Uri uriToUpdate = ContentUris.withAppendedId(uri, rowId);
        String[] projection = new String[]{ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_QUANTITY};
        Cursor cursor = getContentResolver().query(uriToUpdate, projection, null, null, null);
        if (cursor.moveToFirst()) {
            int quantity = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_QUANTITY));
            if (quantity > 0) {
                quantity -= 1;
            } else {
                Toast.makeText(this, getString(R.string.quantity_sold_out_msg), Toast.LENGTH_SHORT).show();
            }
            ContentValues values = new ContentValues();
            values.put(ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_QUANTITY, quantity);
            getContentResolver().update(uriToUpdate, values, null, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to delete all pets in the database.
     */

    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(ProductContract.ProductEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + " rows deleted from product database");
    }

}