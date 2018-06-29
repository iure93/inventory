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
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnTouchListener {
    private EditText mEditProductName;
    private EditText mEditProductPrice;
    private EditText mEditProductQuantity;
    private EditText mEditProductSupplier;
    private EditText mEditSupplierPhoneNumber;

    private Button mEditMinusButton;
    private Button mEditPlusButton;
    private Button mEditDeleteButton;

    private String mEditName;
    private String mEditSupplier;
    private int mEditQuantity;
    private Double mEditPrice;
    private static final int EXISTING_PRODUCT_LOADER_ID = 0;
    private Uri uriToEdit;
    private Uri uri = ProductContract.ProductEntry.CONTENT_URI;
    private boolean mIsTouched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //initialize the views
        mEditProductName = (EditText) findViewById(R.id.edit_product_name);
        mEditProductPrice = (EditText) findViewById(R.id.edit_product_price);
        mEditProductQuantity = (EditText) findViewById(R.id.edit_product_quantity);
        mEditProductSupplier = (EditText) findViewById(R.id.edit_supplier_name);
        mEditSupplierPhoneNumber = (EditText) findViewById(R.id.edit_supplier_phone_number);

        //set touch listener for views to check if any change has been made
        mEditProductName.setOnTouchListener(this);
        mEditProductPrice.setOnTouchListener(this);
        mEditProductQuantity.setOnTouchListener(this);
        mEditProductSupplier.setOnTouchListener(this);
        mEditSupplierPhoneNumber.setOnTouchListener(this);

        //initialize buttons and touch listeners
        mEditMinusButton = (Button) findViewById(R.id.edit_minus);
        mEditPlusButton = (Button) findViewById(R.id.edit_plus);
        mEditMinusButton.setOnTouchListener(this);
        mEditPlusButton.setOnTouchListener(this);

        mEditDeleteButton = (Button) findViewById(R.id.delete_product_button);
        mEditDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct(uriToEdit);
            }
        });

        Intent intent = getIntent();
        uriToEdit = intent.getData();
        if (uriToEdit == null) {
            setTitle(getString(R.string.add_title));
            mEditDeleteButton.setVisibility(View.GONE);
        } else {
            setTitle(getString(R.string.edit_title));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER_ID, null, this);
        }

        mEditMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditQuantity = Integer.parseInt(mEditProductQuantity.getText().toString());
                if (mEditQuantity > 0) {
                    mEditQuantity -= 1;
                    mEditProductQuantity.setText(Integer.toString(mEditQuantity));
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.quantity_less_than_zero_msg), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mEditPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditQuantity = Integer.parseInt(mEditProductQuantity.getText().toString());
                mEditQuantity += 1;
                mEditProductQuantity.setText(Integer.toString(mEditQuantity));
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
        if (mIsTouched) {
            discardConfirmationDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void discardConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_edit_message);
        builder.setPositiveButton(R.string.delete_positive_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                    finish();
                }
            }
        });
        builder.setNegativeButton(R.string.delete_negative_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ((item.getItemId())) {
            case R.id.save_action:
                saveProduct();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveProduct() {
        mEditName = mEditProductName.getText().toString();
        String price = mEditProductPrice.getText().toString();
        String quantity = mEditProductQuantity.getText().toString();
        mEditSupplier = mEditProductSupplier.getText().toString();
        String phoneNumber = mEditSupplierPhoneNumber.getText().toString();

        boolean isValuesEmpty = isContentValuesEmpty(mEditName, price, quantity, mEditSupplier, phoneNumber);
        if (isValuesEmpty) {
            Toast.makeText(this, getString(R.string.data_empty_message), Toast.LENGTH_SHORT).show();
        } else {
            mEditName = mEditName.trim();
            mEditPrice = Double.parseDouble(price.trim());
            mEditQuantity = Integer.parseInt(quantity.trim());
            mEditSupplier = mEditSupplier.trim();

            ContentValues values = new ContentValues();
            values.put(ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_NAME, mEditName);
            values.put(ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_PRICE, mEditPrice);
            values.put(ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_QUANTITY, mEditQuantity);
            values.put(ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_SUPPLIER_NAME, mEditSupplier);
            values.put(ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_SUPPLIER_PHONE_NUMBER, phoneNumber);
            if (uriToEdit == null) {
                getContentResolver().insert(uri, values);
                finish();
            } else {
                getContentResolver().update(uriToEdit, values, null, null);
                finish();
                Intent intent = new Intent();
                intent.setData(uriToEdit);
                NavUtils.navigateUpTo(this, intent);
            }
        }
    }

    private void deleteProduct(final Uri uriToEdit) {
        if (uriToEdit != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_message);
            builder.setPositiveButton(R.string.delete_positive_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialog != null) {
                        int rowDeletedId = getContentResolver().delete(uriToEdit, null, null);
                        if (rowDeletedId > 0) {
                            Toast.makeText(getBaseContext(), getString(R.string.delete_successful_msg), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), getString(R.string.delete_unsuccessful_msg), Toast.LENGTH_SHORT).show();
                        }
                    }
                    dialog.dismiss();
                    finish();
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    NavUtils.navigateUpTo(EditActivity.this, intent);
                }
            });
            builder.setNegativeButton(R.string.delete_negative_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    //helper method: check if passed-in content values are empty
    private boolean isContentValuesEmpty(String name, String price, String quantity, String supplier, String phoneNumber) {
        boolean isContentValuesEmpty = false;
        if (TextUtils.isEmpty(name)) {
            //name can not be empty
            isContentValuesEmpty = true;
        }
        if (TextUtils.isEmpty(price)) {
            //price can't be null
            isContentValuesEmpty = true;
        }
        if (TextUtils.isEmpty(quantity)) {
            //quantity can't be null
            isContentValuesEmpty = true;
        }
        if (TextUtils.isEmpty(supplier)) {
            //supplier can't be null
            isContentValuesEmpty = true;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            //phone can't be null
            isContentValuesEmpty = true;
        }
        return isContentValuesEmpty;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //projection must include column_id because cursor needs id to function properly
        String[] projection = new String[]{ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_ID,
                ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_NAME,
                ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_PRICE,
                ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_QUANTITY,
                ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_SUPPLIER_NAME,
                ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_SUPPLIER_PHONE_NUMBER};
        switch (id) {
            case EXISTING_PRODUCT_LOADER_ID:
                return new CursorLoader(this, uriToEdit, projection, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            if (data.moveToFirst()) {
                int nameColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_NAME);
                int priceColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_PRICE);
                int quantityColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_QUANTITY);
                int supplierColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_SUPPLIER_NAME);
                int supplierPhoneColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_SUPPLIER_PHONE_NUMBER);

                String name = data.getString(nameColumnIndex);
                double price = data.getDouble(priceColumnIndex);
                int quantity = data.getInt(quantityColumnIndex);
                String supplier = data.getString(supplierColumnIndex);
                String supplierPhone = data.getString(supplierPhoneColumnIndex);

                mEditProductName.setText(name);
                mEditProductPrice.setText(String.valueOf(price));
                mEditProductQuantity.setText(String.valueOf(quantity));
                mEditProductSupplier.setText(supplier);
                mEditSupplierPhoneNumber.setText(supplierPhone);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mEditProductName.setText(null);
        mEditProductPrice.setText(null);
        mEditProductQuantity.setText(null);
        mEditProductSupplier.setText(null);
        mEditSupplierPhoneNumber.setText(null);
    }
}
