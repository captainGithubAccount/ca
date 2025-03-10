package com.you.company.rtcpgvd.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CustomProgressBar extends View {

    private float currentVal;

    private float minValue;

    private float maxValue;

    private Paint mPaint;


    public CustomProgressBar(Context context) {
        super(context);
    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#2B000000"));
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {

       // drawBackground();
        drawThumb();
        drawText();
    }

    private void drawText(){

    }

    /***
     * 绘制进度条
     */
    private void drawThumb(){

    }


    /***
     * 绘制背景
     */
    private void drawBackground(Canvas canvas){

        float radius = 180f;

        float width = getWidth();
        float height = getHeight();
//        RectF rectF = new RectF()
//        canvas.drawRoundRect(,,,);
    }
}
