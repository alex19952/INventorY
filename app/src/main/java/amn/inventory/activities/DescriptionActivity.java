package amn.inventory.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import amn.inventory.R;

public class DescriptionActivity extends AppCompatActivity {

    ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        toolbar = getSupportActionBar();
        toolbar.setTitle(R.string.description_activity_title);
        toolbar.setDisplayHomeAsUpEnabled(true);
        TextView textView = findViewById(R.id.textView);
        String str = getIntent().getStringExtra("azaza");
        textView.setText(str);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}