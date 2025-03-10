package com.you.company.rtcpgvd;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;

import com.you.company.rtcpgvd.utils.MatrixImageUtils;

import java.nio.ByteBuffer;


public class ImageUtils {

    public static Bitmap imageToBitmap(Image image) {
            if(image == null)
                return null;
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
            return bitmap;
    }

    public static float calculateGrayValueRect(Bitmap bitmap,float startX,float endX,float startY,float endY) {
        long count = 0;
        float sum = 0;
        for(int i= (int)startX; i < endX; i++) {
            for(int j = (int)startY; j < endY; j++) {
                int pixel = bitmap.getPixel(i,j);

                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                sum += (red + green + blue) /3;
                count++;
            }
        }
        float averageGrayVal = sum / count;
        Log.i("capture","rect average gray value : " + sum / count);
        return averageGrayVal;
    }


    public static float calculateGrayValueCircle(Bitmap bitmap,float startX,float endX,float startY,float endY) {
        float raidu = MatrixImageUtils.getDistanceOf2Points(startX,startY,endX,endY) / 2;
        float centerX = (endX - startX) / 2 + startX;
        float centerY = (endY - startY) / 2 + startY;
        int numPoints = 360;
        float sum = 0;  //
        long count = 0;
        for(float i = startX; i <= endX; i++) {
            for(float j = startY; j <=endY; j++) {
                float len = (Math.abs(i - centerX) * Math.abs(i - centerX)) + Math.abs(j - centerY) *
                Math.abs(j - centerY);
                if(len <= raidu * raidu) {
                    int pixel = bitmap.getPixel((int)i,(int)j);
                    int red = Color.red(pixel);
                    int green = Color.red(pixel);
                    int blue = Color.red(pixel);

                    sum += (red + green + blue)/3;
                    count++;
                }
            }
        }
        float averageGrayVal = sum / count;
        Log.i("capture","circle average gray val :" + averageGrayVal);
        return averageGrayVal;
    }
}
