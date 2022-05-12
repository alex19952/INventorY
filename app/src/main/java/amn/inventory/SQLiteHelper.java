package amn.inventory;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;

class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Inventory";
    private static final int DB_VERSION = 1;
    private static final String TABLE_DATA_NAME = "MTRs";
    private static final String TABLE_SETTINGS_NAME = "Settings";
    private String DB_PATH;
    private String[] COLUMNS_NAMES = {
            "ID_FOR_SEARCH",        //  0   id
            "CODE",                 //  1   код
            "DATE",                 //  2   дата
            "INV_NUM",              //  3   инвентарник
            "NAME_MTR",             //  4   наименование
            "QUANTITY",             //  5   количество
            "SCANNED_QUANTITY",     //  6   количество отсканированных
            "PRICE",                //  7   цена
            "PLACE",                //  8   местонахождение
            "RESP_PERS",            //  9   ответстенный
            "DESCRIPTION"};         //  10  описание
    private String ADDITIONAL_COLUMNS = ", PLACE TEXT";


    SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) { //FIXME сделать пустым
        db.execSQL("CREATE TABLE " + TABLE_DATA_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID_FOR_SEARCH INTEGER, "
                + "NAME_MTR TEXT, "
                + "QUANTITY INTEGER, "
                + "PLACE TEXT);");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void cteateTableData (SQLiteDatabase db,
                                 boolean use_groups ,
                                 int quantity_optional_columns) {
        String sql_command =
                        "CREATE TABLE "
                        + TABLE_DATA_NAME
                        + " ("
                        + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "ID_FOR_SEARCH INTEGER, "
                        + "NAME_MTR TEXT, "
                        + "QUANTITY INTEGER, "
                        + "SCANNED_QUANTITY INTEGER";
        if (use_groups) {
            sql_command += ", GROUPS TEXT";
        }
        for (int i = 0; i < quantity_optional_columns; i++) {
            sql_command += "DESCRIPTION_" + Integer.toString(i);
        }
        sql_command += ");";
        db.execSQL(sql_command);
        int x = 1;
    }

    public void createTableSettings () {

    }

    public void clearTableData(SQLiteDatabase db, Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 17)
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        else
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        db.execSQL("Delete from " + TABLE_DATA_NAME);
        db.close();
    }


    public ArrayList<MTR> getMtrList (SQLiteDatabase db) { // FIXME переместить эту функцию куда нибудь
        ArrayList<MTR> list = new ArrayList<>();
        Cursor cursor = db.query(TABLE_DATA_NAME, new String[] {"ID_FOR_SEARCH", "NAME_MTR", "QUANTITY"},
                null, null, null, null, null); // FIXME обращение к несуществующей таблице
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(new MTR(   cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getInt(2)));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return list;
    }

    public ArrayList<MTR> getMtrList (SQLiteDatabase db, String selectionArg) { // FIXME переместить эту функцию куда нибудь
        ArrayList<MTR> list = new ArrayList<>();
        Cursor cursor = db.query(TABLE_DATA_NAME, new String[] {"ID_FOR_SEARCH", "NAME_MTR", "QUANTITY"},
                "PLACE=?", new String[] {selectionArg}, null, null, null); // FIXME обращение к несуществующей таблице
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(new MTR(   cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2)));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return list;
    }


}