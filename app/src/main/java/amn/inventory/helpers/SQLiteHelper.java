package amn.inventory.helpers;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.Scanner;

import amn.inventory.helpers.DatabaseStructure;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "inventory";
    public static final int DB_VERSION = 1;

    Context context;

    SQLiteDatabase db;

    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        db = getWritableDatabase();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public synchronized void close() {
        db.close();
        super.close();
    }

    public void createTables(boolean use_category ,
                             int quantity_optional_columns) {
        createDataTables(use_category, quantity_optional_columns);
        createTittlesTable();
        if (use_category) {
            createCategoriesTable();
        }
    }

    public void deleteTables() {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseStructure.DataTable.tableName + ";");
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseStructure.TitlesTable.tableName + ";");
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseStructure.CategoriesTable.tableName + ";");
    }

    // data ////////////////////////////////////////////////////////////////////////////////////////

    public void createDataTables(boolean use_category ,
                                 int quantity_optional_columns) {
        String sql_command =
                        "CREATE TABLE " + DatabaseStructure.DataTable.tableName
                        + " ("
                        + DatabaseStructure.DataTable.Columns._id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + DatabaseStructure.DataTable.Columns.search_id + " INTEGER, "
                        + DatabaseStructure.DataTable.Columns.title + " TEXT, "
                        + DatabaseStructure.DataTable.Columns.quantity + " INTEGER, "
                        + DatabaseStructure.DataTable.Columns.scanned_quantity + " INTEGER, "
                        + DatabaseStructure.DataTable.Columns.result + " INTEGER";
        if (use_category) {
            sql_command += ", " + DatabaseStructure.DataTable.Columns.category + " TEXT";
        }
        for (int i = 0; i < quantity_optional_columns; i++) {
            sql_command += ", " + DatabaseStructure.DataTable.Columns.description + Integer.toString(i) + " TEXT"; // FIXME
        }
        sql_command += ");";
        db.execSQL(sql_command);
    }

    public void putValuesInDataTable(Scanner scanner,
                                     int num_column_id,
                                     int num_column_name,
                                     int num_column_quantity,
                                     Integer num_column_category,
                                     Integer[] numbs_optionals_columns) {

        String[] row;
        while (scanner.hasNext()) {
            row = scanner.nextLine().split(";", -1);
            ContentValues contentValue = new ContentValues();
            contentValue.put(DatabaseStructure.DataTable.Columns.search_id, Integer.parseInt(row[num_column_id]));
            contentValue.put(DatabaseStructure.DataTable.Columns.title, row[num_column_name].trim());
            contentValue.put(DatabaseStructure.DataTable.Columns.quantity, Integer.parseInt(row[num_column_quantity]));
            contentValue.put(DatabaseStructure.DataTable.Columns.scanned_quantity, 0);
            contentValue.put(DatabaseStructure.DataTable.Columns.result, Integer.parseInt(row[num_column_quantity]));
            if (num_column_category != null) {
                String str = row[num_column_category].trim();
                if (str.equals("")) {
                    str = "Неизвестная категория";
                }
                contentValue.put(DatabaseStructure.DataTable.Columns.category, str);
            }
            int count = 0;
            if (numbs_optionals_columns != null) {
                for (Integer num_column : numbs_optionals_columns) {
                    contentValue.put(DatabaseStructure.DataTable.Columns.description + Integer.toString(count++), row[num_column].trim()); // FIXME: 06.06.2022
                }
            }
            db.insert(DatabaseStructure.DataTable.tableName, null, contentValue);
        }
    }

    public void changeScanResult(int id) {
        try {
            String sql_command = "UPDATE " + DatabaseStructure.DataTable.tableName
                    + " set " + DatabaseStructure.DataTable.Columns.scanned_quantity
                    + "=" + DatabaseStructure.DataTable.Columns.scanned_quantity + "+1 where "
                    + DatabaseStructure.DataTable.Columns.search_id + "=" + String.valueOf(id);
            db.execSQL(sql_command);
            sql_command = "UPDATE " + DatabaseStructure.DataTable.tableName
                    + " set " + DatabaseStructure.DataTable.Columns.result
                    + "="
                    + DatabaseStructure.DataTable.Columns.quantity
                    + "-"
                    + DatabaseStructure.DataTable.Columns.scanned_quantity
                    + " where "
                    + DatabaseStructure.DataTable.Columns.search_id
                    + "=" + String.valueOf(id);
            db.execSQL(sql_command);
        }
        catch (Exception e) {
            Toast toast = Toast.makeText(context, e.toString(), LENGTH_SHORT);
            toast.show();
        }
    }

    public Cursor getDataFromDataTable() {
        return db.query(DatabaseStructure.DataTable.tableName, new String[] {"*"},null,
                null, null, null, null);
    }

    public int entryData(int id, String category) {
        // returns the number of occurrences of rows with the passed id
        Cursor cursor = db.query(DatabaseStructure.DataTable.tableName,
                new String[] {DatabaseStructure.DataTable.Columns.search_id},
                DatabaseStructure.DataTable.Columns.search_id + "=?" +
                        " AND " + DatabaseStructure.DataTable.Columns.category + "=?",
                new String[] {String.valueOf(id), category},
                null, null, null);
        return cursor.getCount();
    }

    public int entryData(int id) {
        // returns the number of occurrences of rows with the passed id
        Cursor cursor = db.query(DatabaseStructure.DataTable.tableName,
                new String[] {DatabaseStructure.DataTable.Columns.search_id},
                DatabaseStructure.DataTable.Columns.search_id + "=?",
                new String[] {String.valueOf(id)},
                null, null, null);
        return cursor.getCount();
    }

    public Cursor getCursorForCategories(String filter) {
        return db.query(
                DatabaseStructure.CategoriesTable.tableName,
                new String[] {
                        DatabaseStructure.CategoriesTable.Columns._id,
                        DatabaseStructure.CategoriesTable.Columns.category_name,
                        DatabaseStructure.CategoriesTable.Columns.category_completed,
                        DatabaseStructure.CategoriesTable.Columns.category_wrong,
                        DatabaseStructure.CategoriesTable.Columns.total_quantity},
                        DatabaseStructure.CategoriesTable.Columns.category_name
                                + " LIKE " + "'%" + filter + "%'",
                        null, null, null, null);
    }

    public Cursor getCursorForScanning(String selectionArg) {
        if (selectionArg == null) {
            return db.query(DatabaseStructure.DataTable.tableName,
                    new String[] {DatabaseStructure.DataTable.Columns._id,
                            DatabaseStructure.DataTable.Columns.search_id,
                            DatabaseStructure.DataTable.Columns.title,
                            DatabaseStructure.DataTable.Columns.quantity,
                            DatabaseStructure.DataTable.Columns.scanned_quantity,
                            DatabaseStructure.DataTable.Columns.result},
                    null, null, null, null, null);
        }
        else {
            return db.query(DatabaseStructure.DataTable.tableName,
                    new String[] {
                            DatabaseStructure.DataTable.Columns._id,
                            DatabaseStructure.DataTable.Columns.search_id,
                            DatabaseStructure.DataTable.Columns.title,
                            DatabaseStructure.DataTable.Columns.quantity,
                            DatabaseStructure.DataTable.Columns.scanned_quantity,
                            DatabaseStructure.DataTable.Columns.result},
                    DatabaseStructure.DataTable.Columns.category + "=?",
                    new String[] {selectionArg}, null, null, null);
        }
    }

    public String getTittle(int id) {
        Cursor cursor = db.query(DatabaseStructure.DataTable.tableName,
                new String[] {DatabaseStructure.DataTable.Columns.title},
                DatabaseStructure.DataTable.Columns.search_id + "=?",
                new String[] {String.valueOf(id)},
                null, null, null);
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    public boolean isDataBaseLoad() {
        try {
            db.query(DatabaseStructure.DataTable.tableName, new String[]{"*"}, null,
                    null, null, null, null);
            return true;
        } catch (SQLiteException e) {
            return false;
        }
    }


//tittles //////////////////////////////////////////////////////////////////////////////////////////
    public void createTittlesTable() {
        String sql_command =
                "CREATE TABLE " + DatabaseStructure.TitlesTable.tableName
                        + " ("
                        + DatabaseStructure.TitlesTable.Columns._id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + DatabaseStructure.TitlesTable.Columns.title + " TEXT);";
        db.execSQL(sql_command);
    }

    public void putValuesInTableTittles(Scanner scanner) {
        String[] row;
        row = scanner.nextLine().split(";", -1);
        ContentValues contentValue = new ContentValues();
        for (String tittle:row) {
            contentValue.put(DatabaseStructure.TitlesTable.Columns.title, tittle);
            db.insert(DatabaseStructure.TitlesTable.tableName, null, contentValue);
        }
    }

    public Cursor getDataFromTittlesTable() {
        return db.query(DatabaseStructure.TitlesTable.tableName,
                new String[] {DatabaseStructure.TitlesTable.Columns.title}, null,
                null,null, null, null);
    }


//categories ///////////////////////////////////////////////////////////////////////////////////////

    private void createCategoriesTable() {
        String sql_command =
                "CREATE TABLE " + DatabaseStructure.CategoriesTable.tableName
                        + " ("
                        + DatabaseStructure.CategoriesTable.Columns._id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + DatabaseStructure.CategoriesTable.Columns.category_name + " TEXT, "
                        + DatabaseStructure.CategoriesTable.Columns.category_completed + " INTEGER, "
                        + DatabaseStructure.CategoriesTable.Columns.category_wrong + " INTEGER, "
                        + DatabaseStructure.CategoriesTable.Columns.total_quantity + " INTEGER, "
                        + "FOREIGN KEY (" + DatabaseStructure.CategoriesTable.Columns.category_name + ") REFERENCES "
                        + DatabaseStructure.DataTable.tableName + " (" + DatabaseStructure.DataTable.Columns.category + "));";
        db.execSQL(sql_command);
    }

    public void putValuesInCategoriesTable() {
        String count_column = "count";
        Cursor cursor = db.query(
                DatabaseStructure.DataTable.tableName,
                new String[] {
                        DatabaseStructure.DataTable.Columns._id,
                        DatabaseStructure.DataTable.Columns.category,
                        "COUNT(*) as " + count_column},
                null, // FIXME: 06.06.2022
                null,
                DatabaseStructure.DataTable.Columns.category,
                null,
                null);
//        cursor.moveToFirst();
        ContentValues contentValue = new ContentValues();
        while (cursor.moveToNext()) {
            int index;
            index = cursor.getColumnIndex(DatabaseStructure.DataTable.Columns.category);
            contentValue.put(DatabaseStructure.CategoriesTable.Columns.category_name, cursor.getString(index));

            index = cursor.getColumnIndex(count_column);
            contentValue.put(DatabaseStructure.CategoriesTable.Columns.total_quantity, cursor.getInt(index));

            db.insert(DatabaseStructure.CategoriesTable.tableName, null, contentValue);
            contentValue.clear();
        }
    }

    public void setComplete(String category_name, Boolean complete){

        ContentValues contentValues = new ContentValues();
        if (complete) {
            contentValues.put(DatabaseStructure.CategoriesTable.Columns.category_completed, 1);
        }
        else {
            contentValues.put(DatabaseStructure.CategoriesTable.Columns.category_completed, 0);
        }
        db.update(DatabaseStructure.CategoriesTable.tableName,
                contentValues,
                DatabaseStructure.CategoriesTable.Columns.category_name + "=?",
                new String[] {category_name});
    }

    public void setWrong (String category_name, Boolean wrong) {
        ContentValues contentValues = new ContentValues();
        if (wrong) {
            contentValues.put(DatabaseStructure.CategoriesTable.Columns.category_wrong, 1);
        }
        else {
            contentValues.put(DatabaseStructure.CategoriesTable.Columns.category_wrong, 0);
        }
        db.update(DatabaseStructure.CategoriesTable.tableName,
                contentValues,
                DatabaseStructure.CategoriesTable.Columns.category_name + "=?",
                new String[] {category_name});
    }

    public Cursor getAllData() {
        return db.rawQuery("SELECT * FROM " + DatabaseStructure.DataTable.tableName, null);
    }

}