package amn.inventory.activities;

import static android.telephony.MbmsDownloadSession.RESULT_CANCELLED;
import static android.widget.Toast.LENGTH_SHORT;

import static amn.inventory.R.drawable.unformat_icon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.ObjectUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import amn.inventory.adapters.AdapterScanActivity;
import amn.inventory.R;
import amn.inventory.helpers.DatabaseStructure;
import amn.inventory.helpers.SQLiteHelper;
import amn.inventory.helpers.SettingsHelper;
import amn.inventory.model.ISaveHelper;
import amn.inventory.model.SaveCSV;

public class ScanActivity extends AppCompatActivity implements AdapterScanActivity.OnCardClickListener {

    ActionBar toolbar;
    TextView lastCodeView;
    TextView editTextView;
    RecyclerView recyclerView;
    SQLiteHelper helper;
    AdapterScanActivity adapter;
    String category_name;
    SharedPreferences preferences;
    ISaveHelper saveHelper;
    private static final int REQUEST_CODE_SCAN = 0;
    private static final int REQUEST_CODE_CREATE_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        preferences = getSharedPreferences(SettingsHelper.settings_name, Context.MODE_PRIVATE);

        category_name = getIntent().getStringExtra(getString(R.string.arg_for_scan_activity));

        toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(category_name);

        editTextView = (EditText) findViewById((R.id.MTR_codeEditText));
        editTextView.requestFocus();

        lastCodeView = (TextView) findViewById(R.id.last_codeTextView);

        helper = new SQLiteHelper(this);
        Cursor cursor = helper.getCursorForScanning(category_name);

        recyclerView = (RecyclerView) findViewById(R.id.MTR_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdapterScanActivity(cursor);
        adapter.setOnCardClickListener(this);
        recyclerView.setAdapter(adapter);

        saveHelper = new SaveCSV(helper);

        TextWatcher change1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String strSequence = charSequence.toString();
                if(strSequence.length() != 0 && strSequence.charAt(strSequence.length() - 1) == '\n'){
                    try {
                        strSequence = strSequence.substring(0, strSequence.length() - 1);
                        int intSequence = Integer.parseInt(strSequence);
                        if (helper.entryData(intSequence, category_name) == 0) {
                            if (helper.entryData(intSequence) == 0) {
                                Toast toast = Toast.makeText(
                                        getApplicationContext(),
                                        getString(R.string.warning_no_id),
                                        LENGTH_SHORT);
                                toast.show();
                            } else {
                                Toast toast = Toast.makeText(
                                        getApplicationContext(),
                                        getString(R.string.warning_other_category),
                                        LENGTH_SHORT);
                                toast.show();
                            }
                        }
                        else if (helper.entryData(intSequence, category_name) > 1) {
                            Toast toast = Toast.makeText(
                                    getApplicationContext(),
                                    getString(R.string.warning_many_id),
                                    LENGTH_SHORT);
                            toast.show();
                        }
                        else {
                            helper.changeScanResult(intSequence);
                            Cursor cursor = helper.getCursorForScanning(category_name);
                            adapter.changeAdapter(cursor);
                            lastCodeView.setText(helper.getTittle(intSequence));
                            lastCodeView.setSelected(true);
                        }
                    }
                    catch  (Exception e) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                getString(R.string.warning_scan_error), LENGTH_SHORT);
                        toast.show();
                    }
                    finally {
                       editTextView.setText("");
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        };
        editTextView.addTextChangedListener(change1);
    }

    @Override
    public void onCardClick(View view, String id) {
        Intent intent = new Intent(this, DescriptionActivity.class);
        intent.putExtra("azaza", id);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        getSupportActionBar().setElevation(0);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setElevation(0);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
//                SQLiteDatabase db = helper.getReadableDatabase(); fixme
//                adapter.changeAdapter(helper.getCursorOnCategories(db, s)); fixme
                return false;
            }
        });

        MenuItem saveItem = menu.findItem(R.id.save_button);
        saveItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.setType("text/csv");
                intent.putExtra(Intent.EXTRA_TITLE, "result.csv");
                startActivityForResult(intent, REQUEST_CODE_CREATE_FILE);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void onScanCamera(View view) {
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SCAN) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                contents += "\n";
                editTextView.setText(contents);
            } else if (resultCode == RESULT_CANCELED) {
                // Обработка отмены сканирования
            }

        } else if (requestCode == REQUEST_CODE_CREATE_FILE) {

            if (resultCode == RESULT_OK) {
                if (data != null && data.getData() != null) {
                    Uri uri = data.getData();
                    try {
                        OutputStream output = getContentResolver().openOutputStream(uri);
                        Writer writer = new OutputStreamWriter(output, Charset.forName("CP1251"));
                        writer.write(saveHelper.getCSV());
                        writer.flush();
                        writer.close();
                        Toast.makeText(this, "Файл успешно сохранен", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Ошибка сохранения файла", Toast.LENGTH_SHORT).show();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    // Обработка отмены сохранения файла
                }
            }
        }
    }

    @Override
    protected void onPause() {
        Boolean use_category = preferences.getBoolean(SettingsHelper.use_category, false    );
        if (use_category) {
            Boolean complete = null;
            Boolean wrong = null; // FIXME: 08.06.2022 
            Cursor cursor = helper.getCursorForScanning(category_name);
            while (cursor.moveToNext()) {
                Integer index = cursor.getColumnIndex(DatabaseStructure.DataTable.Columns.result);
                Integer result = cursor.getInt(index);
                if (result <= 0) {
                    complete = true;
                    if (result < 0) {
                        wrong = true; // FIXME: 08.06.2022 
                    }
                }
                else {
                    complete = false;
                }
            }
//            if (complete != null) {
//                helper.setComplete(category_name, complete);
//            }
//            else {
//                Toast.makeText(this, R.string.onPause_wrong,
//                        Toast.LENGTH_SHORT).show();
//            }
//            if (wrong != null) {
//                helper.setWrong(category_name, wrong);
//            }
//            else {
//                Toast.makeText(this, R.string.onPause_wrong,
//                        Toast.LENGTH_SHORT).show();
//            }
        }
        super.onPause();
    }

}