package amn.inventory;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

public class ListCategoriesActivity extends AppCompatActivity implements AdapterListCategoriesActivity.OnCardClickListener {

    RecyclerView recyclerView;
    AdapterListCategoriesActivity adapter;
    SQLiteHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_categories);
        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);
        helper = new SQLiteHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewListOfCategories);
        recyclerView.setLayoutManager((new LinearLayoutManager(this)));
        adapter = new AdapterListCategoriesActivity(helper.getCursorOnCategories(db, ""));
        adapter.setOnCardClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                SQLiteDatabase db = helper.getReadableDatabase();
                adapter.changeAdapter(helper.getCursorOnCategories(db, s));
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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