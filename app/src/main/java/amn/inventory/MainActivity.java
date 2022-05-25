package amn.inventory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


public class MainActivity extends AppCompatActivity {

    private final int PICKFILE_RESULT_CODE = 0;
    private int NUM_COLUMN_ID = 0;
    private int NUM_COLUMN_NAME = 4;
    private int NUM_COLUMN_QUANTITY = 5;
    private int NUM_COLUMN_PLACE = 7;
    private boolean divided_into_categories = true;
    private int tittles = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickOpenFile(View view) {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseFile.setType("text/comma-separated-values");
                    chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                    chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                    startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);

                } else {

                    Toast.makeText(MainActivity.this, R.string.warning_read_external_storage,
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("myLogs", "requestCode = " + requestCode + ", resultCode = " + resultCode);
        if (resultCode == RESULT_OK) {

            Uri file;
            file = data.getData();

            TextView textView = (TextView) findViewById(R.id.textFilePath);
            textView.setText(file.getLastPathSegment());



            try {
                // ТУТ ДОЛЖНА БЫТЬ ПРОВЕРКА ФАЙЛА!!! FIXME


                String address = file.getLastPathSegment();
                address = (Environment.getExternalStorageDirectory() + "/"
                        + address.substring(address.lastIndexOf(':') + 1));

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(address), getString(R.string.charset_name)));

                Scanner scanner = new Scanner(reader);

//                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
//                progressBar.setVisibility(ProgressBar.VISIBLE);
                new ВatabaseСreator().execute(scanner);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ВatabaseСreator extends AsyncTask <Scanner, ProgressBar, Integer> implements amn.inventory.cteateDataBase {

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        @Override
        protected void onPreExecute () {
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Scanner... scanners) {
            Scanner scanner = scanners[0];
            SQLiteHelper helper = new SQLiteHelper(MainActivity.this);
            SQLiteDatabase db = helper.getReadableDatabase();

            helper.deleteTableData(db);
            helper.deleteTableTittles(db);

            helper.createTableData(db, true, 0);
            helper.createTableTittles(db);


            for (int i = 0; i < tittles; i++) {
                scanner.nextLine();
            }
            helper.putValuesInTableTittles(db, scanner);
            helper.putValuesInTableData(db, scanner, NUM_COLUMN_ID, NUM_COLUMN_NAME,
                    NUM_COLUMN_QUANTITY, NUM_COLUMN_PLACE, null);

            scanner.close();
            db.close();
            helper.close();

            return 0;
        }

        @Override
        protected void onProgressUpdate (ProgressBar... values) {

        }

        @Override
        protected void onPostExecute (Integer result) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
        }
    }

    public void onClickStartScan(View view) {
        if (divided_into_categories) {
            Intent intent = new Intent(MainActivity.this, ListCategoriesActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(MainActivity.this, ScanActivity.class);
            startActivity(intent);
        }
    }

    public void onClickSave (View view) {
        String save_file = "/storage/emulated/0/Download/База МТР/excel.xls"; // FIXME
        WritableWorkbook workbook = null;
        SQLiteHelper helper = new SQLiteHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();


        try {
            workbook = Workbook.createWorkbook(new File(save_file));
            MediaScannerConnection.scanFile(this, new String[] { save_file }, null, null);
            WritableSheet sheet = workbook.createSheet("Инвентаризация", 0);


            Cursor cursor = helper.getDataFromTittlesTable(db);
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                Label label = new Label(i, 0, cursor.getString(0));
                sheet.addCell(label);
                cursor.moveToNext();
            }
            cursor.close();


            cursor = helper.getDataFromDataTable(db);
            cursor.moveToFirst();

            for (int row = 1; row < cursor.getCount() + 1; row++) { // FIXME если будешь менять не заьудь про +1
                for (int column = 0; column < cursor.getColumnCount(); column++) {
                    Label label = new Label (column, row, cursor.getString(column));
                    sheet.addCell(label);
                }
                cursor.moveToNext();
            }
            workbook.write();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (WriteException e) {
        e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            db.close();
            helper.close();
            if (workbook != null) {
                try {
                    workbook.close();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}