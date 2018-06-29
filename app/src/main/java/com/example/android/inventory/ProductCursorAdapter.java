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
    private OnSaleButtonClickListener saleBtnClickListener;
    private int id;

    public interface OnSaleButtonClickListener {
        public void onSaleButtonClick(int rowId);
    }

    public ProductCursorAdapter(Context context, Cursor cursor){
        super(context,cursor,0);
        this.context = context;
        try{
            saleBtnClickListener = (OnSaleButtonClickListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement OnSaleButtonClickListener");
        }
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v =(View) LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
        int idColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.TABLE_COLUMN_ID);
        int rowId = cursor.getInt(idColumnIndex);
        Button saleBtn = (Button) v.findViewById(R.id.sale_button);
        saleBtn.setTag(rowId);
        return v;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextV = (TextView) view.findViewById(R.id.product_name);
        TextView priceTextV = (TextView) view.findViewById(R.id.product_price);
        TextView quantityTextV = (TextView) view.findViewById(R.id.product_quantity);
        final Button saleBtn = (Button) view.findViewById(R.id.sale_button);

        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.TABLE_COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.TABLE_COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.TABLE_COLUMN_QUANTITY);

        String name = cursor.getString(nameColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        int cursorQuantity = cursor.getInt(quantityColumnIndex);

        nameTextV.setText(name);
        priceTextV.setText(String.valueOf(price));
        quantityTextV.setText(String.valueOf(cursorQuantity));

        saleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = (Integer) saleBtn.getTag();
                saleBtnClickListener.onSaleButtonClick(id);
            }
        });
    }
}
