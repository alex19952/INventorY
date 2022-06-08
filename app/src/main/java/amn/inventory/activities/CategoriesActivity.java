package amn.inventory.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import amn.inventory.adapters.AdapterCategoriesActivity;
import amn.inventory.R;
import amn.inventory.helpers.SQLiteHelper;

public class CategoriesActivity extends AppCompatActivity implements AdapterCategoriesActivity.OnCardClickListener {

    ActionBar toolbar;
    RecyclerView recyclerView;
    AdapterCategoriesActivity adapter;
    SQLiteHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        toolbar = getSupportActionBar();
        assert toolbar != null;
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.categories_activity_title);
        helper = new SQLiteHelper(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewListOfCategories);
        recyclerView.setLayoutManager((new LinearLayoutManager(this)));
        adapter = new AdapterCategoriesActivity(helper.getCursorForCategories(""));
        adapter.setOnCardClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Cursor cursor = helper.getCursorForCategories("");
        adapter.changeAdapter(cursor);
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
                adapter.changeAdapter(helper.getCursorForCategories(s));
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