package com.example.android.product.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.product.data.ProductContract.ProductEntry;


/**
 * Created by ascom on 24/05/2018.
 */

public class ProductDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="shelter.db";
    private static final int DATABASE_VERSION=1;

    public ProductDbHelper(Context context){
        super(context,DATABASE_NAME,null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREAT_PRODUCT_TABLE ="CREATE TABLE "+ ProductEntry.TABLE_NAME+ "("
                + ProductEntry._ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_NAME +" TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_PRICE +" INTEGER, "
                + ProductEntry.COLUMN_PRODUCT_QUANTITY +" INTERGER NOT NULL, "
                + ProductEntry.COLUMN_SUPPLIER_NAME +" TEXT NOT NULL, "
                + ProductEntry.COLUMN_SUPPLIER_PHONENUMBER +" TEXT NOT NULL);";
        db.execSQL(SQL_CREAT_PRODUCT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {


    }
}
