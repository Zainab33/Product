package com.example.android.product;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
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

import com.example.android.product.data.ProductContract.ProductEntry;
import com.example.android.product.data.ProductDbHelper;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_PRODUCT_LOADER = 0;
    private Uri mCurrentProdUri;
    private TextView qualityTextView;
    private EditText nameEditText, priceEditText, supplierNameEditText, supplierPhoneEditText;
    private boolean mProductHasChanged;
    private Button increamentButton, decreamentButton, callButton;
    int quantity;
    private static final int CALL_PHONE_PERMISSIONS_REQUEST = 3;

    boolean check = true;
    private View.OnTouchListener mTouchListiner = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent = getIntent();
        mCurrentProdUri = intent.getData();
        if (mCurrentProdUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }
        nameEditText = findViewById(R.id.edit_product_name);
        priceEditText = findViewById(R.id.edit_product_price);
        qualityTextView = findViewById(R.id.text_product_quantity);
        supplierNameEditText = findViewById(R.id.edit_supplier_name);
        supplierPhoneEditText = findViewById(R.id.edit_supplier_phone);
        increamentButton = findViewById(R.id.increment);
        decreamentButton = findViewById(R.id.decrement);
        callButton = findViewById(R.id.button_call);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = supplierPhoneEditText.getText().toString();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + num));
                if (ContextCompat.checkSelfPermission(EditorActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditorActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_PERMISSIONS_REQUEST);
                } else
                    startActivity(intent);
            }
        });
        increamentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = quantity + 1;
                display(quantity);
            }
        });
        decreamentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (quantity == 0) {
                    Toast.makeText(EditorActivity.this, "you cannot have less than 0 products", Toast.LENGTH_SHORT).show();
                    return; // exit method early return nothing
                } else {
                    quantity = quantity - 1;
                    display(quantity);
                }
            }
        });
        nameEditText.setOnTouchListener(mTouchListiner);
        priceEditText.setOnTouchListener(mTouchListiner);
        supplierNameEditText.setOnTouchListener(mTouchListiner);
        supplierPhoneEditText.setOnTouchListener(mTouchListiner);
        increamentButton.setOnTouchListener(mTouchListiner);
        decreamentButton.setOnTouchListener(mTouchListiner);
    }

    private void display(int number) {
        TextView quantityTextView = (TextView) findViewById(R.id.text_product_quantity);
        quantityTextView.setText("" + number);
    }

    private void saveProduct() {
        String currentName = nameEditText.getText().toString().trim();
        String currentPrice = priceEditText.getText().toString().trim();
        int currentQuantity = Integer.parseInt(qualityTextView.getText().toString());
        String currentSupplierName = supplierNameEditText.getText().toString().trim();
        String currentSupplierPhone = supplierPhoneEditText.getText().toString().trim();
        ContentValues values = new ContentValues();
        check =true;
        if ((TextUtils.isEmpty(currentName) || TextUtils.isEmpty(currentSupplierName) ||
                TextUtils.isEmpty(currentSupplierPhone)) || TextUtils.isEmpty(currentPrice)) {
            Toast.makeText(this, "plz complete data ",
                    Toast.LENGTH_SHORT).show();
            check = false;
            return;
        }
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, currentName);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, currentQuantity);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, currentSupplierName);
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONENUMBER, currentSupplierPhone);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, currentPrice);
        if (mCurrentProdUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentProdUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                if (check){
                finish();}
                return true;


            case R.id.action_delete:
                showDeletConfirmationDialog();
                break;
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeletConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deletProduct();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletProduct() {
        if (mCurrentProdUri != null) {
            int id = (int) ContentUris.parseId(mCurrentProdUri);
            int rowsDeleted = getContentResolver().delete(mCurrentProdUri, ProductEntry._ID, new String[]{String.valueOf(id)});
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, mCurrentProdUri,
                null,
                null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            int nameColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int SuppNameColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int SuppNumberColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONENUMBER);
            String name = data.getString(nameColumnIndex);
            int price = data.getInt(priceColumnIndex);
            quantity = data.getInt(quantityColumnIndex);
            String suppName = data.getString(SuppNameColumnIndex);
            String suppNumber = data.getString(SuppNumberColumnIndex);
            nameEditText.setText(name);
            priceEditText.setText(Integer.toString(price));
            qualityTextView.setText(Integer.toString(quantity));
            supplierNameEditText.setText(suppName);
            supplierPhoneEditText.setText(suppNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}