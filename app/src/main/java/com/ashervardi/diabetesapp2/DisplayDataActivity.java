package com.ashervardi.diabetesapp2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;



public class DisplayDataActivity extends AppCompatActivity {

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_display_data);
        Context context = this;
            // Create DatabaseHelper instance
            DiabetesDbHelper dataHelper=new DiabetesDbHelper(context, DiabetesDbHelper.DATABASE_NAME);

            // Reference to TableLayout
            TableLayout tableLayout=(TableLayout)findViewById(R.id.maintable);
            // Add header row
            TableRow rowHeader = new TableRow(context);
            rowHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));

            rowHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,0));
  //                  TableLayout.LayoutParams.WRAP_CONTENT));

            String Hdate = getResources().getString(R.string.H_date);
            String Htime = getResources().getString(R.string.H_time);
            String Hsugar = getResources().getString(R.string.H_sugar);
            String Hcarbo = getResources().getString(R.string.H_carbo);
            String Hbolus = getResources().getString(R.string.H_bolus);
            String Hcomment = getResources().getString(R.string.H_comment);
            String Hsug = getResources().getString(R.string.H_sug);

            String[] headerText={Hdate, Htime, Hsugar, Hcarbo, Hbolus,Hbolus, Hcomment};
            String[] headerText2={"   ", "    ","     ","     ", Hsug,"     ", "      "};

            for(String c:headerText) {
                TextView tv = new TextView(this);
                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(14);
                tv.setPadding(5, 5, 5, 5);
                tv.setText(c);
                rowHeader.addView(tv);

            }
            tableLayout.addView(rowHeader);
            TableRow rowHeader2 = new TableRow(context);
            rowHeader2.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
            rowHeader2.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            for(String c:headerText2) {
                TextView tv = new TextView(this);
                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(14);
                tv.setPadding(5, 5, 5, 5);
                tv.setText(c);
                rowHeader2.addView(tv);
            }
            tableLayout.addView(rowHeader2);

            // Get data from sqlite database and add them to the table
            // Open the database for reading
            SQLiteDatabase db = dataHelper.getReadableDatabase();
            // Start the transaction.
            db.beginTransaction();

            try
            {
                String selectQuery = "SELECT * FROM "+ DatabaseContract.DiabetesTable.SUGAR_TABLE_NAME + " ORDER BY " + DatabaseContract.DiabetesTable.COLUMN_NAME_MONTH + " DESC , " + DatabaseContract.DiabetesTable.COLUMN_NAME_DAY + " DESC";
                Cursor cursor = db.rawQuery(selectQuery,null);
                int bColor = ContextCompat.getColor(context, R.color.light_blue);
                int prevBcolor = ContextCompat.getColor(context, R.color.lightyellow);
                int _prevDay = 0, _prev_Month = 0;
                int tmp;
                int textColor = ContextCompat.getColor(context, R.color.black);

                if(cursor.getCount() >0)
                {


                    while (cursor.moveToNext()) {
                        // Read columns data

                        int _day = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_DAY));
                        int _month = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_MONTH));
                        String date = Integer.toString(_day) + "/" + Integer.toString(_month);
                        String time = cursor.getString(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_TIME));
                        String sugar = cursor.getString(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_SUGAR));
                        String  carbo = cursor.getString(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_CARBO));
                        String  bolus = cursor.getString(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_BOLUS));
                        String  insulin = cursor.getString(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_INSULIN));
                        String  cmnt = cursor.getString(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_COMMENT));
                        if (_day != _prevDay | _month != _prev_Month){
                            tmp = bColor;
                            bColor = prevBcolor;
                            prevBcolor = tmp;
                            _prev_Month = _month;
                            _prevDay = _day;
                        }
                        if(Integer.parseInt(sugar) < 70) {
                            textColor = ContextCompat.getColor(context, R.color.red);
                        } else {
                            textColor = ContextCompat.getColor(context, R.color.black);
                        }
                        // dara rows
                        TableRow row = new TableRow(context);
                        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));
                        String[] colText={date, time,sugar,carbo,bolus,insulin, cmnt};
                        int i = 1;
                        for(String text:colText) {
                            TextView tv = new TextView(this);
                            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT);
    //                        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
    //                                TableRow.LayoutParams.WRAP_CONTENT));
                            tv.setLayoutParams(params);
                            tv.setGravity(Gravity.CENTER);
                            tv.setTextSize(12);
                            tv.setPadding(5, 5, 5, 5);
                            tv.setText(text);
                            tv.setTextColor(textColor);
                            tv.setBackgroundColor(bColor);
                            if (i == 7){
                                tv.setWidth(200);
                                tv.setHorizontallyScrolling(false);
                                tv.setMaxLines(2);
                            }
                            row.addView(tv);
                            i++;
                        }
                        tableLayout.addView(row);

                    }
                    cursor.close();
                }

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