package com.example.android.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventory.data.ProductContract;

public class ProductCursorAdapter extends CursorAdapter {
    private Context context;
    private OnSaleButtonClickListener saleButtonClickListener;
    private int id;

    public interface OnSaleButtonClickListener {
        void onSaleButtonClick(int rowId);
    }

    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.context = context;
        try {
            saleButtonClickListener = (OnSaleButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement OnSaleButtonClickListener");
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        int idColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_ID);
        int rowId = cursor.getInt(idColumnIndex);
        Button saleButton = view.findViewById(R.id.sale_button);
        saleButton.setTag(rowId);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameText = (TextView) view.findViewById(R.id.product_name);
        TextView priceText = (TextView) view.findViewById(R.id.product_price);
        TextView quantityText = (TextView) view.findViewById(R.id.product_quantity);
        final Button saleButton = (Button) view.findViewById(R.id.sale_button);

        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.PRODUCT_TABLE_COLUMN_QUANTITY);

        String name = cursor.getString(nameColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        int cursorQuantity = cursor.getInt(quantityColumnIndex);

        nameText.setText(name);
        priceText.setText(String.valueOf(price));
        quantityText.setText(String.valueOf(cursorQuantity));

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = (Integer) saleButton.getTag();
                saleButtonClickListener.onSaleButtonClick(id);
            }
        });
    }
}
