//package amn.inventory.main;
//
//import android.database.sqlite.SQLiteDatabase;
//import android.os.AsyncTask;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.Button;
//import android.widget.ProgressBar;
//
//import java.util.Scanner;
//
//import amn.inventory.MainActivity;
//import amn.inventory.R;
//import amn.inventory.SQLiteHelper;
//
//public class DatabaseCreator extends AsyncTask<Scanner, ProgressBar, Integer> {
//
//    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
//
//    @Override
//    protected void onPreExecute() {
//        progressBar.setVisibility(ProgressBar.VISIBLE);
//    }
//
//    @Override
//    protected Integer doInBackground(Scanner... scanners) {
//        Scanner scanner = scanners[0];
//        SQLiteHelper helper = new SQLiteHelper(MainActivity.this);
//        SQLiteDatabase db = helper.getReadableDatabase();
//
//        helper.deleteTableData(db);
//        helper.deleteTableTittles(db);
//
//        helper.createTableData(db, true, 0);
//        helper.createTableTittles(db);
//
//
//        for (int i = 0; i < tittles; i++) {
//            scanner.nextLine();
//        }
//        helper.putValuesInTableTittles(db, scanner);
//        helper.putValuesInTableData(db, scanner, NUM_COLUMN_ID, NUM_COLUMN_NAME,
//                NUM_COLUMN_QUANTITY, NUM_COLUMN_PLACE, null);
//
//        scanner.close();
//        db.close();
//        helper.close();
//
//        return 0;
//    }
//
//    @Override
//    protected void onProgressUpdate(ProgressBar... values) {
//
//    }
//
//    @Override
//    protected void onPostExecute(Integer result) {
//        progressBar.setVisibility(ProgressBar.INVISIBLE);
//        Animation anim_scan_1 = AnimationUtils.loadAnimation(context, R.anim.anim_scan_btn_1);
//
//        anim_scan_1.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                scan_button.setVisibility(Button.VISIBLE);
//                scan_button.setEnabled(true);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
//        scan_button.startAnimation(anim_scan_1);
//    }
//}
