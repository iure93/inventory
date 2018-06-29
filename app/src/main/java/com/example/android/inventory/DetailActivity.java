package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.inventory.data.ProductContract;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    TextView mDetailNameV,mDetailPriceV,mDetailQuantityV,mDetailSupplierV,mDetailSupplierPhoneV;
    Button callButton;
    private static final int LOADER_ID = 1;
    Uri uriForDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        uriForDetail = intent.getData();

        mDetailNameV = (TextView) findViewById(R.id.detail_product_name);
        mDetailPriceV = (TextView) findViewById(R.id.detail_product_price);
        mDetailQuantityV = (TextView) findViewById(R.id.detail_product_quantity);
        mDetailSupplierV = (TextView) findViewById(R.id.detail_supplier_name);
        mDetailSupplierPhoneV = (TextView) findViewById(R.id.detail_supplier_phone_number);
        callButton = (Button) findViewById(R.id.call_supplier);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.fromParts("tel",mDetailSupplierPhoneV.getText().toString(),null));
                startActivity(intent);
            }
        });


        if (uriForDetail != null){
            getLoaderManager().initLoader(LOADER_ID,null,this);
        }else{
            throw new IllegalArgumentException("invalid url");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{ProductContract.ProductEntry.TABLE_COLUMN_ID,
                ProductContract.ProductEntry.TABLE_COLUMN_NAME,
                ProductContract.ProductEntry.TABLE_COLUMN_PRICE,
                ProductContract.ProductEntry.TABLE_COLUMN_QUANTITY,
                ProductContract.ProductEntry.TABLE_COLUMN_SUPPLIER_NAME,
                ProductContract.ProductEntry.TABLE_COLUMN_SUPPLIER_PHONE_NUMBER,
                };
        switch (id){
            case LOADER_ID:
                return new CursorLoader(this,uriForDetail,projection,null,null,null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null){
            if (data.moveToFirst()){
                int nameColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.TABLE_COLUMN_NAME);
                int priceColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.TABLE_COLUMN_PRICE);
                int quantityColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.TABLE_COLUMN_QUANTITY);
                int supplierColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.TABLE_COLUMN_SUPPLIER_NAME);
                int supplierPhoneColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.TABLE_COLUMN_SUPPLIER_PHONE_NUMBER);

                String name = data.getString(nameColumnIndex);
                double price = data.getDouble(priceColumnIndex);
                int quantity = data.getInt(quantityColumnIndex);
                String supplier = data.getString(supplierColumnIndex);
                String supplierPhone = data.getString(supplierPhoneColumnIndex);

                mDetailNameV.setText(name);
                mDetailPriceV.setText(String.valueOf(price));
                mDetailQuantityV.setText(String.valueOf(quantity));
                mDetailSupplierV.setText(supplier);
                mDetailSupplierPhoneV.setText(supplierPhone);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDetailNameV.setText(null);
        mDetailPriceV.setText(null);
        mDetailQuantityV.setText(null);
        mDetailSupplierV.setText(null);
        mDetailSupplierPhoneV.setText(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_action:
                Intent intent = new Intent(DetailActivity.this,EditActivity.class);
                intent.setData(uriForDetail);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
