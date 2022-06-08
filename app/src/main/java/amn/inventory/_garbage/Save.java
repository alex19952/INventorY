//package amn.inventory.main;
//
//import android.database.Cursor;
//import android.media.MediaScannerConnection;
//import android.view.View;
//
//import java.io.File;
//import java.io.IOException;
//
//import jxl.Workbook;
//import jxl.write.Label;
//import jxl.write.WritableSheet;
//import jxl.write.WritableWorkbook;
//import jxl.write.WriteException;
//
//public class Save {
//
//    public void onClickSave(View view) {
//
//        String save_file = "/storage/emulated/0/Download/База МТР/excel.xls"; // FIXME
//        WritableWorkbook workbook = null;
//
//        try {
//            workbook = Workbook.createWorkbook(new File(save_file));
//            MediaScannerConnection.scanFile(this, new String[]{save_file}, null, null);
//            WritableSheet sheet = workbook.createSheet("Инвентаризация", 0);
//
//            Cursor cursor = helper.getDataFromTittlesTable();
//            cursor.moveToFirst();
//
//            for (int i = 0; i < cursor.getCount(); i++) {
//                Label label = new Label(i, 0, cursor.getString(0));
//                sheet.addCell(label);
//                cursor.moveToNext();
//            }
//            cursor.close();
//
//            cursor = helper.getDataFromDataTable();
//            cursor.moveToFirst();
//
//            for (int row = 1; row < cursor.getCount() + 1; row++) {
//                for (int column = 0; column < cursor.getColumnCount(); column++) {
//                    Label label = new Label(column, row, cursor.getString(column));
//                    sheet.addCell(label);
//                }
//                cursor.moveToNext();
//            }
//            workbook.write();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (WriteException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (workbook != null) {
//                try {
//                    workbook.close();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (WriteException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//}
