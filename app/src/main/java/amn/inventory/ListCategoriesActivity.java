package amn.inventory;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class ListCategoriesActivity extends AppCompatActivity  implements AdapterListCategoriesActivity.OnCardClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_categories);
        ArrayList<String> list = getListPlaces();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewListOfCategories);
        recyclerView.setLayoutManager((new LinearLayoutManager(this)));
        AdapterListCategoriesActivity adapter = new AdapterListCategoriesActivity(list);
        adapter.setOnCardClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<String> getListPlaces() {
        ArrayList<String> list = new ArrayList<String>();
        SQLiteHelper helper = new SQLiteHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(
                getString(R.string.table_name),
                new String[] {getString(R.string.column_name_PLACE)},
                null,
                null,
                getString(R.string.column_name_PLACE),
                null,
                null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return list;
    }

    @Override
    public void onCardClick(View view, String tittle) {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(getString(R.string.arg_for_scan_activity), tittle);
        startActivity(intent);
    }
}