package com.you.company.rtcpgvd;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ScaleMoveDragView extends View {

    private Matrix matrix = new Matrix();
    private RectF rectF = new RectF(100,100,300,300);
    private PointF startPoint = new PointF();
    private float startAngle = 0;
    private float scale = 1;
    private boolean isRotating;
    private boolean isScaling;
    private float[] pointArr = new float[2];

    public ScaleMoveDragView(Context context) {
        this(context,null);
    }

    public ScaleMoveDragView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        //canvas.setMatrix(matrix);
        Log.i("scale","left: "+ rectF.left + " right: " + rectF.right + "top: " + rectF.top + " bottom: "+ rectF.bottom);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rectF,paint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("scale","action down");
                startPoint.set(event.getX(),event.getY());
                pointArr[0] = startPoint.x;
                pointArr[1] = startPoint.y;
                matrix.postRotate(45.0f);
                matrix.mapRect(rectF);
                break;
            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount() == 2) {
                    Log.i("scale","two pointer down");
                    if(!isScaling) {
                        Log.i("scale", "isScaling:  " + isScaling);
                        isScaling = true;
                        startAngle = calculateAngle(event);
                    }
                    float newAngle = calculateAngle(event);
                    float scaleFactor = calculateScale(event);
                    Log.i("scale",scaleFactor+"");
                    scaleFactor =  Math.min(3f,scaleFactor);
                    matrix.postScale(scaleFactor, scaleFactor, pointArr[0], pointArr[1]);
                  //  matrix.postRotate((newAngle - startAngle), pointArr[0], pointArr[1]);
                   // matrix.mapRect(rectF);
                } else if(event.getPointerCount() == 1 && isScaling) {
                    Log.i("scale","one pointer count");
                    isRotating = true;
                    float newAngle = calculateAngle(event);
                    matrix.postRotate(newAngle - startAngle,startPoint.x,startPoint.y);
                } else {
                    Log.i("scale","translate");
                    PointF movePoint = new PointF(event.getX(),event.getY());
                    matrix.postTranslate(movePoint.x - pointArr[0],movePoint.y - pointArr[1]);
                    pointArr[0] = movePoint.x;
                    pointArr[1] = movePoint.y;
                    matrix.mapRect(rectF);
                    startPoint.set(movePoint);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.i("scale","second pointer down");
                isScaling = true;
                startAngle = calculateAngle(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                isScaling = false;
                isRotating = false;
                break;
        }
        invalidate();
        return true;
    }

    private float calculateAngle(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float) Math.atan2(dy, dx);
    }

    private float calculateScale(MotionEvent event) {
        float dist = distance(event);
        return dist / distance(event, 0);
    }

    private float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private float distance(MotionEvent event, int pointer) {
        float dx = event.getX(pointer) - pointArr[0];
        float dy = event.getY(pointer) - pointArr[1];
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
