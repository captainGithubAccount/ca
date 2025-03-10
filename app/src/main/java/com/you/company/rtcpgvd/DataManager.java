package com.you.company.rtcpgvd;

import android.util.Log;

import com.you.company.rtcpgvd.entity.GrayEntity;
import com.you.company.rtcpgvd.entity.TimeGrayEntity;
import com.you.company.rtcpgvd.utils.ExcelManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataManager {

    private List<TimeGrayEntity> timeGrayEntities = new ArrayList<>();


    ExecutorService executorService = Executors.newFixedThreadPool(5);

    private static DataManager dataMgr = new DataManager();

    private Timer mTimer;


    private File currentFile;

    private DataManager(){
        mTimer = new Timer();
        ExcelManager.getInstance();
    }

    public static DataManager Instance(){
        return dataMgr;
    }


    /**
     * 添加采集数值
     * @param time
     * @param captureId
     * @param gray
     */
    public void AddEntity(String time, int captureId,float gray) {
        boolean exists = false;
        for (int i = 0; i < timeGrayEntities.size(); i++) {
            TimeGrayEntity entity = timeGrayEntities.get(i);
            if(entity != null && entity.getTime().equals(time)) {
                exists = true;
                GrayEntity entity1 = new GrayEntity(captureId+"",gray + "");
                entity.getGrayEntityList().add(entity1);
            }
        }
        if(!exists) {
            TimeGrayEntity entity = new TimeGrayEntity();
            entity.setTime(time);
            GrayEntity entity1 = new GrayEntity(captureId+"",gray+"");
            entity.getGrayEntityList().add(entity1);
            timeGrayEntities.add(entity);
        }
    }

    /***
     * 本地保存
     */
    private void saveLocal() {
        SaveImageGrayLocal(timeGrayEntities);
    }

    public void startSync() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                saveLocal();
            }
        }, 1000, 1000);
    }

    public void stopSync(){
        mTimer.cancel();
    }

    private void removeEntity(String time){
        int index = -1;
        for (int i = 0; i < timeGrayEntities.size(); i++) {
            if(timeGrayEntities.get(i).getTime().equals(time)) {
                index = i;
            }
        }
        if(index != -1) {
            timeGrayEntities.remove(index);
        }
        Log.i("excel",  "size: " + timeGrayEntities.size());
    }

    public void SaveImageGrayLocal(List<TimeGrayEntity> entities) {
//        for (int pos = 0; pos < entities.size(); pos++) {
//            final TimeGrayEntity entity = entities.get(pos);
//            executorService.submit(new Runnable() {
//                @Override
//                public void run() {
//                    for (int col = 0; col < entity.getGrayEntityList().size(); col++) {
//                        GrayEntity grayEntity = entity.getGrayEntityList().get(col);
//                        long time = Long.parseLong(entity.getTime()) - CameraHolder.getInstance().getCurrentTime() + 1;
//                        int area = Integer.parseInt(grayEntity.getSampleArea());
//                        ExcelManager.getInstance().appendData((int)time,area,grayEntity.getGrayVal());
//                    }
//                    removeEntity(entity.getTime());
//
//                }
//            });
//        }
        Log.i("dataManager","cache size: "+ entities.size());
        for (int i = 0; i < entities.size(); i++) {
            ExcelManager.getInstance().putGrayEntity(entities.get(i));
        }
        entities.clear();
    }



//    private static boolean realSaveLocal(TimeGrayEntity entity) {
//        FileOutputStream outputStream = null;
//        try {
//            outputStream = new FileOutputStream(fileName,true);
//            byte[] bytes = entity.toString().getBytes();
//            outputStream.write(bytes);
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        } finally {
//            try {
//                if (outputStream != null) {
//                    outputStream.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
