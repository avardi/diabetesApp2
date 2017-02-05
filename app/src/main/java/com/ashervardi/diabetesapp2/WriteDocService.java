package com.ashervardi.diabetesapp2;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;




public class WriteDocService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10,
            Font.NORMAL, BaseColor.RED);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    private LocalBroadcastManager localBroadcastManager;
    static final String SERVICE_RESULT = "com.service.result";
    static final String SERVICE_MESSAGE = "com.service.message";

    @Override
    public void onCreate() {
        super.onCreate();

        // Other stuff

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    public WriteDocService() {
        super("writeDocService");

    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();
        Context mContext = this;
        String fName = workIntent.getStringExtra("patientName");



        // Do work here, based on the contents of dataString
        try {
            createPdf(mContext,fName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void sendResult(String message) {
        Intent intent = new Intent(SERVICE_RESULT);
        if(message != null)
            intent.putExtra(SERVICE_MESSAGE, message);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void createPdf(Context mContext, String PatientName) throws FileNotFoundException, DocumentException {
        try {

            File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), "pdfdemo");
            if (!pdfFolder.exists()) {
                pdfFolder.mkdir();
                Log.i("my_Tag", "Pdf Directory created");
            }

            //Create time stamp
            Date date = new Date() ;
 //           String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

//            File myFile = new File(pdfFolder + "/diab" + timeStamp + ".pdf");
            File myFile = new File(pdfFolder + "/sugarReport.pdf");

            if(myFile.exists()) {
                myFile.delete();
            }

            OutputStream output = new FileOutputStream(myFile);

            //Step 1
            Document document = new Document();

            //Step 2
            PdfWriter.getInstance(document, output);

            //Step 3
            document.open();

            //Step 4 Add content
            addTitlePage(document, PatientName);
            DiabetesDbHelper dataHelper=new DiabetesDbHelper(mContext, DiabetesDbHelper.DATABASE_NAME);

            Paragraph TblParagraph = new Paragraph("  ");
            createTable(TblParagraph ,dataHelper);

            document.add(TblParagraph);
            //Step 5: Close the document
            document.close();

            // Send report to e-mail address
            sendResult("Done");
//            SendEmail(pdfFolder, myFile.getName());


        } catch (Exception e) {
            e.printStackTrace();
            sendResult("Error");
        }

    }
    private void addTitlePage(Document document, String PatientName)
            throws DocumentException {
        Paragraph preface = new Paragraph();
        // We add one empty line
        addEmptyLine(preface, 1);
        // Lets write a big header
        preface.add(new Paragraph("Sugar Level Report for: " + PatientName, catFont));

        addEmptyLine(preface, 1);
        // Will create: Report generated by: _name, _date
        preface.add(new Paragraph(" Creation date: " +
                new Date(), smallBold));
        document.add(preface);
        // Start a new page
 //       document.newPage();
    }


    private static void createList(Section subCatPart) {
        List list = new List(true, false, 10);
        list.add(new ListItem("First point"));
        list.add(new ListItem("Second point"));
        list.add(new ListItem("Third point"));
        subCatPart.add(list);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private void createTable(Paragraph p, DiabetesDbHelper dbHelp)
            throws BadElementException {
        PdfPTable table = new PdfPTable(6);

        // t.setBorderColor(BaseColor.GRAY);
        // t.setPadding(4);
        // t.setSpacing(4);
        // t.setBorderWidth(1);
        Font f = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.WHITE);
        Font f1 = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
        Font f2 = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.RED);

        String Hdate = "Date";
        String Htime =  "Time";
        String Hsugar = "Sugar";
        String Hcarbo = "Carbo";
        String Hbolus = "Bolus";
        String Hcomment = "Comment";


        BaseColor Bcolor = BaseColor.DARK_GRAY;
        PdfPCell c1 = new PdfPCell(new Phrase(Hdate, f));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(Bcolor);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(Htime, f));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(Bcolor);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(Hsugar,f));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(Bcolor);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(Hcarbo,f));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(Bcolor);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(Hbolus, f));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(Bcolor);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(Hcomment,f));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(Bcolor);
        table.addCell(c1);
        table.setHeaderRows(1);

        // read data from SQLLite

        SQLiteDatabase db = dbHelp.getReadableDatabase();
        // Start the transaction.
        db.beginTransaction();
        try
        {



            String selectQuery = "SELECT * FROM "+ DatabaseContract.DiabetesTable.SUGAR_TABLE_NAME + " ORDER BY " + DatabaseContract.DiabetesTable.COLUMN_NAME_MONTH + " DESC , " + DatabaseContract.DiabetesTable.COLUMN_NAME_DAY + " DESC";
            Cursor cursor = db.rawQuery(selectQuery,null);

            if(cursor.getCount() >0)
            {
                int last_month = 0;
                int last_day = 0;
                BaseColor color = BaseColor.LIGHT_GRAY;
                while (cursor.moveToNext()) {
                    // Read columns data
                    int month = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_MONTH));
                    int day = cursor.getInt(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_DAY));
                    String date = Integer.toString(day) + "/" + Integer.toString(month);
                    String time = cursor.getString(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_TIME));
                    String sugar = cursor.getString(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_SUGAR));
                    String  carbo = cursor.getString(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_CARBO));
                    String  insulin = cursor.getString(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_INSULIN));
                    String  cmnt = cursor.getString(cursor.getColumnIndex(DatabaseContract.DiabetesTable.COLUMN_NAME_COMMENT));

                    if (last_month != month || last_day != day)  color = changeColor(color);
                    Font ff = f1;
                    if (Integer.parseInt(sugar) < 70 ) ff = f2;


                    // dara rows
                    PdfPCell cell = new PdfPCell(new Phrase(date, ff));
                    cell.setBackgroundColor(color);
                    table.addCell(cell);
                    cell = new PdfPCell(new Phrase(time, ff));
                    cell.setBackgroundColor(color);
                    table.addCell(cell);
                    cell = new PdfPCell(new Phrase(sugar, ff));
                    cell.setBackgroundColor(color);
                    table.addCell(cell);
                    cell = new PdfPCell(new Phrase(carbo, ff));
                    cell.setBackgroundColor(color);
                    table.addCell(cell);
                    cell = new PdfPCell(new Phrase(insulin, ff));
                    cell.setBackgroundColor(color);
                    table.addCell(cell);
                    cell = new PdfPCell(new Phrase(cmnt, ff));
                    cell.setBackgroundColor(color);
                    table.addCell(cell);
                    last_day = day;
                    last_month = month;

                }
                cursor.close();
                p.add(table);
            }
            p.add(new Paragraph(
                    " This document was generated automatically by DiabetesConpaninon.",
                    redFont));

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
    static BaseColor changeColor(BaseColor currentColor) {
        if (currentColor == BaseColor.LIGHT_GRAY) {
            return BaseColor.WHITE;
        } else return BaseColor.LIGHT_GRAY;
    }


    private void SendEmail(File dir, String file_name) {

            Intent i = new Intent(Intent.ACTION_SENDTO);

            String uriText =
                    "mailto:ashervardi@gmail.com" +
                            "?subject=" + Uri.encode("Diabetes Report") +
                            "&body=" + Uri.encode("Attached is the report sent to you by DiabetesApp. ");

            Uri uri = Uri.parse(uriText);


            i.setData(uri);

            File filelocation = new File(dir + "/" + file_name);

//            File filelocation = new File("/storage/sdcard0/DCIM/Camera/20150228_102414.jpg");

            Uri path = Uri.fromFile(filelocation);
            i.putExtra(Intent.EXTRA_STREAM, path);
            Toast.makeText(this, path.toString() + " was mailed to you", Toast.LENGTH_LONG).show();

//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
    }


}