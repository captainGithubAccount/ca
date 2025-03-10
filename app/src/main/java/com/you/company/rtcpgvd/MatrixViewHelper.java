package com.you.company.rtcpgvd;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class MatrixViewHelper {

    private HashMap<Integer,MatrixView.ImageLocationListener> locationListeners = new HashMap<>();

    private MatrixViewHelper(){

    }

    private static MatrixViewHelper instance = new MatrixViewHelper();
    public static MatrixViewHelper getInstance(){//返回值类型为Singleton
        return instance;
    }

    public void addListener(int matrixId, MatrixView.ImageLocationListener listener) {
        locationListeners.put(matrixId,listener);
    }

    public MatrixView.ImageLocationListener getListener(int matrixId) {
        return locationListeners.get(matrixId);
    }

    public void print() {
        for (Map.Entry<Integer, MatrixView.ImageLocationListener> entry : locationListeners.entrySet()) {
            Log.i("capture","key: "+ entry.getKey() + " value:" + entry.getValue());
        }
    }
}
