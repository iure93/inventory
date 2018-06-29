package com.example.android.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,View.OnTouchListener{
    private EditText mEditNameV,mEditPriceV,mEditQuantityV,mEditSupplierV,mEditSupplierPhoneV;
    private TextView mNameRequiredV, mPriceRequiredV, mQuantityRequiredV, mSupplierRequiredV, mPhoneRequiredV;
    private Button mEditMinusBtn,mEditPlusBtn,mEditDeleteBtn;
    private String mEditName,mEditSupplier;
    private int mEditQuantity=0;
    private Double mEditPrice;
    private static final int LOADER_ID = 1;
    private Uri uriToEdit;
    private Uri uri = ProductContract.ProductEntry.CONTENT_URI;
    private boolean mIsTouched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //init views
        mEditNameV = (EditText) findViewById(R.id.edit_product_name);
        mEditPriceV = (EditText) findViewById(R.id.edit_product_price);
        mEditQuantityV = (EditText) findViewById(R.id.edit_product_quantity);
        mEditSupplierV = (EditText) findViewById(R.id.edit_supplier_name);
        mEditSupplierPhoneV =(EditText) findViewById(R.id.edit_supplier_phone_number);

        //set touch listener for views to check if any change has been made
        mEditNameV.setOnTouchListener(this);
        mEditPriceV.setOnTouchListener(this);
        mEditQuantityV.setOnTouchListener(this);
        mEditSupplierV.setOnTouchListener(this);
        mEditSupplierPhoneV.setOnTouchListener(this);

        //btn init and touch listener
        mEditMinusBtn = (Button) findViewById(R.id.edit_minus);
        mEditPlusBtn = (Button) findViewById(R.id.edit_plus);
        mEditMinusBtn.setOnTouchListener(this);
        mEditPlusBtn.setOnTouchListener(this);

        mEditDeleteBtn = (Button) findViewById(R.id.delete_product_button);
        mEditDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct(uriToEdit);
            }
        });

        Intent intent = getIntent();
        uriToEdit = intent.getData();
        if (uriToEdit == null){
            setTitle(getString(R.string.add_title));
            mEditDeleteBtn.setVisibility(View.GONE);
        }else{
            setTitle(getString(R.string.edit_title));
            getLoaderManager().initLoader(LOADER_ID,null,this);
        }

        mEditMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditQuantity = Integer.parseInt(mEditQuantityV.getText().toString());
                if (mEditQuantity > 0){
                    mEditQuantity -= 1;
                    mEditQuantityV.setText(Integer.toString(mEditQuantity));
                }else{
                    Toast.makeText(getBaseContext(),getString(R.string.quantity_less_than_zero_msg),Toast.LENGTH_SHORT).show();
                }
            }
        });

        mEditPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditQuantity = Integer.parseInt(mEditQuantityV.getText().toString());
                mEditQuantity += 1;
                mEditQuantityV.setText(Integer.toString(mEditQuantity));
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mIsTouched = true;
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mIsTouched){
            discardConfirmationDialog();
        }else{
            super.onBackPressed();
        }
    }
    private void discardConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_edit_message);
        builder.setPositiveButton(R.string.delete_positive_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog!= null){
                    dialog.dismiss();
                    finish();
                }
            }
        });
        builder.setNegativeButton(R.string.delete_negative_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog!= null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ((item.getItemId())){
            case R.id.save_action:
                saveProduct();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveProduct(){

        mEditName = mEditNameV.getText().toString();
        String price = mEditPriceV.getText().toString();
        String quantity = mEditQuantityV.getText().toString();
        mEditSupplier = mEditSupplierV.getText().toString();
        String phoneNumber = mEditSupplierPhoneV.getText().toString();

        boolean isValuesEmpty = isContentValuesEmpty(mEditName,price,quantity,mEditSupplier,phoneNumber);
        if (isValuesEmpty){
            Toast.makeText(this,getString(R.string.data_empty_message),Toast.LENGTH_SHORT).show();
        }else{
            mEditName = mEditName.trim();
            mEditPrice = Double.parseDouble(price.trim());
            mEditQuantity = Integer.parseInt(quantity.trim());
            mEditSupplier = mEditSupplier.trim();

            ContentValues values = new ContentValues();
            values.put(ProductContract.ProductEntry.TABLE_COLUMN_NAME,mEditName);
            values.put(ProductContract.ProductEntry.TABLE_COLUMN_PRICE,mEditPrice);
            values.put(ProductContract.ProductEntry.TABLE_COLUMN_QUANTITY,mEditQuantity);
            values.put(ProductContract.ProductEntry.TABLE_COLUMN_SUPPLIER_NAME,mEditSupplier);
            values.put(ProductContract.ProductEntry.TABLE_COLUMN_SUPPLIER_PHONE_NUMBER,phoneNumber);
            if (uriToEdit == null) {
                getContentResolver().insert(uri, values);
                finish();
            }else{
                getContentResolver().update(uriToEdit, values, null, null);
                finish();
                Intent intent = new Intent();
                intent.setData(uriToEdit);
                NavUtils.navigateUpTo(this,intent);
            }
        }
    }

    private void deleteProduct(final Uri uriToEdit){
        if (uriToEdit!= null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_message);
            builder.setPositiveButton(R.string.delete_positive_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialog!= null){
                        int rowDeletedId = getContentResolver().delete(uriToEdit,null,null);
                        if (rowDeletedId > 0){
                            Toast.makeText(getBaseContext(),getString(R.string.delete_successful_msg),Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getBaseContext(),getString(R.string.delete_unsuccessful_msg),Toast.LENGTH_SHORT).show();
                        }
                    }
                    dialog.dismiss();
                    finish();
                    Intent intent = new Intent(EditActivity.this,MainActivity.class);
                    NavUtils.navigateUpTo(EditActivity.this,intent);
                }
            });
            builder.setNegativeButton(R.string.delete_negative_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialog != null){
                        dialog.dismiss();
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    //helper method: check if passed-in content values are empty
    private boolean isContentValuesEmpty(String name,String price,String quantity,String supplier,String phoneNumber){
        boolean isContentValuesEmpty = false;
        if (TextUtils.isEmpty(name)){
            //name can not be empty
            isContentValuesEmpty = true;
            Toast.makeText(getBaseContext(),getString(R.string.name_required),Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(price)){
            //price can't be null
            isContentValuesEmpty = true;
            Toast.makeText(getBaseContext(),getString(R.string.price_required),Toast.LENGTH_SHORT).show();

        }
        if (TextUtils.isEmpty(quantity)){
            //quantity can't be null
            isContentValuesEmpty = true;
            Toast.makeText(getBaseContext(),getString(R.string.quantity_required),Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(supplier)){
            //supplier can't be null
            isContentValuesEmpty = true;
            Toast.makeText(getBaseContext(),getString(R.string.supplier_required),Toast.LENGTH_SHORT).show();

        }
        if (TextUtils.isEmpty(phoneNumber)){
            //phone can't be null
            isContentValuesEmpty = true;
            Toast.makeText(getBaseContext(),getString(R.string.phone_required),Toast.LENGTH_SHORT).show();

        }
        return isContentValuesEmpty;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //projection must include column_id because cursor needs id to function properly
        String[] projection = new String[]{ProductContract.ProductEntry.TABLE_COLUMN_ID,
                ProductContract.ProductEntry.TABLE_COLUMN_NAME,
                ProductContract.ProductEntry.TABLE_COLUMN_PRICE,
                ProductContract.ProductEntry.TABLE_COLUMN_QUANTITY,
                ProductContract.ProductEntry.TABLE_COLUMN_SUPPLIER_NAME,
                ProductContract.ProductEntry.TABLE_COLUMN_SUPPLIER_PHONE_NUMBER};
        switch (id){
            case LOADER_ID:
                return new CursorLoader(this,uriToEdit,projection,null,null,null);
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

                mEditNameV.setText(name);
                mEditPriceV.setText(String.valueOf(price));
                mEditQuantityV.setText(String.valueOf(quantity));
                mEditSupplierV.setText(supplier);
                mEditSupplierPhoneV.setText(supplierPhone);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mEditNameV.setText(null);
        mEditPriceV.setText(null);
        mEditQuantityV.setText(null);
        mEditSupplierV.setText(null);
        mEditSupplierPhoneV.setText(null);
    }
}
