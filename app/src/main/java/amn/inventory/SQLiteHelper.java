package amn.inventory;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import java.util.Scanner;

class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Inventory";
    private static final int DB_VERSION = 1;
    private static final String TABLE_DATA_NAME = "MTRs";
    private static final String TABLE_SETTINGS_NAME = "Settings";

    private static final String ID_FOR_SEARCH = "ID_FOR_SEARCH";
    private static final String NAME_MTR = "NAME_MTR";
    private static final String QUANTITY = "QUANTITY";
    private static final String SCANNED_QUANTITY = "SCANNED_QUANTITY";
    private static final String CATEGORY = "CATEGORY";
    private static final String DESCRIPTION = "DESCRIPTION";

    private String ADDITIONAL_COLUMNS = ", PLACE TEXT";

    SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

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
            contentValue.put(NAME_MTR, row[num_column_name]);
            contentValue.put(QUANTITY, Integer.parseInt(row[num_column_quantity]));
            if (num_column_category != null) {
                String x = row[num_column_category];
                contentValue.put(CATEGORY, row[num_column_category]);
            }
            int count = 0;
            if (numbs_optionals_columns != null) {
                for (Integer num_column : numbs_optionals_columns) {
                    contentValue.put(DESCRIPTION + Integer.toString(count++), row[num_column]);
                }
            }
            db.insert(TABLE_DATA_NAME, null, contentValue);
        }
        scanner.close();
    }

    public Cursor getCursorOnCategories (SQLiteDatabase db, String filter) {
        return db.query(
                TABLE_DATA_NAME,
                new String[] {"_id", CATEGORY, "COUNT(*) as count"},
                "CATEGORY LIKE " + "'%" + filter + "%'",
                null,
                CATEGORY,
                null,
                null);
    }

    public Cursor getCursorOnScaning(SQLiteDatabase db, String selectionArg) {
        if (selectionArg == null) {
            return db.query(TABLE_DATA_NAME, new String[] {"_id", ID_FOR_SEARCH, NAME_MTR, QUANTITY, SCANNED_QUANTITY},
                    null, null, null, null, null);
        }
        else {
            Cursor cursor = db.query(TABLE_DATA_NAME, new String[] {"_id", ID_FOR_SEARCH, NAME_MTR, QUANTITY, SCANNED_QUANTITY},
                    CATEGORY + "=?", new String[] {selectionArg}, null, null, null);
            return cursor;
        }
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

    public void incrementScanQuantity(SQLiteDatabase db, int id) {
        String sql_command = "UPDATE " + TABLE_DATA_NAME + " set " + SCANNED_QUANTITY + "=" + SCANNED_QUANTITY + "+1 where " + ID_FOR_SEARCH + "=" + String.valueOf(id);
        db.execSQL(sql_command);
    }

    public void deleteTableData(SQLiteDatabase db, Context context) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_NAME + ";");
    }

    public void createTableSettings () {

    }

    public void putValuesInTableSettings () {

    }

    public void deleteTableSettings () {

    }

}