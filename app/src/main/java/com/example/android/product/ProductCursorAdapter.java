package com.example.android.product;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.android.product.data.ProductContract.ProductEntry;
public class ProductCursorAdapter extends CursorAdapter {
    int productQuntity;


    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_list, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        final int id = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));
        TextView nameTextView = view.findViewById(R.id.text_product_name);
        TextView price_quntityTextView = view.findViewById(R.id.text_Price_Quantity);
        final Button saleButton = view.findViewById(R.id.button_sale);
        String productName = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        int productPrice = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        productQuntity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        nameTextView.setText(productName);
        price_quntityTextView.setText(productPrice + ", " + productQuntity);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productQuntity > 0) {
                    productQuntity = productQuntity - 1;
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuntity);

                    int y = context.getContentResolver().update(Uri.withAppendedPath(ProductEntry.CONTENT_URI,String.valueOf(id))
                            , values, ProductEntry._ID, new String[]{String.valueOf(productQuntity)});
                }
            }
        });
    }
}


