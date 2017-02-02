package com.ashervardi.diabetesapp2;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class DisplayDiabetesDataActivity extends AppCompatActivity {
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_diabetes_data);

        context=this;
        // Create DatabaseHelper instance

        DiabetesDbHelper dataHelper=new DiabetesDbHelper(context, DiabetesDbHelper.DATABASE_NAME);
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+ DatabaseContract.DiabetesTable.SUGAR_TABLE_NAME + " ORDER BY " + DatabaseContract.DiabetesTable.COLUMN_NAME_MONTH + " DESC , " + DatabaseContract.DiabetesTable.COLUMN_NAME_DAY + " DESC";
        Cursor cursor = db.rawQuery(selectQuery,null);
        // Find ListView to populate
        final ListView lvItems = (ListView) findViewById(R.id.diabetesList);
// Setup cursor adapter using cursor from last step
        DiabetesCursorAdapter diabetesAdapter = new DiabetesCursorAdapter(this, cursor);
        // make list selectable
        lvItems.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
// Attach cursor adapter to the ListView
        lvItems.setAdapter(diabetesAdapter);

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, final View v, final int position, long id) {
                String title = getResources().getString(R.string.Dtitle);
                String msg = getResources().getString(R.string.Dmessage);
                new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(msg)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                Object o = lvItems.getItemAtPosition(position);
                                SQLiteDatabase db = ((SQLiteCursor) o).getDatabase();
                                db.delete(DatabaseContract.DiabetesTable.SUGAR_TABLE_NAME, "_ID = ? ", new String[]{Integer.toString(((SQLiteCursor) o).getInt(0))});
                                //       DiabetesCursorAdapter.changeCursor((SQLiteCursor) o);
                                v.setClickable(false);
                                v.setVisibility(View.INVISIBLE);
                        //        v.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
                                Toast.makeText(context, "Item was deleted!", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });


    }

    @Override
    public boolean  onOptionsItemSelected(MenuItem item) {

        Toast.makeText(this,
                String.valueOf(item.getTitle()),
                Toast.LENGTH_LONG).show();
        return true;
    }
}
