package amn.inventory;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import amn.inventory.main.AnimationLauncher;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


public class MainActivity extends AppCompatActivity {

    private boolean database_has_been_loaded;

    private int NUM_COLUMN_ID = 0;
    private int NUM_COLUMN_NAME = 4;
    private int NUM_COLUMN_QUANTITY = 5;
    private int NUM_COLUMN_PLACE = 7;
    private boolean divided_into_categories = true;
    private int tittles = 0;

    Button scan_button;
    Button load_button;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SQLiteHelper helper = new SQLiteHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        database_has_been_loaded = helper.dataBaseIsLoad(db);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        scan_button = findViewById(R.id.start_scan);
        load_button = findViewById(R.id.load_button);
        startAnimation();
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
                    int PICKFILE_RESULT_CODE = 0;
                    startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);

                } else {

                    Toast.makeText(MainActivity.this, R.string.warning_read_external_storage,
                            Toast.LENGTH_SHORT).show();
                }
//                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("myLogs", "requestCode = " + requestCode + ", resultCode = " + resultCode);
        if (resultCode == RESULT_OK) {

            try {
                // ТУТ ДОЛЖНА БЫТЬ ПРОВЕРКА ФАЙЛА!!! FIXME
                Uri file;
                file = data.getData();

                String address = file.getLastPathSegment();
                address = (Environment.getExternalStorageDirectory() + "/"
                        + address.substring(address.lastIndexOf(':') + 1));

                String[] path = address.split("\\.");
                String text_load_btn = "";
                for (int i = 0; i < path.length - 1; i++) {
                    text_load_btn += path[i];
                }
                load_button.setText(text_load_btn);
                String format = path[path.length - 1];
                if (format.equals("csv")) {
                    load_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.csv_icon, 0, R.drawable.reload_icon, 0);
                }

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(address), getString(R.string.charset_name)));
                Scanner scanner = new Scanner(reader);
                new DatabaseCreator().execute(scanner);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class DatabaseCreator extends AsyncTask <Scanner, ProgressBar, Integer> {

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        @Override
        protected void onPreExecute() {
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
        protected void onProgressUpdate(ProgressBar... values) {

        }

        @Override
        protected void onPostExecute(Integer result) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            Animation anim_scan_1 = AnimationUtils.loadAnimation(context, R.anim.anim_scan_btn_1);

            anim_scan_1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    scan_button.setVisibility(Button.VISIBLE);
                    scan_button.setEnabled(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            scan_button.startAnimation(anim_scan_1);
        }
    }


    public void onClickStartScan(View view) {
        if (divided_into_categories) {
            Intent intent = new Intent(MainActivity.this, ListCategoriesActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, ScanActivity.class);
            startActivity(intent);
        }
    }

    public void onClickSave(View view) {

        String save_file = "/storage/emulated/0/Download/База МТР/excel.xls"; // FIXME
        WritableWorkbook workbook = null;
        SQLiteHelper helper = new SQLiteHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        try {
            workbook = Workbook.createWorkbook(new File(save_file));
            MediaScannerConnection.scanFile(this, new String[]{save_file}, null, null);
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

            for (int row = 1; row < cursor.getCount() + 1; row++) {
                for (int column = 0; column < cursor.getColumnCount(); column++) {
                    Label label = new Label(column, row, cursor.getString(column));
                    sheet.addCell(label);
                }
                cursor.moveToNext();
            }
            workbook.write();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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

    public void startAnimation() {
        Animation anim_load_1 = AnimationUtils.loadAnimation(context, R.anim.anim_load_btn_1);
        Animation anim_scan_1 = AnimationUtils.loadAnimation(context, R.anim.anim_scan_btn_1);

        anim_load_1.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                load_button.setVisibility(Button.VISIBLE);
                if (database_has_been_loaded) {
                    scan_button.startAnimation(anim_scan_1);
                    scan_button.setVisibility(Button.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        load_button.startAnimation(anim_load_1);
    }

}
