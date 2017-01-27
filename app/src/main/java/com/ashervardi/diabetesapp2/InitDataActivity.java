package com.ashervardi.diabetesapp2;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;


public class InitDataActivity extends AppCompatActivity {
    static InitContainer carboInsulinR = new InitContainer("CarboInsulinRatio");
    static InitContainer correctionF = new InitContainer("correctionFactor");
    static InitContainer sugarT = new InitContainer("SugatTarget");
    static InitContainer currentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_data);
        currentContainer = sugarT;
        loadFields();
        displayList(currentContainer);
    }

    public void onAddButtonClicked(View view) {

        EditText mEditText = (EditText) this.findViewById(R.id.editInitfrom);
        int from = Integer.parseInt(String.valueOf(mEditText.getText()));
        mEditText = (EditText) this.findViewById(R.id.editInitto);
        int to = Integer.parseInt(String.valueOf(mEditText.getText()));
        mEditText = (EditText) this.findViewById(R.id.editInitvalue);
        int val = Integer.parseInt(String.valueOf(mEditText.getText()));
        if (from >= 0 && from <= 24 && to >= 0 && to <= 24 && val > 0) {
            currentContainer.insert(from, to, val);
            displayList(currentContainer);

        } else Toast.makeText(this, "ERROR - you have a problem with the data! " +  " !", Toast.LENGTH_LONG).show();
        mEditText = (EditText) this.findViewById(R.id.editInitfrom);
 //       mEditText.setText("0");
        mEditText.getText().clear();
        mEditText.requestFocus();
        mEditText = (EditText) this.findViewById(R.id.editInitto);
 //       mEditText.setText("0");
        mEditText.getText().clear();
        mEditText = (EditText) this.findViewById(R.id.editInitvalue);
 //       mEditText.setText("0");
        mEditText.getText().clear();


    }
    public void onResetButtonClicked(View view) {
        currentContainer.delete();
        displayList(currentContainer);
        EditText mEditText = (EditText) this.findViewById(R.id.editInitfrom);
        mEditText.requestFocus();


    }

    public void onSaveButtonClicked(View view) {
        DiabetesDbHelper mDbHelper = new DiabetesDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Remove Previous data
        db.execSQL("DELETE FROM " + DatabaseContract.DiabetesTable.INIT_TABLE_NAME + ";");
        db.close();
        // Save Init data to SQL
        Iterator it = carboInsulinR.getIterator();
        while (it.hasNext()) {
            InitData d = (InitData) it.next();
            mDbHelper.insertInitData(getApplicationContext(), DatabaseContract.DiabetesTable.TYPE_NAME_CARBO_INSULIN_RATIO, Integer.toString(d.get_from()), Integer.toString(d.get_to()), Integer.toString(d.get_val()));
        }
        it = correctionF.getIterator();
        while (it.hasNext()) {
            InitData d = (InitData) it.next();
            mDbHelper.insertInitData(getApplicationContext(), DatabaseContract.DiabetesTable.TYPE_NAME_CORRECTION_FACTOR, Integer.toString(d.get_from()), Integer.toString(d.get_to()), Integer.toString(d.get_val()));
        }
        it = sugarT.getIterator();
        while (it.hasNext()) {
            InitData d = (InitData) it.next();
            mDbHelper.insertInitData(getApplicationContext(), DatabaseContract.DiabetesTable.TYPE_NAME_SUGAR_TARGET, Integer.toString(d.get_from()), Integer.toString(d.get_to()), Integer.toString(d.get_val()));
        }
        // Save MISC data
        //Remove Previous data
        db = mDbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + DatabaseContract.DiabetesTable.MISC_TABLE_NAME + ";");
        db.close();
        EditText mEditText = (EditText) this.findViewById(R.id.patientName);
        String name = mEditText.getText().toString();
        mEditText = (EditText) this.findViewById(R.id.patientEmail);
        String email = mEditText.getText().toString();
        mDbHelper.insertMiscData(getApplicationContext(), name, email);
        // Reset containers
        sugarT.delete();
        carboInsulinR.delete();
        correctionF.delete();

        //Terminate Activity
        Intent resultIntent = new Intent();
        // TODO Add extras or a data URI to this intent as appropriate.
        resultIntent.putExtra("Done", "OK");
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.SugartButton:
                if (checked) {
                    displayList(sugarT);
                    currentContainer = sugarT;
                }
                    break;
            case R.id.carboButton:
                if (checked) {
                    displayList(carboInsulinR);
                    currentContainer = carboInsulinR;
                }
                    break;
            case R.id.correctButton:
                if (checked) {
                    displayList(correctionF);
                    currentContainer = correctionF;
                }
                    break;
        }
    }

    public void loadFields() {
        DiabetesDbHelper mDbHelper = new DiabetesDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Load MISC Data
        String selectQuery = "SELECT " + DatabaseContract.DiabetesTable.COLUMN_NAME_PATIENT + "," + DatabaseContract.DiabetesTable.COLUMN_NAME_MAIL + " FROM " + DatabaseContract.DiabetesTable.MISC_TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        int current_lenth = cursor.getCount();
        if (current_lenth > 0) {
            db.beginTransaction();
            cursor.moveToNext();
            try {
                EditText mEditText = (EditText) this.findViewById(R.id.patientName);
                mEditText.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_PATIENT)));
                mEditText = (EditText) this.findViewById(R.id.patientEmail);
                mEditText.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_MAIL)));
                db.setTransactionSuccessful();
            } catch (SQLiteException e) {
                e.printStackTrace();

            } finally {
                db.endTransaction();
            }
        }
        cursor.close();
        // Load Init Data
        selectQuery = "SELECT * FROM " + DatabaseContract.DiabetesTable.INIT_TABLE_NAME + " WHERE " + DatabaseContract.DiabetesTable.COLUMN_NAME_TYPE + " = '" + DatabaseContract.DiabetesTable.TYPE_NAME_SUGAR_TARGET + "'";
        cursor = db.rawQuery(selectQuery, null);
        current_lenth = cursor.getCount();
        if (current_lenth > 0) {
//            cursor.moveToFirst();
            sugarT.delete();
            while (cursor.moveToNext()) {
                sugarT.insert(cursor.getInt(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_FROM)), cursor.getInt(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_TO)), cursor.getInt(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_VALUE)));
            }
        }
        displayList(sugarT);
        selectQuery = "SELECT * FROM " + DatabaseContract.DiabetesTable.INIT_TABLE_NAME + " WHERE " + DatabaseContract.DiabetesTable.COLUMN_NAME_TYPE + " = '" + DatabaseContract.DiabetesTable.TYPE_NAME_CARBO_INSULIN_RATIO + "'";
        cursor = db.rawQuery(selectQuery, null);
        current_lenth = cursor.getCount();
        if (current_lenth > 0) {
//            cursor.moveToFirst();
            carboInsulinR.delete();
            while (cursor.moveToNext()) {
                carboInsulinR.insert(cursor.getInt(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_FROM)), cursor.getInt(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_TO)), cursor.getInt(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_VALUE)));
            }
        }

        selectQuery = "SELECT * FROM " + DatabaseContract.DiabetesTable.INIT_TABLE_NAME + " WHERE " + DatabaseContract.DiabetesTable.COLUMN_NAME_TYPE + " = '" + DatabaseContract.DiabetesTable.TYPE_NAME_CORRECTION_FACTOR + "'";
        cursor = db.rawQuery(selectQuery, null);
        current_lenth = cursor.getCount();
        if (current_lenth > 0) {
//            cursor.moveToFirst();
            correctionF.delete();
            while (cursor.moveToNext()) {
                correctionF.insert(cursor.getInt(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_FROM)), cursor.getInt(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_TO)), cursor.getInt(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_VALUE)));
            }
        }
    }

    void displayList(InitContainer cont){
        String str = "";
        Iterator it = cont.getIterator();
        while (it.hasNext()) {
            InitData d = (InitData) it.next();
            str = str + d.get_from() + "   " + d.get_to() + "   " + d.get_val() + '\n';
        }
        TextView mEditText = (TextView)this.findViewById(R.id.displayWindow);
        mEditText.setText(str);
    }
}