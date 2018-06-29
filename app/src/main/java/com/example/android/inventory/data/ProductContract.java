package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ProductContract {

    private ProductContract(){}

    public static class ProductEntry implements BaseColumns {

        public static final String PRODUCT_TABLE_NAME = "inventory";
        public static final String PRODUCT_TABLE_COLUMN_ID = BaseColumns._ID;
        public static final String PRODUCT_TABLE_COLUMN_NAME = "product_name";
        public static final String PRODUCT_TABLE_COLUMN_PRICE = "price";
        public static final String PRODUCT_TABLE_COLUMN_QUANTITY = "quantity";
        public static final String PRODUCT_TABLE_COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String PRODUCT_TABLE_COLUMN_SUPPLIER_PHONE_NUMBER = "phone_number";

        public static final String CONTENT_AUTHORITY = "com.example.android.inventory";
        public static final Uri BASE_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
        public static final String PRODUCT_PATH = "inventory";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PRODUCT_PATH);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + BASE_URI + "/" + PRODUCT_PATH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + BASE_URI + "/" + PRODUCT_PATH;
    }
}
