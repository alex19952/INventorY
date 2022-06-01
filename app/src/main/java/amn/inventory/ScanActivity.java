package amn.inventory;

import static android.widget.Toast.LENGTH_SHORT;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ScanActivity extends AppCompatActivity {

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);

        EditText MTR_codeEditText = (EditText) findViewById((R.id.MTR_codeEditText));
        MTR_codeEditText.requestFocus();
        TextView last_codeTextView = (TextView) findViewById(R.id.last_codeTextView);
        SQLiteHelper helper = new SQLiteHelper(this);
        db = helper.getReadableDatabase();
        String arg = getIntent().getStringExtra(getString(R.string.arg_for_scan_activity));
        Cursor cursor = helper.getCursorOnScaning(
                db, arg);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.MTR_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AdapterScanActivity adapter = new AdapterScanActivity(cursor);
        recyclerView.setAdapter(adapter);

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
                        if (helper.entryData(db, intSequence, arg) == 0) {
                            if (helper.entryData(db, intSequence) == 0) {
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
                        else if (helper.entryData(db, intSequence, arg) > 1) {
                            Toast toast = Toast.makeText(
                                    getApplicationContext(),
                                    getString(R.string.warning_many_id),
                                    LENGTH_SHORT);
                            toast.show();
                        }
                        else {
                            helper.incrementScanQuantity(db, intSequence);
                            Cursor cursor = helper.getCursorOnScaning(db, arg);
                            adapter.changeAdapter(cursor);
                            last_codeTextView.setText(strSequence);
                        }
                    }
                    catch  (Exception e) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                getString(R.string.warning_scan_error), LENGTH_SHORT);
                        toast.show();
                    }
                    finally {
                       MTR_codeEditText.setText("");
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        };
        MTR_codeEditText.addTextChangedListener(change1);
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
//                SQLiteDatabase db = helper.getReadableDatabase();
//                adapter.changeAdapter(helper.getCursorOnCategories(db, s));
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

}