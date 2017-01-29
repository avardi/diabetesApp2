package com.ashervardi.diabetesapp2;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Asher on 1/28/2017.
 */

public class DiabetesCursorAdapter extends CursorAdapter{
    static int _prevDay = 0, _prev_Month = 0;
    static int prevBcolor = 0;
    static int bColor = 0;

    public DiabetesCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.table_row, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if(bColor == 0) bColor = ContextCompat.getColor(context, R.color.light_blue);
        if(prevBcolor == 0)  prevBcolor = ContextCompat.getColor(context, R.color.light_yellow);
        /*
        int prevBcolor = ContextCompat.getColor(context, R.color.lightgreen);
        int _prevDay = 0, _prev_Month = 0;
        */
        int tmp;
        int textColor = ContextCompat.getColor(context, R.color.black);
        // Find fields to populate in inflated template
        TextView tvdate = (TextView) view.findViewById(R.id.tvdate);
        TextView tvtime = (TextView) view.findViewById(R.id.tvtime);
        TextView tvsugar = (TextView) view.findViewById(R.id.tvsugar);
        TextView tvcarbo = (TextView) view.findViewById(R.id.tvcarbo);
        TextView tvbolusS = (TextView) view.findViewById(R.id.tvbolusS);
        TextView tvbolus = (TextView) view.findViewById(R.id.tvbolus);
        TextView tvcomment = (TextView) view.findViewById(R.id.tvcomment);
        // Extract properties from cursor
        int month = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.DiabetesTable.COLUMN_NAME_MONTH));
        int day = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.DiabetesTable.COLUMN_NAME_DAY));
        String time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DiabetesTable.COLUMN_NAME_TIME));
        String sugar = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DiabetesTable.COLUMN_NAME_SUGAR));
        String carbo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DiabetesTable.COLUMN_NAME_CARBO));
        String bolusS = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DiabetesTable.COLUMN_NAME_BOLUS));
        String bolus = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DiabetesTable.COLUMN_NAME_INSULIN));
        String cmnt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DiabetesTable.COLUMN_NAME_COMMENT));

        if (day != _prevDay | month != _prev_Month){
            tmp = bColor;
            bColor = prevBcolor;
            prevBcolor = tmp;
            _prev_Month = month;
            _prevDay = day;
        }
        if(Integer.parseInt(sugar) < 70) {
            textColor = ContextCompat.getColor(context, R.color.red);
        } else {
            textColor = ContextCompat.getColor(context, R.color.black);
        }
        // Populate fields with extracted properties

/*
        tvdate.setText(Integer.toString(day) + "/" + Integer.toString(month));
        tvtime.setText(time);
        tvsugar.setText(sugar);
        tvcarbo.setText(carbo);
        tvbolusS.setText(bolusS);
        tvbolus.setText(bolus);
        tvcomment.setText(cmnt);

   */
        setP(tvdate, Integer.toString(day) + "/" + Integer.toString(month), textColor, bColor  );
        setP(tvtime, time, textColor, bColor  );
        setP(tvsugar, sugar, textColor, bColor  );
        setP(tvcarbo, carbo, textColor, bColor  );
        setP(tvbolusS, bolusS, textColor, bColor  );
        setP(tvbolus, bolus, textColor, bColor  );
        setP(tvcomment, cmnt, textColor, bColor  );
    }

    private void setP (TextView tv, String text, int textColor, int bcolor ) {
        tv.setText(text);
        tv.setTextColor(textColor);
        tv.setBackgroundColor(bcolor);

    }
}
