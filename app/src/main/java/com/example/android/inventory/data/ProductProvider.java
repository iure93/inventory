package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class ProductProvider extends ContentProvider {
    private static ProductDbHelper productDbHelper;
    private static final int PRODUCT = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        mUriMatcher.addURI(ProductContract.ProductEntry.CONTENT_AUTHORITY, ProductContract.ProductEntry.PRODUCT_PATH, PRODUCT);
        Log.i("ProductProvider.class","two types of urimatcher" + mUriMatcher);
        mUriMatcher.addURI(ProductContract.ProductEntry.CONTENT_AUTHORITY, ProductContract.ProductEntry.PRODUCT_PATH +"/#", PRODUCT_ID);
    }
    @Override
    public boolean onCreate() {
        productDbHelper = new ProductDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = mUriMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = productDbHelper.getReadableDatabase();
        Cursor cursor;
        switch (match){
            case PRODUCT:
                cursor = sqLiteDatabase.query(ProductContract.ProductEntry.PRODUCT_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(ProductContract.ProductEntry.PRODUCT_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI: " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = mUriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            default:
                throw new IllegalArgumentException("unknown URI: "+uri +" with match "+match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = mUriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Cannot insert unknown URI: " + uri);
        }
    }

    //helper method
    private Uri insertProduct(Uri uri, ContentValues values){
        SQLiteDatabase sqLiteDatabase = productDbHelper.getWritableDatabase();
        long rowInsertedId = sqLiteDatabase.insert(ProductContract.ProductEntry.PRODUCT_TABLE_NAME,null,values);
        if (rowInsertedId == -1){
            Toast.makeText(getContext(),"Insertion failed for URI: "+ uri, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(),"Insertion successful", Toast.LENGTH_SHORT).show();
        }

        Uri returnedUri = ContentUris.withAppendedId(uri,rowInsertedId);
        getContext().getContentResolver().notifyChange(returnedUri,null);
        return returnedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = productDbHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        int rowDeletedId;
        switch (match){
            case PRODUCT:
                rowDeletedId =sqLiteDatabase.delete(ProductContract.ProductEntry.PRODUCT_TABLE_NAME,selection,selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                return rowDeletedId;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowDeletedId = sqLiteDatabase.delete(ProductContract.ProductEntry.PRODUCT_TABLE_NAME,selection,selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                return rowDeletedId;
            default:
                throw new IllegalArgumentException("Deletion failed for URI: "+uri);
        }

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = mUriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                return updateProduct(uri,values,selection,selectionArgs);
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update failed for URI: " + uri);
        }
    }

    //helper method
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        SQLiteDatabase sqLiteDatabase = productDbHelper.getWritableDatabase();
        int rowUpdatedId = sqLiteDatabase.update(ProductContract.ProductEntry.PRODUCT_TABLE_NAME,values,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);
        return rowUpdatedId;
    }

}
