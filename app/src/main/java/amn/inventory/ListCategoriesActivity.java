package amn.inventory;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class ListCategoriesActivity extends AppCompatActivity implements AdapterListCategoriesActivity.OnCardClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_categories);

        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);


        SQLiteHelper helper = new SQLiteHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<String> list = helper.getListCategoties(db);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewListOfCategories);
        recyclerView.setLayoutManager((new LinearLayoutManager(this)));
        AdapterListCategoriesActivity adapter = new AdapterListCategoriesActivity(list);
        adapter.setOnCardClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCardClick(View view, String tittle) {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(getString(R.string.arg_for_scan_activity), tittle);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}