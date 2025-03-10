package com.you.company.rtcpgvd;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public  class ImageSaver implements Runnable {
    private final Image mImage;
    private final File mFile;
    Context mContext;

    private static final String TAG = "ImageSaver";
    
    ImageSaver(Context context,Image image, File file) {
        mContext = context;
        mImage = image;
        mFile = file;
    }

    @Override
    public void run() {
        Log.d(TAG,"take picture Image Run");
        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DESCRIPTION, "This is an qr image");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, mFile.getName());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.TITLE, "Image.jpg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/");
        Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = mContext.getContentResolver();
        Uri insertUri = resolver.insert(external, values);
        OutputStream os = null;
        try {
            if (insertUri != null) {
                os = resolver.openOutputStream(insertUri);
            }
            if (os != null) {
                os.write(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mImage.close();
            try {
                if(os!=null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
