package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductDbHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "product_inventory.db";
    static final int TABLE_VERSION = 1;
    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, TABLE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SQL_TABLE = "CREATE TABLE " + ProductContract.ProductEntry.PRODUCT_TABLE_NAME +"("
                + ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_NAME + " TEXT NOT NULL,"
                + ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_PRICE + " DECIMAL(2) NOT NULL,"
                + ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0,"
                + ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_SUPPLIER_NAME +" TEXT NOT NULL,"
                + ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL"
                + ");";
        db.execSQL(CREATE_SQL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
