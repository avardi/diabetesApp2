package com.ashervardi.diabetesapp2;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.ashervardi.diabetesapp2.DiabetesDbHelper.SQL_CREATE_SUGAR_ENTRIES;
import static com.ashervardi.diabetesapp2.DiabetesDbHelper.SQL_DELETE_SUGAR_TABLE;


public class MainActivity extends AppCompatActivity implements EnterDataDialogFragment.EnterDataDialogListener{

    //  Static Variables declarations
    static String carboInsulinRatio, correctionFactor, sugarTarget;
    static String currentSugar;
    private BroadcastReceiver PDFbroadcastReceiver;

 /*
    static InitContainer carboInsulinR = new InitContainer("CarboInsulinRatio");
    static InitContainer correctionF = new InitContainer("correctionFactor");
    static InitContainer sugarT = new InitContainer("SugatTarget");
*/
    static String PatientName;
    static String PatientEmail;
    static boolean Initialized = false;

    static final int INIT_ACTIVITY = 1;
    static MainActivity my_main_activity;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        setLocale("he");
 //       Locale myLocale = getResources().getConfiguration().locale;
        Locale myLocale = getCurrentLocale();
        if (myLocale.getLanguage().equals(new Locale("he").getLanguage())) {
            Toast.makeText(this," Local Language is Hebrew...", Toast.LENGTH_SHORT).show();
        }

        setInitialValues();
    // save the main activity for further reference
        my_main_activity = this;


        // define broadCast receiver to communicate with PDF writer
        PDFbroadcastReceiver = new BroadcastReceiver() {

            @Override
            // When writing the PDF file is done .. send it to the user.
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(WriteDocService.SERVICE_MESSAGE);
                // do something here.
                if (s.equals("Done")){
                    File file = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOCUMENTS), "pdfdemo");
                    Send(file, "sugarReport.pdf", PatientEmail);
                } else if (s.equals("Error")){
                    Toast.makeText(context, "Error: Document was not sent...", Toast.LENGTH_SHORT).show();
                }

            }

        };

    // -- Save Button Listener
        final Button buttonS = (Button) findViewById(R.id.saveButton);
        buttonS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(my_main_activity);
                showEditDialog();
            }
        });


    // -- Calc Button Listener
        final Button button = (Button) findViewById(R.id.calcButton);
        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            public void onClick(View v) {

                hideKeyboard(my_main_activity);
                // Calculate Bollus -----
                double C_bolus;

                setInitialValues();

                int S_Target = Integer.parseInt(sugarTarget);
                int Corr_F = Integer.parseInt(correctionFactor);
                int Carbo_R = Integer.parseInt(carboInsulinRatio);
                TextView myText = (TextView) findViewById(R.id.currentSugarLevel);
                int C_Sugar = Integer.parseInt(myText.getText().toString());
                myText = (TextView) findViewById(R.id.carbo);
                int M_carbo = Integer.parseInt(myText.getText().toString());

                if (C_Sugar >= 70) {
                    C_bolus = (double) (C_Sugar - S_Target) / (double) Corr_F + (double) M_carbo / (double) Carbo_R;

                } else {
                    // Low Sugar message!
                    C_bolus = 0;
                    Toast.makeText(getApplicationContext(), R.string.toast_low_sugar, Toast.LENGTH_SHORT).show();
                }
                myText = (TextView) findViewById(R.id.bolusVal);
                myText.setText(String.format("%.1f", C_bolus));

            }
        });

// -----------   create Diabetes DB
 //       DiabetesDbHelper mDbHelper = new DiabetesDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((PDFbroadcastReceiver),
                new IntentFilter(WriteDocService.SERVICE_RESULT));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(PDFbroadcastReceiver);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_init) {

            Intent intent = new Intent(this, InitDataActivity.class);
            Context c = getApplicationContext();
            int request = INIT_ACTIVITY;
            startActivityForResult(intent,request);
//            Toast.makeText(c,"Initailization completed",Toast.LENGTH_LONG).show();
            return true;
        }
        if(id== R.id.action_delete){
            Intent intent = new Intent(this, DisplayDiabetesDataActivity.class);
            Context c = getApplicationContext();
            startActivity(intent);
        }
        if(id== R.id.action_history){
            Intent intent = new Intent(this, DisplayDataActivity.class);
            Context c = getApplicationContext();
            startActivity(intent);
        }
        if (id == R.id.action_pdf) {
          /*
           * Creates a new Intent to start the WriteDocService
           * IntentService. Do not currently pass any data to the service
          */

            Intent mServiceIntent = new Intent(this, WriteDocService.class);
            mServiceIntent.putExtra("patientName", PatientName);

            startService(mServiceIntent);
            // debug
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), "pdfdemo");
        }
        if(id == R.id.action_reset){

            AlertDialog.Builder alertDB = new AlertDialog.Builder(this);
            alertDB.setTitle("Reset Action");
            alertDB.setMessage("Are you sure you want to delete the Database?");
            alertDB.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // continue with delete
                            DiabetesDbHelper mDbHelper = new DiabetesDbHelper(MainActivity.this, DiabetesDbHelper.DATABASE_NAME);
                            SQLiteDatabase db = mDbHelper.getWritableDatabase();
                            db.execSQL(SQL_DELETE_SUGAR_TABLE);
                            db.execSQL(SQL_CREATE_SUGAR_ENTRIES);

                            Toast.makeText(MainActivity.this,"Database was deleted!",Toast.LENGTH_LONG).show();
                        }
                    });
            alertDB.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this,"Operation cancelled!",Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog alertDialog = alertDB.create();
            alertDialog.show();

                         }
        if(id== R.id.action_exit){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    private void Send(File dir, String file_name, String email) {

        Intent i = new Intent(Intent.ACTION_SENDTO);

        String uriText =
                "mailto:" + email  +
                        "?subject=" + Uri.encode("Diabetes Report") +
                        "&body=" + Uri.encode("Attached is the report sent to you by DiabetesApp. ");

        Uri uri = Uri.parse(uriText);


        i.setData(uri);

      File filelocation = new File(dir + "/" + file_name);

//        File filelocation = new File("/storage/sdcard0/DCIM/Camera/20150228_102414.jpg");
        Uri path = Uri.fromFile(filelocation);
        i.putExtra(Intent.EXTRA_STREAM, path);


//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    // Save current values to database.
    private void saveData(String insulin) {
        // Gets the data repository in write mode
        DiabetesDbHelper mDbHelper = new DiabetesDbHelper(this, DiabetesDbHelper.DATABASE_NAME);
//        SQLiteDatabase db = mDbHelper.getWritableDatabase();


// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm");
        Date now = new Date();
        String date = dateFormat.format(now);

        try {
            TextView t = (TextView) findViewById(R.id.currentSugarLevel);
            String sugar = t.getText().toString();

            t.setText("");
            t = (TextView) findViewById(R.id.carbo);
            String carbo = t.getText().toString();

            t.setText("");
            t = (TextView) findViewById(R.id.bolusVal);
            String bolus = t.getText().toString();
            t.setText("0");

            t = (TextView) findViewById(R.id.comment);
            String cmnt = t.getText().toString();
            t.setText("");
            // check if sugar & carbo values are legal.
            int tmp = Integer.parseInt(sugar);
            tmp = Integer.parseInt(carbo);

// Insert the new row, returning the primary key value of the new row
            mDbHelper.insertSugarData(this, date,sugar, carbo, bolus, insulin, cmnt );

        } catch(java.lang.NumberFormatException e){
            Toast.makeText(this, R.string.toast_invalidInput, Toast.LENGTH_LONG).show();
        }

//        Toast.makeText(this, "Inserted new record " +  " !", Toast.LENGTH_SHORT).show();
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        EnterDataDialogFragment editDialogFragment = EnterDataDialogFragment.newInstance(getString(R.string.promt_insulin));
        editDialogFragment.show(fm, "fragment_edit_name");
    }



    @Override
    public void onFinishEditDialog(String inputText) {

        saveData(inputText);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (INIT_ACTIVITY) : {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.
                    String returnValue = data.getStringExtra("Done");
                    if (returnValue.equals("OK")){
                        setInitialValues();
                    }
                }
                break;
            }
        }
    }


    private void setInitialValues(){

        DiabetesDbHelper mDbHelper = new DiabetesDbHelper(this, DiabetesDbHelper.DATABASE_NAME);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Date dNow = new Date( );
        @SuppressLint("SimpleDateFormat") SimpleDateFormat ft = new SimpleDateFormat ("H");
        String current_hour = ft.format(dNow);
        String selectQuery = "SELECT " + DatabaseContract.DiabetesTable.COLUMN_NAME_TYPE + " , " + DatabaseContract.DiabetesTable.COLUMN_NAME_VALUE  + " FROM "+ DatabaseContract.DiabetesTable.INIT_TABLE_NAME + " WHERE ( " +
                DatabaseContract.DiabetesTable.COLUMN_NAME_FROM + " <= " + current_hour + "  AND " +  DatabaseContract.DiabetesTable.COLUMN_NAME_TO + " > " + current_hour + " ) OR  (" +
                DatabaseContract.DiabetesTable.COLUMN_NAME_FROM + " <= " + current_hour + "  OR " +  DatabaseContract.DiabetesTable.COLUMN_NAME_TO + " >  " + current_hour + ")";

        Cursor cursor = db.rawQuery(selectQuery,null);
        int current_lenth = cursor.getCount();
        if( current_lenth > 0) {
            try {
                for (int index = 1; index <= current_lenth; index++) {
                    cursor.moveToNext();
                    String T = cursor.getString(0);
                    String val = cursor.getString(1);
                    TextView myText;
                    switch (T) {
                        case DatabaseContract.DiabetesTable.TYPE_NAME_SUGAR_TARGET:
                            myText = (TextView) findViewById(R.id.sugarTarget);
                            myText.setText(val);
                            sugarTarget = val;
                            break;
                        case DatabaseContract.DiabetesTable.TYPE_NAME_CARBO_INSULIN_RATIO:
                            myText = (TextView) findViewById(R.id.carboInsulinRatio);
                            myText.setText(val);
                            carboInsulinRatio = val;
                            break;
                        case DatabaseContract.DiabetesTable.TYPE_NAME_CORRECTION_FACTOR:
                            myText = (TextView) findViewById(R.id.correctionFactor);
                            myText.setText(val);
                            correctionFactor = val;

                            break;
                    }
                }
            }
            catch (SQLiteException e) {
                e.printStackTrace();

            }
        }
        cursor.close();
        // get patient Name and email from DB
        String ar [] = DiabetesDbHelper.getMiscData( getApplicationContext(), mDbHelper);
        PatientName = ar[0];
        PatientEmail = ar[1];

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Locale getCurrentLocale(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return getResources().getConfiguration().locale;
        }
    }
}

