package amn.inventory.activities;

import static amn.inventory.R.drawable.unformat_icon;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import amn.inventory.R;
import amn.inventory.helpers.SQLiteHelper;
import amn.inventory.helpers.SettingsHelper;


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

    SQLiteHelper helper;

    SharedPreferences preferences;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(SettingsHelper.settings_name, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(SettingsHelper.use_category, true).commit(); // FIXME: 08.06.2022 

        helper = new SQLiteHelper(this);

        database_has_been_loaded = helper.isDataBaseLoad();

        context = this;
        load_button = findViewById(R.id.load_button);
        if (database_has_been_loaded){
            load_button.setText(preferences.getString(SettingsHelper.file_path, getString(R.string.load_file_btn_default)));
            load_button.setCompoundDrawablesWithIntrinsicBounds(
                    preferences.getInt(SettingsHelper.file_format_drwbl, unformat_icon),
                    0,
                    R.drawable.reload_icon,
                    0);
        }

        scan_button = findViewById(R.id.start_scan);

        startAnimation();
    }

    public void onClickOpenFile(View view) {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
                return;
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
                preferences.edit().putString(SettingsHelper.file_path, text_load_btn).commit();
                String format = path[path.length - 1];
                if (format.equals("csv")) {
                    load_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.csv_icon, 0, R.drawable.reload_icon, 0);
                    preferences.edit().putInt(SettingsHelper.file_format_drwbl, R.drawable.csv_icon).commit();
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
            if (database_has_been_loaded) {
                scan_button.setEnabled(false);
                Animation scan_anim_2 = AnimationUtils.loadAnimation(context, R.anim.anim_scan_btn_2);
                scan_button.startAnimation(scan_anim_2);
                scan_button.setVisibility(Button.INVISIBLE);
                database_has_been_loaded = false;
            }
        }

        @Override
        protected Integer doInBackground(Scanner... scanners) {
            Scanner scanner = scanners[0];

            helper.deleteTables();

            helper.createTables(true, 0);

            for (int i = 0; i < tittles; i++) {
                scanner.nextLine();
            }
            helper.putValuesInTableTittles(scanner);
            helper.putValuesInDataTable(scanner, NUM_COLUMN_ID, NUM_COLUMN_NAME,
                    NUM_COLUMN_QUANTITY, NUM_COLUMN_PLACE, null);
            helper.putValuesInCategoriesTable();

            scanner.close();

            return 0;
        }

        @Override
        protected void onProgressUpdate(ProgressBar... values) {

        }

        @Override
        protected void onPostExecute(Integer result) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            load_button.setText(preferences.getString(SettingsHelper.file_path, getString(R.string.load_file_btn_default)));
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
            database_has_been_loaded = true;
        }
    }

    public void onClickStartScan(View view) {
        Intent intent;
        if (divided_into_categories) {
            intent = new Intent(MainActivity.this, CategoriesActivity.class);
        } else {
            intent = new Intent(MainActivity.this, ScanActivity.class);
        }
        startActivity(intent);
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

    public void onClickSettings (View view){
        Intent intent;
        intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }

}
