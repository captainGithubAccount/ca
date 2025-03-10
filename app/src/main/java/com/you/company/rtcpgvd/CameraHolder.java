package com.you.company.rtcpgvd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.you.company.rtcpgvd.utils.CameraUtil;
import com.you.company.rtcpgvd.utils.ExcelManager;
import com.you.company.rtcpgvd.utils.SqImageSaver;
import com.you.company.rtcpgvd.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class CameraHolder {

    private Context mContext;
    private CameraManager cameraManager;
    private CameraOrientation c_orientation;
    private String[] cameraIds;
    private CameraDevice cameraDevice;
    private String mCameraId = null;

    private static CameraHolder instance;
    private TextureView mTextureView;
    private TextureView.SurfaceTextureListener textureListener;
    private Handler cameraHandler;
    private Size previewSize;
    private ImageReader imageReader;
    private List<CaptureTask> captureTasks;
    private ArrayList<Surface> mCaptureSurfaces;

    private CaptureRequest.Builder mPreviewBuilder;

    private boolean splashLight = true;

    private boolean startCapture;

    private int frequencey = 1;

    private float minFocusLen;

    private float maxFocusLen;

    private int second = 0;

    private long nextMillis = 0;

    private long intervalMillis = 0;

    private long saveImgMillis = 0;

    private UUID uuid;

    private long firstTime;

    public static CameraHolder getInstance() {
        if (instance == null) {
            instance = new CameraHolder();
        }
        return instance;
    }

    private CameraHolder() {
        mCaptureSurfaces = new ArrayList<>();
    }

    public void init(Context context) {
        mContext = context;
        cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        HandlerThread handlerThread = new HandlerThread("camera");
        handlerThread.start();
        cameraHandler = new Handler(handlerThread.getLooper());
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        if (captureTasks == null) {
            captureTasks = new ArrayList<>();
        }
        //  imageReader = ImageReader.newInstance(metrics.widthPixels,metrics.heightPixels, ImageFormat.RGB_565,2);
    }

    public void setFrequencey(int fre) {
        this.frequencey = fre;
    }

    public int getFrequencey(){
        return frequencey;
    }

    public void setLightStatus(boolean status) {
        splashLight = status;
    }

    public boolean isCaptureTaskExist(int captureId) {
        for (CaptureTask captureTask : captureTasks) {
            if(captureTask.chartId() == captureId) {
                return true;
            }
        }
        return false;
    }

    public CaptureTask getCaptureTaskById(int captureId) {
        for (CaptureTask captureTask : captureTasks) {
            if(captureId == captureTask.chartId()) {
                return captureTask;
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void collectCaptureTask(CaptureTask task)
    {
        try {
            captureTasks.add(task);
            captureTasks.sort(new Comparator<CaptureTask>() {
                @Override
                public int compare(CaptureTask o1, CaptureTask o2) {
                    return o1.chartId() - o2.chartId();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void initTexture(TextureView textureView) {
        mTextureView = textureView;

        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Matrix matrix = new Matrix();
                matrix.postRotate(270, mTextureView.getWidth()/2, mTextureView.getHeight()/2);
                matrix.postScale(1f * mTextureView.getWidth()/mTextureView.getHeight(),
                        1f * mTextureView.getHeight() / mTextureView.getWidth(),mTextureView.getWidth()/2,
                        mTextureView.getHeight()/2);
                mTextureView.setTransform(matrix);
                preparePreview(width, height);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }


    private void quit() {
        mContext = null;
    }

    private ImageReader.OnImageAvailableListener imageAvailableListener;

    public void newImageReader() {

    }

    public void registerImageAvailableListener(ImageReader.OnImageAvailableListener listener) {
//        if(imageReader != null) {
//            imageReader.setOnImageAvailableListener(listener,handler);
//        }
        imageAvailableListener = listener;
    }

    public synchronized List<CaptureTask> getCaptureTasks(){
        return captureTasks;
    }

    public void removeCaptureTask(int captureId) {
        Log.i("capture","before remove :" + captureTasks.size());
        int index = Integer.MIN_VALUE;
        for (int i = 0; i < captureTasks.size(); i++) {
            if(captureTasks.get(i).chartId() == captureId) {
                index = i;
            }
        }
        if(index != Integer.MIN_VALUE)
        captureTasks.remove(index);
        Log.i("capture","after remove: "+ captureTasks.size() );
        for (int i = 0; i < captureTasks.size(); i++) {
            Log.i("capture","id: "+ captureTasks.get(i).chartId());
        }
    }

    public int getCameraIds() throws CameraAccessException {
        cameraIds = cameraManager.getCameraIdList();
        return cameraIds.length;
    }

    public void setIntervalMillis(long millis) {
        intervalMillis = millis;
    }

    /**
     * 打开摄像头朝向
     */
    private void getCameraOrientation() {

    }

    /**
     * 切换摄像头朝向
     */
    public void switchCameraOrientation() {

    }

    private float focusLength;

    CameraCharacteristics characteristics;

    private float maxZoom;

    /**
     * 打开摄像头预览
     */
    @SuppressLint("MissingPermission")
    public void preparePreview(int width, int height) {
        if (cameraIds.length > 0) {
            try {
                for (int i = 0; i < cameraIds.length; i++) {
                    String cameraId = cameraIds[i];
                    //描述相机设备的属性类
                    characteristics = cameraManager.getCameraCharacteristics(cameraId);
                    maxZoom = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
                    //获取是前置还是后置摄像头
                    Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);

                    //使用后置摄像头
                    if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                      //  focusLength = characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
                        if(isSupportManualSensor(characteristics)){
                            Log.i("capture","support manual sensor");
                        }
                     //   Log.i("capture","focusLength: " + focusLength);
                        previewSize = CameraUtil.getOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height);
                        mCameraId = cameraId;
                        Size[] sizeMap = map.getOutputSizes(SurfaceTexture.class);
                        previewSize = sizeMap[0];
                        if(cameraAvailableListener !=null) {
                            cameraAvailableListener.cameraCapability();
                        }
                    }
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

    }

    public interface CameraAvailable {
        void cameraCapability();
    }

    private CameraAvailable cameraAvailableListener;

    public void setCameraAvailableListener(CameraAvailable listener) {
        cameraAvailableListener = listener;
    }

    public boolean isSupportManualSensor(CameraCharacteristics characteristics) {
        int[] capabilities = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
        for (int i = 0; i < capabilities.length; i++) {
            if(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR == capabilities[i]) {
                return true;
            }
        }
        return false;
    }

    public void openCamera() {
        try {
            if(mCameraId!=null) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    //
                    cameraManager.openCamera(mCameraId, callback, cameraHandler);
                }
            } else {
                ToastUtils.show(mContext,"未找到合适的摄像头");
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.i("capture", e.getMessage());
        }
    }


    /**
     * 打开闪光灯
     */
    public void openFlashLight() {

    }




    private CameraDevice.StateCallback callback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice device) {
            cameraDevice = device;
            if (!mCaptureSurfaces.isEmpty()) {
                mCaptureSurfaces.clear();
            }
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            texture.setDefaultBufferSize(mTextureView.getWidth(), mTextureView.getHeight());
            Surface previewSurface = new Surface(texture);
            try {
                mPreviewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                toggleSplashLight();
                adjustZoomLevel(1.5f);

                mPreviewBuilder.addTarget(previewSurface);
                imageReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), ImageFormat.JPEG, 3);
                imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader reader) {
                        Image image = reader.acquireLatestImage();
                        if(image == null) {
                            return;
                        }
                        if(!startCapture) {
                            Log.i("capture","not launch");
                            image.close();
                            return;
                        }

                        //判断采集画面保存周期设定，如果为0 则不用
                        if (intervalMillis != 0) {

                            if(saveImgMillis == 0) {
                                new SqImageSaver(image).run();
                                saveImgMillis = System.currentTimeMillis();
                            }else {
                                if(System.currentTimeMillis() >= saveImgMillis + intervalMillis) {
                                    new SqImageSaver(image).run();
                                    saveImgMillis = System.currentTimeMillis();
                                }
                            }
                        }
                        if(nextMillis == 0) {
                            nextMillis = System.currentTimeMillis();
                            firstTime = nextMillis / 1000;
                            Log.i("capture","record first millis: "+ nextMillis);
                        } else {
                            //加入当前时间戳还没到就继续等待
                            if(System.currentTimeMillis() < (nextMillis + 1000 / frequencey)) {
                                //Log.i("capture","还未到下一个时间戳");
                                Log.i("capture","未到下一个采集时间点");
                                image.close();
                                return;
                            } else {
                                // 记录当前
                                Log.i("capture","记录下一个时间戳");
                                nextMillis = System.currentTimeMillis();
                            }
                        }

                        //Log.i("capture","acquire image");
                        Bitmap bitmap = ImageUtils.imageToBitmap(image);
                        if(captureTasks.size() > 0) {
                            Log.i("capture","collect gray value");
                            Log.i("capture","captureTasks size: " + captureTasks.size());
                            Long tempTime = System.currentTimeMillis() / 1000;
                            Log.i("timeDiff","tempTime: "+tempTime);
                            Log.i("timeDiff","currentTime: " + firstTime);
                            int timeDiff = (int) (tempTime - firstTime + 1);
                            Log.i("timeDiff","timeDiff: "+ timeDiff);
                            for (CaptureTask captureTask : getCaptureTasks()) {

                                captureTask.setTime(timeDiff);
                                captureTask.collectGrayValue(bitmap);
                            }
                        }
                        image.close();
                    }
                },cameraHandler);
                mCaptureSurfaces.add(imageReader.getSurface());
                mPreviewBuilder.addTarget(imageReader.getSurface());
                mCaptureSurfaces.add(previewSurface);

                cameraDevice.createCaptureSession(mCaptureSurfaces, sessionCallback, cameraHandler);
            } catch (CameraAccessException e) {

            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
        }

        @Override
        public void onClosed(@NonNull CameraDevice camera) {
            super.onClosed(camera);
            Log.i("capture","camera close");
        }
    };

    private CameraCaptureSession currentSession;

    public void switchSplashLight(boolean status) {
        splashLight = status;
        toggleSplashLight();
    }

    public void toggleSplashLight(){
        if(mPreviewBuilder != null) {
            if(splashLight) {
                mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
                mPreviewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
            } else {
                mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON);
                mPreviewBuilder.set(CaptureRequest.FLASH_MODE,CameraMetadata.FLASH_MODE_OFF);
            }
        }
    }

    /**
     * 切换为手动曝光
     */
    public void switchManualExposure() {
        try {
            // 获取相机的特性，以便查询支持的曝光参数范围
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(mCameraId);
            Range<Long> exposureTimeRange = characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
            Range<Integer> sensitivityRange = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);

            // 设置曝光时间（需要在支持的范围内）
            long exposureTime = (exposureTimeRange.getLower() + exposureTimeRange.getUpper()) / 2; // 示例：设置为中间值
            mPreviewBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposureTime);

            // 设置感光度（ISO，需要在支持的范围内）
            int sensitivity = sensitivityRange.getUpper(); // 示例：设置为最大值
            mPreviewBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, sensitivity);

            // 如果需要，可以锁定自动曝光
            mPreviewBuilder.set(CaptureRequest.CONTROL_AE_LOCK, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private float mCurrentZoomLevel = 1.5f; // 初始缩放级别为1.0（无缩放）

    private float stepScale =  maxZoom / (2.0f - 1.0f);

    public void adjustDistance(float zoomLevel) {
//        focus_per = focus_per * (focusLength / 2);
       // Log.i("capture","focus_per :" + focus_per);
//        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);
//        mPreviewBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, zoomLevel * 3);

        adjustZoomLevel(zoomLevel);

        updatePreview();
    }

    public void adjustZoomLevel(float zoomLevel) {
        if(characteristics != null) {
            zoomLevel = (zoomLevel - 1) > 0? (zoomLevel -1): 0.1f;
            float calculatedZoom = zoomLevel  * maxZoom;
            Rect newRect = getZoomRect(calculatedZoom);
            mPreviewBuilder.set(CaptureRequest.SCALER_CROP_REGION, newRect);
        }
    }

    private Rect getZoomRect(float zoomLevel) {
        //获取传感器的活动区域大小
        Rect activeRect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        if (activeRect == null) {
            Log.e("CameraActivity", "SENSOR_INFO_ACTIVE_ARRAY_SIZE is null!");
            return null;
        }

        int minW = (int) (activeRect.width() / maxZoom);
        int minH = (int) (activeRect.height() / maxZoom);
        int difW = activeRect.width() - minW;
        int difH = activeRect.height() - minH;


        int cropW = (int) (difW * (zoomLevel - 1) / (maxZoom - 1) / 2F);
        int cropH = (int) (difH * (zoomLevel - 1) / (maxZoom - 1) / 2F);

        Rect scaleRect =  new Rect(cropW, cropH, activeRect.width() - cropW,
                activeRect.height() - cropH);
        return scaleRect;
    }

    public void updatePreview() {

        try {
            if(mPreviewBuilder == null) {
                mPreviewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            }
            currentSession.setRepeatingRequest(mPreviewBuilder.build(), null, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.StateCallback sessionCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            currentSession = session;
            updatePreview();
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
            Log.i("capture", "capture session configure failed");
            cameraCaptureSession.close();
        }
    };

    public void exitCamera() {
        if(currentSession != null)
          currentSession.close();
        if(cameraDevice != null)
          cameraDevice.close();
    }

    public boolean getAssessmentStatus(){
        return startCapture;
    }

    public void switchAssessment() {
        if (startCapture) {
            startCapture = false;
            DataManager.Instance().stopSync();
            ExcelManager.getInstance().stopSync();
        } else {
            generateUUID(); //生成一个uuid标识这次采集
            startCapture = true;
            DataManager.Instance().startSync();
            ExcelManager.getInstance().startSync();
        }

    }

    public String generateUUID(){
        uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public int getCurrentSecond() {
        return ++second;
    }

    public String getAssessmentId(){
        return uuid.toString();
    }

    public enum CameraOrientation {
        FRONT,
        BACK;
    }

}
