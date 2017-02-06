package com.ashervardi.diabetesapp2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DiabetesGraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diabetes_graph);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Context context = getApplicationContext();
        LineChart chart = (LineChart) findViewById(R.id.chart);
        DiabetesDbHelper dataHelper=new DiabetesDbHelper(context, DiabetesDbHelper.DATABASE_NAME);
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        List<Entry> entries = new ArrayList<Entry>();

        try {
            String selectQuery = "SELECT * FROM " + DatabaseContract.DiabetesTable.SUGAR_TABLE_NAME + " ORDER BY " + DatabaseContract.DiabetesTable.COLUMN_NAME_MONTH + " DESC , " + DatabaseContract.DiabetesTable.COLUMN_NAME_DAY + " DESC";
            Cursor cursor = db.rawQuery(selectQuery, null);
            String[] db_time;

            int col = 1;
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    db_time = cursor.getString(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_TIME)).split(":");
                    entries.add(new Entry(col, Float.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_SUGAR)))));
                    col++;
                }
            }
            cursor.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        LineDataSet dataSet = new LineDataSet(entries, "Sugar level"); // add entries to dataset
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        LineData lineData = new LineData(dataSet);
        YAxis leftAxis = chart.getAxisLeft();

        LimitLine ll = new LimitLine(140f, "Normal Sugar Limits");
        ll.setLineColor(Color.RED);
        ll.setLineWidth(3f);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(12f);
// .. and more styling options

        leftAxis.addLimitLine(ll);
        ll = new LimitLine(80f, "");
        ll.setLineColor(Color.RED);
        ll.setLineWidth(3f);
        leftAxis.addLimitLine(ll);


        chart.setGridBackgroundColor(Color.LTGRAY);
        chart.setDrawBorders(true);
        chart.setData(lineData);
        chart.invalidate(); // refresh

    }

}
