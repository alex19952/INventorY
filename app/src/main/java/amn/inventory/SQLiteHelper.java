package amn.inventory;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.Scanner;

class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Inventory";
    private static final int DB_VERSION = 1;

    private static final String TABLE_SETTINGS_NAME = "Settings";


    private static final String TABLE_DATA_NAME = "MTRs";
    private static final String ID_FOR_SEARCH = "ID_FOR_SEARCH";
    private static final String NAME_MTR = "NAME_MTR";
    private static final String QUANTITY = "QUANTITY";
    private static final String SCANNED_QUANTITY = "SCANNED_QUANTITY";
    private static final String CATEGORY = "CATEGORY";
    private static final String DESCRIPTION = "DESCRIPTION";

    private static final String TABLE_TITTLES_NAME = "TITTLES";
    private static final String TITTLE = "TITTLE";

    Context context;

    SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    // data ////////////////////////////////////////////////////////////////////////////////////////

    public void createTableData (SQLiteDatabase db,
                                 boolean use_category ,
                                 int quantity_optional_columns) {
        String sql_command =
                        "CREATE TABLE " + TABLE_DATA_NAME
                        + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + ID_FOR_SEARCH +       " INTEGER, "
                        + NAME_MTR +            " TEXT, "
                        + QUANTITY +            " INTEGER, "
                        + SCANNED_QUANTITY +    " INTEGER";
        if (use_category) {
            sql_command += ", " + CATEGORY + " TEXT";
        }
        for (int i = 0; i < quantity_optional_columns; i++) {
            sql_command += ", " + DESCRIPTION + Integer.toString(i) + " TEXT";
        }
        sql_command += ");";
        db.execSQL(sql_command);
    }

    public void putValuesInTableData(SQLiteDatabase db, Scanner scanner,
                                     int num_column_id,
                                     int num_column_name,
                                     int num_column_quantity,
                                     Integer num_column_category,
                                     Integer[] numbs_optionals_columns) {

        String[] row;
        while (scanner.hasNext()) {
            row = scanner.nextLine().split(";", -1);
            ContentValues contentValue = new ContentValues();
            contentValue.put(ID_FOR_SEARCH, Integer.parseInt(row[num_column_id]));
            contentValue.put(NAME_MTR, row[num_column_name].trim());
            contentValue.put(QUANTITY, Integer.parseInt(row[num_column_quantity]));
            contentValue.put(SCANNED_QUANTITY, 0);
            if (num_column_category != null) {
                String str = row[num_column_category].trim();
                if (str.equals("")) {
                    str = "Неизвестная категория";
                }
                contentValue.put(CATEGORY, str);
            }
            int count = 0;
            if (numbs_optionals_columns != null) {
                for (Integer num_column : numbs_optionals_columns) {
                    contentValue.put(DESCRIPTION + Integer.toString(count++), row[num_column].trim());
                }
            }
            db.insert(TABLE_DATA_NAME, null, contentValue);
        }
    }

    public void incrementScanQuantity(SQLiteDatabase db, int id) {
        try {
            String sql_command = "UPDATE " + TABLE_DATA_NAME + " set " + SCANNED_QUANTITY + "=" + SCANNED_QUANTITY + "+1 where " + ID_FOR_SEARCH + "=" + String.valueOf(id);
            db.execSQL(sql_command);
        }
        catch (Exception e) {
            Toast toast = Toast.makeText(context, e.toString(), LENGTH_SHORT);
            toast.show();
        }
    }

    public Cursor getDataFromDataTable(SQLiteDatabase db) {
        return db.query(TABLE_DATA_NAME, new String[] {"*"},null, null, null, null, null);
    }

    public int entryData(SQLiteDatabase db, int id, String category) {
        // returns the number of occurrences of rows with the passed id
        Cursor cursor = db.query(TABLE_DATA_NAME, new String[] {ID_FOR_SEARCH},
                ID_FOR_SEARCH + "=?" +
                        " AND " + CATEGORY + "=?", new String[] {String.valueOf(id), category},
                null, null, null);
        return cursor.getCount();
    }

    public int entryData(SQLiteDatabase db, int id) {
        // returns the number of occurrences of rows with the passed id
        Cursor cursor = db.query(TABLE_DATA_NAME, new String[] {ID_FOR_SEARCH},
                ID_FOR_SEARCH + "=?", new String[] {String.valueOf(id)},
                null, null, null);
        return cursor.getCount();
    }

    public Cursor getCursorForCategories(SQLiteDatabase db, String filter) {
        return db.query(
                TABLE_DATA_NAME,
                new String[] {"_id", CATEGORY, "COUNT(*) as count"},
                "CATEGORY LIKE " + "'%" + filter + "%'",
                null,
                CATEGORY,
                null,
                null);
    }

    public Cursor getCursorForScanning(SQLiteDatabase db, String selectionArg) {
        if (selectionArg == null) {
            return db.query(TABLE_DATA_NAME, new String[] {"_id", ID_FOR_SEARCH, NAME_MTR, QUANTITY, SCANNED_QUANTITY},
                    null, null, null, null, null);
        }
        else {
            return db.query(TABLE_DATA_NAME, new String[] {"_id", ID_FOR_SEARCH, NAME_MTR, QUANTITY, SCANNED_QUANTITY},
                    CATEGORY + "=?", new String[] {selectionArg}, null, null, null);
        }
    }

    public String getTittle(SQLiteDatabase db, int id) {
        Cursor cursor = db.query(TABLE_DATA_NAME, new String[] {NAME_MTR},
                ID_FOR_SEARCH + "=?", new String[] {String.valueOf(id)},
                null, null, null);
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    public boolean dataBaseIsLoad (SQLiteDatabase db) {
        try {
            db.query(TABLE_DATA_NAME, new String[]{"*"}, null, null,
                    null, null, null);
            return true;
        } catch (SQLiteException e) {
            return false;
        }
    }

    public void deleteTableData(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_NAME + ";");
    }



    // tittles /////////////////////////////////////////////////////////////////////////////////////

    public void createTableTittles(SQLiteDatabase db) {
        String sql_command =
                "CREATE TABLE " + TABLE_TITTLES_NAME
                        + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + TITTLE + " TEXT);";
        db.execSQL(sql_command);
    }

    public void putValuesInTableTittles(SQLiteDatabase db, Scanner scanner) {
        String[] row;
        row = scanner.nextLine().split(";", -1);
        ContentValues contentValue = new ContentValues();
        for (String tittle:row) {
            contentValue.put(TITTLE, tittle);
            db.insert(TABLE_TITTLES_NAME, null, contentValue);
        }
    }

    public Cursor getDataFromTittlesTable(SQLiteDatabase db) {
        return db.query(TABLE_TITTLES_NAME, new String[] {TITTLE}, null, null,
                null, null, null);
    }

    public void deleteTableTittles(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TITTLES_NAME + ";");
    }


    // settings ////////////////////////////////////////////////////////////////////////////////////

    public void createTableSettings () {
        String sql_command =
                "CREATE TABLE " + TABLE_TITTLES_NAME
                        + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + TITTLE + " TEXT);";
        db.execSQL(sql_command);
    }

    public void putValuesInTableSettings () {

    }

    public void deleteTableSettings () {

    }


}