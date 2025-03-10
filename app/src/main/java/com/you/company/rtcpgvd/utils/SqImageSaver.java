package com.you.company.rtcpgvd.utils;

import android.media.Image;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SqImageSaver implements Runnable {

    private Image mImage;

    public SqImageSaver(Image image) {
        mImage = image;
    }

    @Override
    public void run() {
        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
        byte[] data = new byte[buffer.remaining()];
        buffer.mark();
        buffer.get(data);
        buffer.reset();
        String path = Environment.getExternalStorageDirectory() + "/DCIM/CameraV2/";
        File mImageFile = new File(path);
        if (!mImageFile.exists()) {
            mImageFile.mkdir();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = path + "IMG_" + timeStamp + ".jpg";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
            fos.write(data, 0, data.length);
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(fileName);
            ToastUtils.showCorrectToast("Saved Successfully");
            ToastUtils.show(strBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
