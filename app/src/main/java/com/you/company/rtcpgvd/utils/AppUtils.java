package com.you.company.rtcpgvd.utils;

import android.os.Environment;

public class AppUtils {


    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
