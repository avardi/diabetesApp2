package com.ashervardi.diabetesapp2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.sql.Statement;


/**
 * Created by Asher on 1/2/2017.
 */


public class DiabetesDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "DiabetesDb3.db";
    private static final int  TABLE_MAX_LENGTH = 500;
    // Create SQLITE Database

    public static final String SQL_CREATE_SUGAR_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + DatabaseContract.DiabetesTable.SUGAR_TABLE_NAME + " (" +
                    DatabaseContract.DiabetesTable._ID + " INTEGER PRIMARY KEY," +
                    DatabaseContract.DiabetesTable.COLUMN_NAME_MONTH + " INT," +
                    DatabaseContract.DiabetesTable.COLUMN_NAME_DAY + " INT," +
                    DatabaseContract.DiabetesTable.COLUMN_NAME_TIME + " TEXT," +
                    DatabaseContract.DiabetesTable.COLUMN_NAME_SUGAR + " TEXT," +
                    DatabaseContract.DiabetesTable.COLUMN_NAME_CARBO + " TEXT," +
                    DatabaseContract.DiabetesTable.COLUMN_NAME_BOLUS + " TEXT," +
                    DatabaseContract.DiabetesTable.COLUMN_NAME_INSULIN + " TEXT," +
                    DatabaseContract.DiabetesTable.COLUMN_NAME_COMMENT + " TEXT)" ;

    public static final String SQL_CREATE_INIT_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + DatabaseContract.DiabetesTable.INIT_TABLE_NAME + " (" +
                    DatabaseContract.DiabetesTable._ID + " INTEGER PRIMARY KEY," +
                    DatabaseContract.DiabetesTable.COLUMN_NAME_TYPE + " TEXT," +
                    DatabaseContract.DiabetesTable.COLUMN_NAME_FROM + " INT," +
                    DatabaseContract.DiabetesTable.COLUMN_NAME_TO + " INT," +
                    DatabaseContract.DiabetesTable.COLUMN_NAME_VALUE + " INT)";

    public static final String SQL_CREATE_MISC_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + DatabaseContract.DiabetesTable.MISC_TABLE_NAME + " (" +
                    DatabaseContract.DiabetesTable._ID + " INTEGER PRIMARY KEY," +
                    DatabaseContract.DiabetesTable.COLUMN_NAME_PATIENT + " TEXT," +
                    DatabaseContract.DiabetesTable.COLUMN_NAME_MAIL + " TEXT)";


    public static final String SQL_DELETE_SUGAR_TABLE =
            "DROP TABLE IF EXISTS " + DatabaseContract.DiabetesTable.SUGAR_TABLE_NAME;
    public static final String SQL_DELETE_INIT_TABLE =
            "DROP TABLE IF EXISTS " + DatabaseContract.DiabetesTable.INIT_TABLE_NAME;
    public static final String SQL_DELETE_MISC_TABLE =
            "DROP TABLE IF EXISTS " + DatabaseContract.DiabetesTable.MISC_TABLE_NAME;


    public DiabetesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        try {

            db.execSQL(SQL_CREATE_SUGAR_ENTRIES);
            db.execSQL(SQL_CREATE_INIT_ENTRIES);
            db.execSQL(SQL_CREATE_MISC_ENTRIES);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        try {
            db.execSQL(SQL_DELETE_SUGAR_TABLE);
            db.execSQL(SQL_DELETE_INIT_TABLE);
            db.execSQL(SQL_DELETE_MISC_TABLE);
            onCreate(db);
        } catch (SQLiteException e) {
            e.printStackTrace();

        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void delete_all() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_DELETE_SUGAR_TABLE);
        db.execSQL(SQL_DELETE_INIT_TABLE);
        db.execSQL(SQL_DELETE_MISC_TABLE);
    }

    public void insertInitData(Context context, String type, String from, String to, String val ){
        // Open the database for writing
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values;

        try
        {
            values = new ContentValues();

            values.put(DatabaseContract.DiabetesTable.COLUMN_NAME_TYPE, type);
            values.put(DatabaseContract.DiabetesTable.COLUMN_NAME_FROM, Integer.parseInt(from));
            values.put(DatabaseContract.DiabetesTable.COLUMN_NAME_TO , Integer.parseInt(to));
            values.put(DatabaseContract.DiabetesTable.COLUMN_NAME_VALUE, Integer.parseInt(val));

            // Insert Row
            long i = db.insert(DatabaseContract.DiabetesTable.INIT_TABLE_NAME , null, values);
            Log.i("Insert", i + "");
            // Insert into database successfully.
            db.setTransactionSuccessful();

        }
        catch (SQLiteException e)
        {
            e.printStackTrace();

        }
        finally
        {
            db.endTransaction();
            // End the transaction.
            db.close();
            // Close database
        }
    }

    public void insertMiscData(Context context, String name, String email){
        // Open the database for writing
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values;

        try
        {

            values = new ContentValues();

            values.put(DatabaseContract.DiabetesTable.COLUMN_NAME_PATIENT, name);
            values.put(DatabaseContract.DiabetesTable.COLUMN_NAME_MAIL, email);


            // Insert Row
            long i = db.insert(DatabaseContract.DiabetesTable.MISC_TABLE_NAME , null, values);
            Log.i("Insert", i + "");
            // Insert into database successfully.
            db.setTransactionSuccessful();

        }
        catch (SQLiteException e)
        {
            e.printStackTrace();

        }
        finally
        {
            db.endTransaction();
            // End the transaction.
            db.close();
            // Close database
        }
    }

    public static String[] getMiscData(Context context, DiabetesDbHelper dbH){
        // Open the database for writing
        SQLiteDatabase db = dbH.getReadableDatabase();
        ContentValues values;

        String selectQuery = "SELECT * FROM "+ DatabaseContract.DiabetesTable.MISC_TABLE_NAME ;
        Cursor cursor = db.rawQuery(selectQuery,null);
        int current_lenth = cursor.getCount();
        String ar[] = new String[] {"",""};
        if( current_lenth > 0) {
            try {
                cursor.moveToFirst();
                ar[0] =   cursor.getString(1);
                ar[1] =   cursor.getString(2);
            }
            catch (SQLiteException e) {
                e.printStackTrace();

            }
        }
        cursor.close();
        return ar;
    }


    public void insertSugarData(Context context, String date,String sugar , String carbo, String bolus, String insulin, String cmnt ){

        // Open the database for writing
        SQLiteDatabase db = this.getWritableDatabase();



        String selectQuery = "SELECT * FROM "+ DatabaseContract.DiabetesTable.SUGAR_TABLE_NAME + " ORDER BY " + DatabaseContract.DiabetesTable.COLUMN_NAME_MONTH + " DESC , " + DatabaseContract.DiabetesTable.COLUMN_NAME_DAY + " DESC";
        Cursor cursor = db.rawQuery(selectQuery,null);
        int current_lenth = cursor.getCount();
        if( current_lenth > TABLE_MAX_LENGTH) {
            db.beginTransaction();
            try {
                cursor.moveToLast();
                for (int index = TABLE_MAX_LENGTH; index < current_lenth; index++) {

               //     db.delete(DatabaseContract.DiabetesTable.TABLE_NAME, s, null);
                    db.delete(DatabaseContract.DiabetesTable.SUGAR_TABLE_NAME, "_ID = ? ", new String[]{Integer.toString(cursor.getInt(0))});

                    cursor.moveToPrevious();
                }
                db.setTransactionSuccessful();
            }
            catch (SQLiteException e) {
                e.printStackTrace();

            } finally {
                db.endTransaction();
            }
        }
        cursor.close();


        // Start the transaction.
 //       Toast.makeText( context, "Inserted new record in" + db.getPath(), Toast.LENGTH_LONG).show();
        db.beginTransaction();
        ContentValues values;

        try
        {


            values = new ContentValues();
            String date_time[] = date.split(" ");
            String mDate[] = date_time[0].split("/");
            values.put(DatabaseContract.DiabetesTable.COLUMN_NAME_MONTH, Integer.parseInt(mDate[1]));
            values.put(DatabaseContract.DiabetesTable.COLUMN_NAME_DAY,Integer.parseInt(mDate[0]));
            values.put(DatabaseContract.DiabetesTable.COLUMN_NAME_TIME, date_time[1]);
            values.put(DatabaseContract.DiabetesTable.COLUMN_NAME_SUGAR, sugar);
            values.put(DatabaseContract.DiabetesTable.COLUMN_NAME_CARBO, carbo);
            values.put(DatabaseContract.DiabetesTable.COLUMN_NAME_BOLUS , bolus);
            values.put(DatabaseContract.DiabetesTable.COLUMN_NAME_INSULIN, insulin);
            values.put(DatabaseContract.DiabetesTable.COLUMN_NAME_COMMENT, cmnt);

            // Insert Row
            long i = db.insert(DatabaseContract.DiabetesTable.SUGAR_TABLE_NAME , null, values);
            Log.i("Insert", i + "");
            // Insert into database successfully.
            db.setTransactionSuccessful();

        }
        catch (SQLiteException e)
        {
            e.printStackTrace();

        }
        finally
        {
            db.endTransaction();
            // End the transaction.
            db.close();
            // Close database
        }

    }

}