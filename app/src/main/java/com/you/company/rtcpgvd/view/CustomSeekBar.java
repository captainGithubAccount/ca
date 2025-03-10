package com.you.company.rtcpgvd.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.you.company.rtcpgvd.R;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author wangyongjian
 * @date 2025/1/10
 * @descrption
 */
public class CustomSeekBar extends View {

    private Paint textPaint;
    private int startValue;
    private int endValue;

    private float moveDistance;

    private float downY;

    private int radiusLength;

    private Paint paint;

    private Paint thumbPaint;

    private Paint txtPaint;

    private float circleX;
    private float circleY;

    private float translateFactor = 0.75f;

    private float transThreshold = 25; //最小移动阈值

    private float minCenterY;
    private float maxCenterY;

    private float selectedProgress = 1.5f;

    private ProgressListener progressListener = null;

    private HashMap<Float,Double> intermediateVals = new HashMap<>();

    public CustomSeekBar(Context context) {
        super(context);
        init();
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public interface ProgressListener {
        void onSeekBarProgressChanged(float progress);
    }

    private void init(){
        startValue = 1;
        endValue = 2;
        paint = new Paint();
        thumbPaint = new Paint();
        txtPaint = new Paint();
        txtPaint.setColor(getResources().getColor(R.color.black_90));
        txtPaint.setTextSize(24f);
        thumbPaint.setColor(getResources().getColor(R.color.white));
        thumbPaint.setTextSize(24F);
        thumbPaint.setAntiAlias(true);
        thumbPaint.setStyle(Paint.Style.STROKE);
        thumbPaint.setStrokeWidth(3f);
        radiusLength = 180;
        paint.setColor(getResources().getColor(R.color.black_30));


    }

    float width;
    float height;

    boolean firstDraw = true;

    private float margin = 3.5f;
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if(firstDraw) {
            calculate(canvas);
            firstDraw = false;
        }

        drawMaxValue(canvas);
        drawMinValue(canvas);
        canvas.drawRoundRect(0,0,width,height,radiusLength, radiusLength,paint);
        canvas.drawCircle(circleX, circleY ,width/2,thumbPaint);
        float txtWidth =  txtPaint.measureText("1.2");

        canvas.drawText(selectedProgress +"",circleX - txtWidth/2,circleY,thumbPaint);
    }

    private void drawMaxValue(Canvas canvas){
        float txtWidth = txtPaint.measureText("2.0");
        canvas.drawText("2.0",width/2 - txtWidth/2,width/2,txtPaint);
    }

    private void drawMinValue(Canvas canvas) {
        float txtWidth = txtPaint.measureText("1.0");
        canvas.drawText("1.0",width/2 - txtWidth/2,height - width/2,txtPaint);
    }

    private void calculate(Canvas canvas)
    {
        // init the center coor of circle
        width = canvas.getWidth();
        height = canvas.getHeight();
        circleX = width /2 ;
        circleY = height/2;
        minCenterY =  width/2;
        maxCenterY = height - width /2;
        initIntermediate();
    }

    private void initIntermediate() {
        float offset = maxCenterY - minCenterY;
        float step = offset / 10 ;
        float i= 0;
        while(minCenterY <= maxCenterY) {
            float progress = i / 10;

            intermediateVals.put(minCenterY,  (2.0 - progress));
            minCenterY += step;
            i++;
        }
        for (Map.Entry<Float, Double> entry : intermediateVals.entrySet()) {
            Log.i("seek","key: " + entry.getKey() + "value:  " + entry.getValue());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private float preX = 0;
    private float preY = 0;

    private float offsetY = 0;

    private boolean isVibrate = true;

    public void setOnSeekBarProgressListener(ProgressListener listener) {
        progressListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preX = event.getX();
                preY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //circleY = event.getX();
                float curY = event.getY();
                Log.i("seek","offsetY: " + offsetY);
               // circleY += offsetY / height ;
                if(curY < width/2) {
                    curY = width/2;
                    isVibrate = false;
                }
                if(curY > height - width/2) {
                    curY = height -width/2;
                    isVibrate = false;
                }
                circleY = curY;
                for (Map.Entry<Float, Double> floatFloatEntry : intermediateVals.entrySet()) {
                    if(Math.abs(circleY - floatFloatEntry.getKey())  <=20 ) {
                        selectedProgress =  floatFloatEntry.getValue().floatValue();
                    }
                }
                invalidate();
               // isVibrate = true;
                break;
            case MotionEvent.ACTION_UP:

//                if(isVibrate) {
//                    Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
//                    vibrator.vibrate(80);
//                }

                Log.i("seek","circleY: " + circleY);
                if(progressListener !=null) {
                    progressListener.onSeekBarProgressChanged(selectedProgress);
                }
                break;
        }
        return true;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        if(gainFocus) {
            Log.i("seek","gain focus");
        } else {
            Log.i("seek", "loss focus");
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

}
