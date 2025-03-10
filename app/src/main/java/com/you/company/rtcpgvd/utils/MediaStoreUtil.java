package com.you.company.rtcpgvd.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class MediaStoreUtil {

    public static void scanFile(Context context, File file) {
        if (file == null || context == null) {
            return;
        }

        Uri fileUri = Uri.fromFile(file);
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri);
        context.sendBroadcast(scanIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Uri saveFileToMediaStore(Context context, String fileName, String mimeType, File file) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri != null) {
            try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
                if (outputStream != null) {
                    Files.copy(file.toPath(), outputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        scanFile(context, file);
        return uri;
    }

    public static void saveImageToGallery(Context context, Bitmap bitmapImage) throws IOException {
        // Create an image file in the internal storage of the application
        File imageFile = new File(context.getFilesDir(), "image.png");
        try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        }

        // ContentValues is a simple data structure that maps column names to their values
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.MediaColumns.DATA, imageFile.getAbsolutePath());

        // Use MediaStore to insert the image into the system gallery
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // intent.setData(uri);
        // context.sendBroadcast(intent);

        // Deprecated way to scan media files on external storage
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }
}