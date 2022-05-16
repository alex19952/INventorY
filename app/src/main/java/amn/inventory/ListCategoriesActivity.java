package amn.inventory;

import android.content.Intent;
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

import java.util.ArrayList;

public class ListCategoriesActivity extends AppCompatActivity implements AdapterListCategoriesActivity.OnCardClickListener {

    RecyclerView recyclerView;
    AdapterListCategoriesActivity adapter;
    ArrayList<String> list;
    SQLiteHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_categories);

        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);

        helper = new SQLiteHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        list = helper.getListCategories(db);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewListOfCategories);
        recyclerView.setLayoutManager((new LinearLayoutManager(this)));
        adapter = new AdapterListCategoriesActivity(list);
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
                System.out.println(s);
                SQLiteDatabase db = helper.getReadableDatabase();
                adapter.categories = helper.getListCategories(db, s);
                adapter.notifyDataSetChanged();
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