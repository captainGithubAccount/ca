package com.you.company.rtcpgvd;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class MyRectView extends View {
    private Paint paint;
    private Rect rect;
    private int left=200,right=700,top=200,bottom=500;
    private int strokeWidth=10;
    private boolean onVerticalLeftStroke=false,onVerticalRightStroke=false; //是否在左边框上；右边框上
    private boolean onHorizontalUpStroke=false,onHorizontalDownStroke=false;//是否在上边框上；下边框上
    private float currentX=0,currentY=0;
    private float screenWidth,screenHeight;

    public MyRectView(Context context) {
        this(context,null);
    }

    public MyRectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        DisplayMetrics metrics =  context.getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
    }

    private void init(){
        paint=new Paint();
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        rect=new Rect(left,top,right,bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(rect,paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x=event.getX();
        float y=event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                currentX=x;currentY=y;
                onHorizontalRectStroke(x,y);
                onVerticalRectStroke(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                currentX=x;currentY=y;
                moveRectStroke();
                break;
            case MotionEvent.ACTION_UP:
                onHorizontalUpStroke=false;
                onHorizontalDownStroke=false;
                onVerticalLeftStroke=false;
                onVerticalRightStroke=false;
                break;
        }
        return true;
    }

    //通过边框移动矩形（重画）
    private void moveRectStroke(){
        if(onVerticalLeftStroke){
            left=(int)currentX;
            //检查左边框的边界
            if(left<0)left=0;
            if(left>right-25)left=right-25;
            redrawRect();
        }else if(onVerticalRightStroke){
            right=(int)currentX;
            //检查右边框的边界
            if(right>screenWidth)right=900;
            if(right<left+25)right=left+25;
            redrawRect();
        }else if(onHorizontalUpStroke){
            top=(int)currentY;
            //检查上边框的边界
            if(top<0)top=0;
            if(top>bottom-25)top=bottom-25;
            redrawRect();
        }else if(onHorizontalDownStroke){
            bottom=(int)currentY;
            //检查下边框的边界
            if(bottom>screenHeight)bottom=1500;
            if(bottom<top+25)bottom=top+25;
            redrawRect();
        }
    }

    //重绘矩形
    private void redrawRect(){
        rect.set(left,top,right,bottom);
        invalidate();
    }

    //是否在矩形边框上
    private void onVerticalRectStroke(float x,float y){
        //在竖边框上
        if(y>=top && y<=bottom){
            //在左边框上(在一定的误差范围内)
            if(x<=left+strokeWidth*5 && x>=left-strokeWidth*5){
                onVerticalLeftStroke=true;
            }
            //在右边框上(在一定的误差范围内)
            else if(x<=right+strokeWidth*5 && x>=right-strokeWidth*5){
                onVerticalRightStroke=true;
            }
        }
    }

    //是否在矩形边框上
    private void onHorizontalRectStroke(float x,float y){
        //在横边框上
        if(x>=left && x<=right){
            //在上边框上(在一定的误差范围内)
            if(y<=top+strokeWidth*5 && y>=top-strokeWidth*5){
                onHorizontalUpStroke=true;
            }
            //在下边框上(在一定的误差范围内)
            else if(y<=bottom+strokeWidth*5 && y>=bottom-strokeWidth*5) {
                onHorizontalDownStroke=true;
            }
        }
    }
}
