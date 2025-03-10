package com.you.company.rtcpgvd.utils;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.you.company.rtcpgvd.CameraHolder;
import com.you.company.rtcpgvd.entity.GrayEntity;
import com.you.company.rtcpgvd.entity.TimeGrayEntity;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class ExcelManager {
    private static ExcelManager instance;
    private static String currentFileName;
    private Workbook workbook;
    private final ReentrantLock lock = new ReentrantLock();
    private File file;
    private String sheetName =  "Sheet1";
    private PriorityBlockingQueue<TimeGrayEntity> blockingQueue = new PriorityBlockingQueue<>();
    private boolean sync = false;
    ExecutorService executorService = Executors.newFixedThreadPool(5);
    private static final int MSG_DO_WORK = 1;

    private HandlerThread handlerThread = new HandlerThread("excelManager");
    private Handler mHandler;
    private ExcelManager() {
        Sheet sheet;
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case MSG_DO_WORK:
                        loop();
                }

            }
        };
        try {
            if (file == null || !file.exists()) {
                Log.i("excel","new a file");
                file = new File(currentFileName);
                // 如果文件不存在或需要重新生成，则创建新文件
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet(sheetName);

                // 创建列名
                Row headerRow = sheet.createRow(0);
                for (int i = 1; i <= 10; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue("zone" + i);
                }

                // 保存文件
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                    Log.i("excel","write col header");
                } finally {
                    workbook.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putGrayEntity(TimeGrayEntity entity) {
        Log.i("excel","put GrayEntity" + entity.getTime());
        blockingQueue.put(entity);
    }

    public static synchronized ExcelManager getInstance() {
        if (instance == null) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String path = Environment.getExternalStorageDirectory() + "/DCIM/CameraV2/Calculate" + timeStamp;
            File mImageFile = new File(path);
            if (!mImageFile.exists()) {
                mImageFile.mkdir();
            }
            String uuid = CameraHolder.getInstance().getAssessmentId();
            String fileName = path + "/" + uuid  + ".xlsx";
            currentFileName = fileName;
            instance = new ExcelManager();
        }
        return instance;
    }

    private void loop(){
        while(sync) {
            try {
                if(blockingQueue.size() != 0 ) {
                    TimeGrayEntity entity = blockingQueue.take();
                    Log.i("excel","entity time: "+ entity.getTime());
                    List<GrayEntity> grayEntitys = entity.getGrayEntityList();
                    for (int i = 0; i < grayEntitys.size(); i++) {
                        final GrayEntity grayEntity = grayEntitys.get(i);
                        executorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                int area = Integer.parseInt(grayEntity.getSampleArea());
                                appendData(Integer.parseInt(entity.getTime()),area,grayEntity.getGrayVal());
                            }
                        });

                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void appendData(int rowNum,int colNum,String val) {
        lock.lock();
        Log.i("excel","rowNum: "+ rowNum + "colNum: " + colNum + "VALUE: "+ val);
        try {
            try {
                FileInputStream fileIn = new FileInputStream(file);
                workbook = WorkbookFactory.create(fileIn);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (EncryptedDocumentException e) {
                e.printStackTrace();
            }

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
            }

            Row assertRow = sheet.getRow(rowNum);
            if(assertRow == null) {
                assertRow = sheet.createRow( rowNum);
            }
            Cell cell = assertRow.createCell(colNum);
            cell.setCellValue(val);
            Log.i("excel","cell: "+ cell.getStringCellValue());
            saveWorkbook();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void saveWorkbook() {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        } catch (IOException e) {
            Log.i("excel",e.toString());
            e.printStackTrace();
        }
    }

    public void startSync() {
        sync = true;
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_DO_WORK;
        mHandler.sendMessage(msg);
    }

    public void stopSync(){
        sync = false;
    }
}