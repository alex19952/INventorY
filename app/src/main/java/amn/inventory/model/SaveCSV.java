package amn.inventory.model;


import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import amn.inventory.helpers.SQLiteHelper;

public class SaveCSV implements ISaveHelper {

    SQLiteHelper sqLiteHelper;

    public SaveCSV(SQLiteHelper sqLiteHelper) {
        this.sqLiteHelper = sqLiteHelper;
    }

    @Override
    public boolean saveData(String filePath) {
        Cursor cursor = sqLiteHelper.getAllData();
        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            // Записываем заголовки столбцов в файл CSV
            int columnCount = cursor.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                writer.write(cursor.getColumnName(i));
                if (i < columnCount - 1) {
                    writer.write(",");
                }
            }
            writer.newLine();

            // Перебираем строки в Cursor и записываем данные в файл CSV
            while (cursor.moveToNext()) {
                for (int i = 0; i < columnCount; i++) {
                    String value = cursor.getString(i);
                    writer.write(value);
                    if (i < columnCount - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }

            // Закрываем BufferedWriter
            writer.close();
            System.out.println("Запись в файл CSV успешно выполнена.");
            return true; // outputStream.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public byte[] getBytes() {
        Cursor cursor = sqLiteHelper.getAllData();
        String data = "";
        // Записываем заголовки столбцов в файл CSV
        int columnCount = cursor.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            data += cursor.getColumnName(i);
            if (i < columnCount - 1) {
                data += "|";
            }
        }
        data += "\n";

        // Перебираем строки в Cursor и записываем данные в файл CSV
        while (cursor.moveToNext()) {
            for (int i = 0; i < columnCount; i++) {
                String value = cursor.getString(i);
                data += value;
                if (i < columnCount - 1) {
                    data += "|";
                }
            }
            data += "\n";
        }
        cursor.close();
        return data.getBytes();
    }


        public String getCSV() {
            Cursor cursor = sqLiteHelper.getAllData();
            String data = "";
            // Записываем заголовки столбцов в файл CSV
            int columnCount = cursor.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                data += cursor.getColumnName(i);
                if (i < columnCount - 1) {
                    data += "|";
                }
            }
            data += "\n";

            // Перебираем строки в Cursor и записываем данные в файл CSV
            while (cursor.moveToNext()) {
                for (int i = 0; i < columnCount; i++) {
                    String value = cursor.getString(i);
                    data += value;
                    if (i < columnCount - 1) {
                        data += "|";
                    }
                }
                data += "\n";
            }
            cursor.close();
            return data;
    }
}
