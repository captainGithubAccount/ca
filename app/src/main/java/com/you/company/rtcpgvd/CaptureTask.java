package com.you.company.rtcpgvd;

import android.graphics.Bitmap;
import android.media.ImageReader;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CaptureTask {

    private Handler mainHandler;

    private int second = 0;

    private int captureId;

    private ImageReader.OnImageAvailableListener availableListener;

    private ExecutorService executor;

    private Bitmap bitmap;

    public Region getmRegion() {
        return mRegion;
    }

    public void setmRegion(Region mRegion) {
        this.mRegion = mRegion;
    }

    private Region mRegion;

    private List<Float> mGrays = new ArrayList<>();

    private int time;

    private CaptureTask(Handler handler,Region region){
        second = 0;
        if(mPointValues == null) {
            mPointValues = new ArrayList<>();
        }
        executor = Executors.newCachedThreadPool();
        this.mRegion = region;
        mainHandler = handler;
    }

    private List<Entry> mPointValues;

    public synchronized List<Entry> getPoints(){
        return mPointValues;
    }

    public synchronized void addPoints(Entry entry){
        mPointValues.add(entry);
    }

    public synchronized static  CaptureTask newCaptureTask(Handler handler,Region region){
        CaptureTask task = new CaptureTask(handler,region);
        return task;
    }

    public void setTime(int time){
        this.time = time;
    }

    public void collectGrayValue(Bitmap bitmap){
        Log.i("capture","start calculate gray value");
        float averageGrayVal;
                synchronized (CaptureTask.class) {
                    try {
                        float grayVal;
                        if(mRegion.getRegionType() == RegionType.CIRCLE) {
                            grayVal = ImageUtils.calculateGrayValueCircle(bitmap,mRegion.getStartX(),mRegion.getStartY(),mRegion.getEndX(),mRegion.getEndY());

                        }else {
                            grayVal = ImageUtils.calculateGrayValueRect(bitmap,mRegion.getStartX(),mRegion.getStartY(),mRegion.getEndX(),mRegion.getEndY());
                        }
                        mGrays.add(grayVal);
                        if(mGrays.size() == CameraHolder.getInstance().getFrequencey()) {
                            float sum = 0;
                            for (Float mGray : mGrays) {
                                sum += mGray;
                            }
                            averageGrayVal = sum / CameraHolder.getInstance().getFrequencey();
                            if(getPoints().size() > 15) {
                                mPointValues.remove(0);
                            }

                            DataManager.Instance().AddEntity(time+"",mRegion.getMatrixId(),averageGrayVal);
                            Log.i("capture","save time: "+ time);
                            addPoints(new Entry(Float.parseFloat(String.valueOf(time)),averageGrayVal));
                            dispatchMsgShow();
                            mGrays.clear();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("capture",e.getMessage());
                    }
                }


    }

    public List<Entry> pointValues(){
        return mPointValues;
    }

    public int chartId(){
        return mRegion.getMatrixId();
    }

    public void dispatchMsgShow(){
        Message msg = mainHandler.obtainMessage();
        msg.what = 1;
        msg.arg1 = mRegion.getMatrixId();
        mainHandler.sendMessage(msg);
    }


    public void bindAvailableImage(ImageReader imageReader, Handler handler) {
        imageReader.setOnImageAvailableListener(availableListener,handler);
    }
}
