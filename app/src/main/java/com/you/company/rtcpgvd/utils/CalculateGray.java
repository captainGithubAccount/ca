package com.you.company.rtcpgvd.utils;

import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

import com.you.company.rtcpgvd.CaptureTask;
import com.you.company.rtcpgvd.ImageUtils;

import java.util.List;

public class CalculateGray implements Runnable{

    Image image;
    private List<CaptureTask> mTasks;

    public CalculateGray(Image image, List<CaptureTask> tasks){
        this.image = image;
        this.mTasks = tasks;
    }

    @Override
    public void run() {
        Bitmap bitmap = ImageUtils.imageToBitmap(image);
        if(mTasks.size() > 0) {
            Log.i("capture","collect gray value");
            for (CaptureTask captureTask : mTasks) {
                captureTask.collectGrayValue(bitmap);
            }
        }
        image.close();
    }
}
