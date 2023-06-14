package amn.inventory.model;

import android.net.Uri;

import java.io.ByteArrayInputStream;

public interface ISaveHelper {
    public boolean saveData(String filePath);
    public byte[] getBytes();
    public String getCSV();
}
