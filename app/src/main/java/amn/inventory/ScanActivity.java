package amn.inventory;

import static android.widget.Toast.LENGTH_SHORT;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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
        SQLiteHelper sql_helper = new SQLiteHelper(this);
        db = sql_helper.getReadableDatabase();
        ArrayList<MTR> scanned_MTRs = sql_helper.getMtrList(db,
                getIntent().getStringExtra(getString(R.string.arg_for_scan_activity)));
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.MTR_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AdapterScanActivity adapter = new AdapterScanActivity(scanned_MTRs);
        recyclerView.setAdapter(adapter);

        TextWatcher change1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String strSequence = charSequence.toString();
                if(strSequence.length() != 0 && strSequence.charAt(strSequence.length() - 1) == '\n'){
                    try {
                        int intSequence = Integer.parseInt(strSequence.replace("\n", ""));
                        MTR scanned_MTR = scanned_MTRs.stream().filter(mtr -> intSequence == mtr.id).findAny().orElse(null);
                        if (scanned_MTR == null) {
                            Toast toast = Toast.makeText(
                                    getApplicationContext(),
                                    getString(R.string.no_id),
                                    LENGTH_SHORT);
                            toast.show();
                        }
                        else {
                            scanned_MTR.IncrementCurrentQuantity();
                            if (scanned_MTR.current_quantity >= scanned_MTR.quantity) {
                                scanned_MTRs.remove(scanned_MTR);
                            } else {
                                adapter.swapItem(scanned_MTRs.indexOf(scanned_MTR), 0);
                            }
                            adapter.notifyDataSetChanged();
                            last_codeTextView.setText(strSequence);
                            MTR_codeEditText.setText("");
                        }
                    }
                    catch  (Exception e) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                getString(R.string.scan_error), LENGTH_SHORT);
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
}


