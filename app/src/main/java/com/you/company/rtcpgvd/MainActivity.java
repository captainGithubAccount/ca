package com.you.company.rtcpgvd;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.you.company.rtcpgvd.lib.NiceSpinner;
import com.you.company.rtcpgvd.lib.OnSpinnerItemSelectedListener;
import com.you.company.rtcpgvd.utils.AppUtils;
import com.you.company.rtcpgvd.utils.DateTimeUtil;
import com.you.company.rtcpgvd.utils.DialogUtil;
import com.you.company.rtcpgvd.utils.MPChartUtil;
import com.you.company.rtcpgvd.utils.MatrixImageUtils;
import com.you.company.rtcpgvd.utils.ToastUtils;
import com.you.company.rtcpgvd.view.CustomSeekBar;
import com.you.company.rtcpgvd.view.IntervalDialog;
import com.you.company.rtcpgvd.view.IntervalImageSavePopWindow;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MatrixView.ImageCloseClickListener, MatrixView.ImageLocationListener {

    private TextureView previewView;
    private CameraDevice device;
    private boolean hasCamera = false;
    private boolean hasPermission = false;
    private FrameLayout mRootLayout;
    private ToggleImageView btn_start;
    private static final String TAG = "MainActivity";


    private ImageAvailableHandler mainHandler;

    private List<Region> regions = new ArrayList<>();

    private ImageView region_circle,region_rect;

    private Button interval_btn;

    private ToggleImageView splashLight;

    private NiceSpinner mSpinner;

    private NiceSpinner mImageSaveSpinner;

    private LineChart lineChartView;
    //GrayValueChartView chartUtil;

    private MPChartUtil chartUtil;

    private CustomSeekBar mSeekBar;

    LinkedList<String> dataset;

    private Button sampleBtn;

    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        previewView = findViewById(R.id.previewView);
        mRootLayout = findViewById(R.id.main_content);
        sampleBtn = findViewById(R.id.lauch_sample);
        region_circle = findViewById(R.id.region_circle);
        region_rect = findViewById(R.id.region_rect);
        splashLight = findViewById(R.id.icon_splash);
        mSpinner = findViewById(R.id.capture_frequency);
        mImageSaveSpinner = findViewById(R.id.capture_interval_image);
        lineChartView = findViewById(R.id.line_chart_list);
        lineChartView.setTouchEnabled(true);
        lineChartView.setDragXEnabled(false);
        mSeekBar = findViewById(R.id.camera_focus_ajust);
        MPChartUtil.initChart(lineChartView,true,true,false);
        //chartUtil.init(lineChartView);
        initListener();

        interval_btn = findViewById(R.id.interval_btn);

        interval_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CameraHolder.getInstance().getAssessmentStatus()) {
                    ToastUtils.show(MainActivity.this,"Image Sampling, Please stop then start Timer");
                    return;
                }
                //todo 开启定时任务
                DialogUtil.showDialog(MainActivity.this.getSupportFragmentManager(), new IntervalDialog.OnTimeSetFinish() {
                    @Override
                    public void onTimeDone(String str) {
                        interval_btn.setText(str);
                        interval_btn.setEnabled(false);
                       // CameraHolder.getInstance().switchAssessment();
                        Long date =  DateTimeUtil.parseTimeToMilliseconds(str);

                         timer = new CountDownTimer(date,1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                String timeUntilFinish = DateTimeUtil.formatMillisecondsToTime(millisUntilFinished);
                                interval_btn.setText(timeUntilFinish);
                            }

                            @Override
                            public void onFinish() {
                                CameraHolder.getInstance().switchAssessment();
                                sampleBtn.setText(getResources().getString(R.string.start_sample));
                                interval_btn.setText(getResources().getString(R.string.interval_task));
                                DialogUtil.showTimeCompletDialog(MainActivity.this.getSupportFragmentManager());
                                interval_btn.setEnabled(true);
                                timer = null;
                            }
                        };
                    }
                });
            }
        });

        mImageSaveSpinner.attachDataSource(new ArrayList<>());
        mImageSaveSpinner.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(CameraHolder.getInstance().getAssessmentStatus()) {
                    ToastUtils.showCloseToast(getResources().getString(R.string.operate_parameter_error));
                    return;
                }

                IntervalImageSavePopWindow popWindow = IntervalImageSavePopWindow.create(MainActivity.this);
                popWindow.setDismissListener(new IntervalImageSavePopWindow.DismissListener() {
                    @Override
                    public void returnTimeVal(String val) {
                        Log.i("capture","receive " + val);
                        mImageSaveSpinner.setText(val);
                        long intervalTime = DateTimeUtil.convertToMilliseconds(val);
                        CameraHolder.getInstance().setIntervalMillis(intervalTime);
                    }
                });
                popWindow.showAsDropDown(mImageSaveSpinner);
            }
        });

        dataset = new LinkedList<>(Arrays.asList("1hz", "2hz", "3hz", "4hz", "5hz","6hz","7hz","8hz","9hz","10hz"));
        mSpinner.attachDataSource(dataset);
        mSpinner.setSelectedIndex(0);

        mSpinner.setBackgroundResource(R.drawable.spinner_background);
        mSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                Log.i("capture","position: " + position);
                if(CameraHolder.getInstance().getAssessmentStatus()) {
                    ToastUtils.showCloseToast(getResources().getString(R.string.operate_parameter_error));
                    return;
                }
                CameraHolder.getInstance().setFrequencey(position+1);
            }
        });
        splashLight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                splashLight.switchStatus();
                CameraHolder.getInstance().switchSplashLight(splashLight.getStatus());
                CameraHolder.getInstance().updatePreview();
//                CameraHolder.getInstance().setLightStatus(splashLight.getStatus());
//                CameraHolder.getInstance().exitCamera();
//                CameraHolder.getInstance().openCamera();
            }
        });
        region_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CameraHolder.getInstance().getAssessmentStatus()) {
                    ToastUtils.showCloseToast(getResources().getString(R.string.operate_parameter_error));
                    return;
                }
                if(MatrixImageUtils.getCaptureRegionId() > 0) {
                    View section = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_capture_section_circle,mRootLayout,false);
                    MatrixView matrixView  = section.findViewById(R.id.capture_region);
                    mRootLayout.addView(section);
                    matrixView.registerImageLocationListener(MainActivity.this);
                    matrixView.registerImageCloseListener(MainActivity.this);
                } else {
                    ToastUtils.showCloseToast(getResources().getString(R.string.add_max_areas_forbidden));
                }
            }
        });

        region_rect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(CameraHolder.getInstance().getAssessmentStatus()) {
                    ToastUtils.showCloseToast(getResources().getString(R.string.operate_parameter_error));
                    return;
                }
                if(MatrixImageUtils.getCaptureRegionId() > 0) {
                    View section = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_capture_section_rect,mRootLayout,false);
                    MatrixView matrixView  = section.findViewById(R.id.capture_region);
                    mRootLayout.addView(section);
                    matrixView.registerImageLocationListener(MainActivity.this);
                    matrixView.registerImageCloseListener(MainActivity.this);
                } else {
                    ToastUtils.showCloseToast(getResources().getString(R.string.add_max_areas_forbidden));
                }
            }
        });
    }

    private void reset(){

    }


    private void initListener(){
        sampleBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(CameraHolder.getInstance().getCaptureTasks().size() <=0) {
                    ToastUtils.showCloseToast(getResources().getString(R.string.add_sample_area_first));
                    return;
                }

                if(timer != null) {
                    timer.start();
                }

                if(!CameraHolder.getInstance().getAssessmentStatus()) {
                    //点击开始  开始+重置
                    sampleBtn.setText(getResources().getString(R.string.end_sample));
                    CameraHolder.getInstance().switchAssessment();
                } else {
                    //点击暂停
                    reset();
                    if(timer != null) {
                        timer.cancel();
                        timer = null;
                        interval_btn.setEnabled(true);
                        interval_btn.setText(getResources().getString(R.string.interval_task));
                    }
                    sampleBtn.setText(getResources().getString(R.string.start_sample));
                    CameraHolder.getInstance().switchAssessment();
                }


            }
        });
    }

    int seconds = 0;

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if(!AppUtils.isExternalStorageWritable()) {
            ToastUtils.show(this,"未找到可存储的设备");
        }
        //camera初始化
        initSeekBar();
        CameraHolder.getInstance().init(this);
        CameraHolder.getInstance().setCameraAvailableListener(new CameraHolder.CameraAvailable() {
            @Override
            public void cameraCapability() {
                CameraHolder.getInstance().openCamera();
            }
        });
        try {
            if(CameraHolder.getInstance().getCameraIds() <=0 ) {
                ToastUtils.show(this,"未找到摄像头");
            } else {
                Log.i("scale","找到摄像头");
                hasCamera = true;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       // mSpinner.setSelectedIndex(0);
        mainHandler = new ImageAvailableHandler(Looper.getMainLooper(),this);

        if(hasCamera) {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                hasPermission = true;
                CameraHolder.getInstance().initTexture(previewView);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasPermission = true;
                    CameraHolder.getInstance().initTexture(previewView);

                } else {
                    Log.d( TAG, "onRequestPermissionsResult: You Denied the Permission");
                    Toast.makeText(this, "You Denied the Permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraHolder.getInstance().exitCamera();
        DataManager.Instance().stopSync();
    }

    @Override
    public void imageLocation(int imageMatrixId,float startX, float startY, float endX, float endY,RegionType type) {
        Log.i("capture","imageLocation: "+ imageMatrixId);
//            Region region = null;
//            if(regions.size() == 0) {
//                region = new Region(startX,startY,endX,endY,imageMatrixId,type);
//                regions.add(region);
//            } else {
//                Log.i("capture","size: "+ regions.size());
//                for (Region item : regions) {
//                    if(item.getMatrixId() == imageMatrixId) {
//                        item.setStartX(item.getStartX());
//                        item.setEndX(item.getEndX());
//                        item.setStartY(item.getStartY());
//                        item.setEndY(item.getEndY());
//                    } else {
//                         region = new Region(startX,startY,endX,endY,imageMatrixId,type);
//                    }
//                }
//            }
//            if(region != null) {
//                regions.add(region);
//            }


                Log.i("capture","regions size: "+ regions.size());
                if(CameraHolder.getInstance().isCaptureTaskExist(imageMatrixId)) {
                    CaptureTask task  = CameraHolder.getInstance().getCaptureTaskById(imageMatrixId);
                    Region region = new Region(startX,startY,endX,endY,imageMatrixId,type);
                    task.setmRegion(region);
                } else {
                    Region region = new Region(startX,startY,endX,endY,imageMatrixId,type);
                    CaptureTask task = CaptureTask.newCaptureTask(mainHandler, region);
                    CameraHolder.getInstance().collectCaptureTask(task);
                }

            Log.i("capture","cameraHolder captureTasks size: "+CameraHolder.getInstance().getCaptureTasks().size());
    }




    private void initSeekBar(){
        mSeekBar.setOnSeekBarProgressListener(new CustomSeekBar.ProgressListener() {
            @Override
            public void onSeekBarProgressChanged(float progress) {
               // float val = seekBar.getProgress();

                 CameraHolder.getInstance().adjustDistance(progress);
//                CameraHolder.getInstance().adjustDistance(per);
//                CameraHolder.getInstance().updatePreview();
            }
        });
    }

    public void updateChartView(int chartId){
        List<CaptureTask> tasks = CameraHolder.getInstance().getCaptureTasks();
        ArrayList<CaptureTask> tempTasks = new ArrayList<>();
        tempTasks.clear();
        tempTasks.addAll(tasks);
        for (CaptureTask tempTask : tempTasks) {
            if(chartId == tempTask.chartId()){
                Log.i("capture","display chartId: "+ chartId);
                List<Entry> tempPoints = new ArrayList<>();
                tempPoints.addAll(tempTask.getPoints());
                MPChartUtil.setChartData(lineChartView,tempPoints, LineDataSet.Mode.CUBIC_BEZIER,chartId);
            }
        }
    }


    private static class ImageAvailableHandler extends Handler {

        private WeakReference<MainActivity> mContext;

        public ImageAvailableHandler(@NonNull Looper looper,MainActivity activity) {
            super(looper);
            mContext = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what == 1) {
                int id = msg.arg1;
                Log.i("capture","receive msg to update chart");
                mContext.get().updateChartView(id);
            }
            super.handleMessage(msg);
        }
    }



//    @Override
//    public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
//        String captureItem =  dataset.get(position);
//        parent.setText(captureItem);
//        parent.setTextColor(getResources().getColor(R.color.white));
//        Log.i("capture","capture frequency: " + captureItem);
//        CameraHolder.getInstance().setFrequencey(position);
//    }

    @Override
    public void onImageClose(int childId) {
        MatrixImageUtils.recycleCaptureId(childId);
        CameraHolder.getInstance().removeCaptureTask(childId);
    }
}