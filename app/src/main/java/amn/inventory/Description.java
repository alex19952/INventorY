package amn.inventory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Description extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        TextView textView = findViewById(R.id.textView);
        int inn = getIntent().getIntExtra("azaza", 1);
        String str = String.valueOf(inn);
        textView.setText(str);
    }
}