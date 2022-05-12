package amn.inventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity {

    private final int PICKFILE_RESULT_CODE = 0;
    private int NUM_COLUMN_ID = 0;
    private int NUM_COLUMN_NAME = 4;
    private int NUM_COLUMN_QUANTITY = 5;
    private int NUM_COLUMN_PLACE = 7;
    private boolean divided_into_categories = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickOpenFile(View view) {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("text/comma-separated-values");
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("myLogs", "requestCode = " + requestCode + ", resultCode = " + resultCode);
        if (resultCode == RESULT_OK) {
            Uri file;
            file = data.getData();
            try {
                // ТУТ ДОЛЖНА БЫТЬ ПРОВЕРКА ФАЙЛА!!! FIXME
                createDataBase(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createDataBase(Uri uri_of_file) throws IOException {
        String address = uri_of_file.getLastPathSegment();
        address = (Environment.getExternalStorageDirectory() + "/"
                + address.substring(address.lastIndexOf(':') + 1));
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(address), getString(R.string.charset_name)));
        Scanner scanner = new Scanner(reader);
        scanner.nextLine();
        SQLiteHelper helper = new SQLiteHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        helper.clearTableData(db, this);
        String[] row;
        while (scanner.hasNext()) {
            row = scanner.nextLine().split(";", -1);
            ContentValues contentValue = new ContentValues();
            contentValue.put("ID_FOR_SEARCH", Integer.parseInt(row[NUM_COLUMN_ID]));
            contentValue.put("NAME_MTR", row[NUM_COLUMN_NAME]);
            contentValue.put("QUANTITY", Integer.parseInt(row[NUM_COLUMN_QUANTITY]));
            contentValue.put("PLACE", row[NUM_COLUMN_PLACE]);
            db.insert("MTRs", null, contentValue);
            System.out.println(row[NUM_COLUMN_PLACE]);
        }
        scanner.close();
        db.close();
        helper.close();
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

}