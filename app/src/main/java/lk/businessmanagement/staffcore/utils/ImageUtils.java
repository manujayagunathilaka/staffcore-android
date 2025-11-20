package lk.businessmanagement.staffcore.utils;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageUtils {

    public static String copyImageToAppStorage(Context context, Uri sourceUri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(sourceUri);

            String fileName = "EMP_" + System.currentTimeMillis() + ".jpg";

            File directory = new File(context.getFilesDir(), "employee_images");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File destinationFile = new File(directory, fileName);

            OutputStream outputStream = new FileOutputStream(destinationFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return destinationFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
