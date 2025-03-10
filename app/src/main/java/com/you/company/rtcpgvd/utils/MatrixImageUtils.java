package com.you.company.rtcpgvd.utils;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;

import com.you.company.rtcpgvd.MatrixView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.you.company.rtcpgvd.utils.MatrixImageUtils.TouchMode.TOUCH_CLOSE;
import static com.you.company.rtcpgvd.utils.MatrixImageUtils.TouchMode.TOUCH_CONTROL_6;
import static com.you.company.rtcpgvd.utils.MatrixImageUtils.TouchMode.TOUCH_CONTROL_7;

public class MatrixImageUtils {

    private static HashMap<Integer,Boolean> captureIdDispatcher = new HashMap<>();

    static {
        captureIdDispatcher.put(1,false);
        captureIdDispatcher.put(2,false);
        captureIdDispatcher.put(3,false);
        captureIdDispatcher.put(4,false);
        captureIdDispatcher.put(5,false);
        captureIdDispatcher.put(6,false);
        captureIdDispatcher.put(7,false);
        captureIdDispatcher.put(8,false);
        captureIdDispatcher.put(9,false);
        captureIdDispatcher.put(10,false);
    }

    public static TouchMode getTouchMode(MatrixView view,float x,float y) {

        RectF imageRect = getImageRectF(view);
        float left = imageRect.left;
        float top = imageRect.top;
        float right = imageRect.right;
        float bottom = imageRect.bottom;

        float offset = view.mScaleDotRadius * 3;

        RectF rect1 = new RectF(left - offset,top - offset, left + offset,top + offset);
        if(rect1.contains(x,y)) {
            return TouchMode.TOUCH_CONTROL_1;
        }

        RectF rect2 = new RectF(right - offset,top -offset,right +offset,top + offset);
        if(rect2.contains(x,y)) {
            return TouchMode.TOUCH_CONTROL_2;
        }

        RectF rect3 = new RectF(left - offset, bottom - offset,left + offset, bottom + offset);
        if(rect3.contains(x,y)) {
            return TouchMode.TOUCH_CONTROL_3;
        }

        RectF rect4 = new RectF(right - offset,bottom - offset,right + offset, bottom + offset);
        if(rect4.contains(x,y)) {
            return TouchMode.TOUCH_CONTROL_4;
        }

        float rect5Y = (bottom - top) / 2 + top;
        RectF rect5 = new RectF(left - offset, rect5Y - offset, left + offset, rect5Y + offset);
        if (rect5.contains(x, y)) {
            return TouchMode.TOUCH_CONTROL_5;
        }

        float rect6Y = (bottom - top) / 2 + top;
        RectF rect6 = new RectF(right - offset, rect6Y - offset, right + offset, rect6Y + offset);
        if (rect6.contains(x, y)) {
            return TOUCH_CONTROL_6;
        }
        float rect7X = (right - left) / 2 + left;
        RectF rect7 = new RectF(rect7X - offset, bottom - offset, rect7X + offset, bottom + offset);
        if (rect7.contains(x, y)) {
            return TOUCH_CONTROL_7;
        }

        // 旋转控制点半径
        float closeDotRadius = view.mCloseDotRadius;
        // 旋转控制点的中心x坐标
        float rectRotateX = (right - left) / 2 + left;
        // rotateDotRadius / 3 是连接线的长度
        RectF rectRotate = new RectF(
                rectRotateX - closeDotRadius,
                top - closeDotRadius / 3 - closeDotRadius * 2,
                rectRotateX + closeDotRadius,
                top - closeDotRadius / 3
                );
        if (rectRotate.contains(x, y)) {
            return TOUCH_CLOSE;
        }

        if (imageRect.contains(x, y)) {
            return TouchMode.TOUCH_IMAGE;
        }
        return TouchMode.TOUCH_OUTSIDE;
    }

    public static RectF getImageRectF(ImageView view) {
        Matrix matrix = view.getImageMatrix();
        return getImageRectF(view,matrix);
    }

    public static RectF getImageRectF(ImageView view , Matrix matrix) {
        Rect bounds = view.getDrawable().getBounds();
        RectF rectF = new RectF();
        matrix.mapRect(
                rectF,
                new RectF(
                        bounds.left,
                        bounds.top,
                        bounds.right,
                        bounds.bottom
                )
        );
        return rectF;
    }

    public static float getDistanceOf2Points(float x1,float y1,float x2,float y2) {
        return (float) Math.sqrt(Math.pow(x1 -x2,2) + Math.pow(y1-y2,2));
    }

    public enum  TouchMode {

        TOUCH_OUTSIDE,

        TOUCH_IMAGE,
        /**
         * 左上角控制点，等比缩放
         */
        TOUCH_CONTROL_1,

        /**
         * 右上角控制点，等比缩放
         */
        TOUCH_CONTROL_2,

        /**
         * 左下角控制点，等比缩放
         */
        TOUCH_CONTROL_3,

        /**
         * 右下角控制点，等比缩放
         */
        TOUCH_CONTROL_4,

        /**
         * 左中间控制点，横向缩放
         */
        TOUCH_CONTROL_5,

        /**
         * 右中间控制点，横向缩放
         */
        TOUCH_CONTROL_6,

        /**
         * 下中间控制点，竖向缩放
         */
        TOUCH_CONTROL_7,

        /**
         * 删除图标
         */
        TOUCH_CLOSE,
    }

    /**
     * 分配captureId
     * @return
     */
    public static int getCaptureRegionId() {
        if(captureIdDispatcher.size() > 0) {
           Iterator<Map.Entry<Integer,Boolean>> iterator =  captureIdDispatcher.entrySet().iterator();
           while(iterator.hasNext()) {
               Map.Entry<Integer,Boolean> entry =  iterator.next();
               if(entry.getValue() == false){
                   return entry.getKey();
               }
           }
        }
        return 0;
    }

    /**
     * 回收id
     */
    public static void recycleCaptureId(int id){
        if(captureIdDispatcher.size() > 0) {
            if(captureIdDispatcher.containsKey(id)) {
                captureIdDispatcher.put(id,false);
            }
        }
    }

    public static void flagCaptureIdDispatch(int id) {
        if(captureIdDispatcher.size() > 0) {
            if(captureIdDispatcher.containsKey(id)) {
                captureIdDispatcher.put(id,true);
            }
        }
    }
}


