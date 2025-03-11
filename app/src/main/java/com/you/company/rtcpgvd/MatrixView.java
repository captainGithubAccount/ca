package com.you.company.rtcpgvd;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static com.you.company.rtcpgvd.utils.MatrixImageUtils.TouchMode.TOUCH_CLOSE;
import static com.you.company.rtcpgvd.utils.MatrixImageUtils.TouchMode.TOUCH_CONTROL_5;
import static com.you.company.rtcpgvd.utils.MatrixImageUtils.TouchMode.TOUCH_CONTROL_6;
import static com.you.company.rtcpgvd.utils.MatrixImageUtils.TouchMode.TOUCH_CONTROL_7;
import static com.you.company.rtcpgvd.utils.MatrixImageUtils.TouchMode.TOUCH_OUTSIDE;
import static com.you.company.rtcpgvd.utils.MatrixImageUtils.getDistanceOf2Points;
import static com.you.company.rtcpgvd.utils.MatrixImageUtils.getImageRectF;
import static com.you.company.rtcpgvd.utils.MatrixImageUtils.getTouchMode;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.you.company.rtcpgvd.utils.MatrixImageUtils;

public class MatrixView extends AppCompatImageView {

    private float mWidth = 0;

    private float mHeight = 0;

    private boolean mFirstDraw = true;

    private boolean mShowFrame = false;

    private int shapeType = 0;

    private Matrix mImgMatrix = new Matrix();

    private Paint mPaint;

    private Paint mTextPaint;

    // 触摸模式
    private MatrixImageUtils.TouchMode touchMode = null;

    private float mDownX = 0f;  //按下店坐标

    private float mDownY = 0f;

    private float mLastX = 0f;

    private float mLastY = 0f;

    private Bitmap mCloseIcon;

    private int mFrameColor = Color.parseColor("#1677FF");

    private float mLineWidth = dp2px(getContext(),2f);

    public float mScaleDotRadius = dp2px(getContext(),12f);

    public float mCloseDotRadius = dp2px(getContext(),18f);

    private ImageLocationListener imageLocationListener;

    private ImageCloseClickListener imageCloseClickListener;

    private int  imageMatrixId;

    public MatrixView(Context context) {
        this(context,null);
    }

    public MatrixView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MatrixView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMatrixViewId();
        init();
        initAttributes(attrs);
    }

    private void initMatrixViewId() {
        imageMatrixId = MatrixImageUtils.getCaptureRegionId();
        Log.i("capture","imageMatrixId: "+ imageMatrixId);
        MatrixImageUtils.flagCaptureIdDispatch(imageMatrixId);
    }


    private void initAttributes(AttributeSet attrs){
        if(attrs == null)
            return;
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MatrixImageView);
        int indexCount = typedArray.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int attr = typedArray.getIndex(i);
            if( R.styleable.MatrixImageView_fcLineWidth == attr)
              mLineWidth = typedArray.getDimension(attr,mLineWidth);
            if(R.styleable.MatrixImageView_fcScaleDotRadius == attr)
                mScaleDotRadius = typedArray.getDimension(attr,mScaleDotRadius);
            if(R.styleable.MatrixImageView_fcFrameColor == attr)
                mFrameColor = typedArray.getColor(attr,mFrameColor);
            if(R.styleable.MatrixImageView_showControlFrame ==attr) {
                mShowFrame = typedArray.getBoolean(attr,mShowFrame);
            }
            if(R.styleable.MatrixImageView_fcShapeType == attr) {
                shapeType = typedArray.getInteger(attr,shapeType);
            }
            if(R.styleable.MatrixImageView_fcRotateDotRadius == attr) {
                mCloseDotRadius = typedArray.getDimension(attr,mCloseDotRadius);
            }
        }
        typedArray.recycle();
    }

    private void init(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setColor(mFrameColor);
        mPaint.setStyle(Paint.Style.FILL);

        //matrix模式
        setScaleType(ScaleType.MATRIX);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(32);
        Typeface typeface =  Typeface.defaultFromStyle(Typeface.BOLD);
        mTextPaint.setTypeface(typeface);

        mCloseIcon = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_close_frame);
        //int closeWidth = (int)(mCloseDotRadius * 1.6f);
        ///mCloseIcon = Bitmap.createScaledBitmap(mCloseIcon,closeWidth,closeWidth,true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(canvas == null || getDrawable() == null) {
            return;
        }

        RectF imgRect = getImageRectF(this);

        float left = imgRect.left;
        float top = imgRect.top;
        float right = imgRect.right;
        float bottom = imgRect.bottom;

        if(mFirstDraw) {
            mFirstDraw = false;
            float centerX = (mWidth /2);
            float centerY = (mHeight /2 );
            float imageWidth = right - left;
            float imageHeight = bottom - top;
            mImgMatrix.postTranslate(centerX - imageWidth /2,centerY - imageHeight /2 );
            //如果图片较大 缩放0.5倍
            if(imageWidth > getWidth() || imageHeight > getHeight()) {
                mImgMatrix.postScale(0.5f,0.5f,centerX,centerY);

            }
            setImageMatrix(mImgMatrix);
        }

        canvas.drawText(imageMatrixId + "",(right-left) /2 + left, (bottom - top )/2 + top, mTextPaint);

        if(!mShowFrame) {
            return;
        }

        //上边框
        canvas.drawLine(left,top,right,top,mPaint);

        //下边框
        canvas.drawLine(left,bottom,right,bottom,mPaint);

        // 左边框
        canvas.drawLine(left,top,left, bottom , mPaint);

        // 有边框
        canvas.drawLine(right,top,right,bottom,mPaint);

        // 左上角控制点

        canvas.drawCircle(left,top,mScaleDotRadius,mPaint);

        // 右上角控制点

        canvas.drawCircle(right,top,mScaleDotRadius,mPaint);

        //左中间控制点
        if(shapeType == 1 )
        canvas.drawCircle(left,top + (bottom - top ) /2 ,mScaleDotRadius, mPaint);

        //右中间控制点
        if(shapeType == 1)
        canvas.drawCircle(right, top  + (bottom - top)/2, mScaleDotRadius,mPaint);

        // 左下角控制点
        canvas.drawCircle(left,bottom,mScaleDotRadius,mPaint);

        // 右下角控制点
        canvas.drawCircle(right,bottom,mScaleDotRadius,mPaint);

        //下中间控制点
        if(shapeType == 1)
        canvas.drawCircle((right - left) /2 + left,bottom,mScaleDotRadius,mPaint);

        //上中间控制点

       // canvas.drawCircle((right - left)/2 + left,top,mScaleDotRadius,mPaint);

        float middleX = (right - left) /2 + left;

        // 上中间控制点，旋转
        float rotateLine = mCloseDotRadius / 3;

        Log.i("capture","top: " + top + " rotateLine " + rotateLine);

        canvas.drawLine(middleX, top - rotateLine - mCloseIcon.getWidth()/2, middleX, top, mPaint);
        // 删除图标
        canvas.drawBitmap(
                mCloseIcon,
                middleX - mCloseIcon.getWidth() / 2,
                top - rotateLine - mCloseDotRadius - mCloseIcon.getWidth() / 2,
                mPaint
        );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("scale","event: "+ event.getAction());
        if(event ==  null || getDrawable() == null) {
            return super.onTouchEvent(event);
        }

        float x = event.getX();
        float y = event.getY();

        //图片显示区域
        RectF imageRect = getImageRectF(this);

        // 图片中心点坐标
        float centerX = (imageRect.right - imageRect.left) /2 + imageRect.left;

        // 图片中心点y坐标
        float centerY = (imageRect.bottom - imageRect.top )/2 + imageRect.top;

        switch(event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                touchMode = getTouchMode(this,x,y);
                Log.i("capture","touchMode: " + touchMode + " matrixId: "+ imageMatrixId);
                if(touchMode == TOUCH_OUTSIDE) {
                    ImageLocationListener imageLocationListener = MatrixViewHelper.getInstance().getListener(imageMatrixId);
                    if(imageLocationListener != null) {
                        RegionType type;
                        if(shapeType == 1) {
                            type = RegionType.RECT;
                        } else {
                            type = RegionType.CIRCLE;
                        }
                        Log.i("capture","type: "+ type +"  imageMatrixId: " + imageMatrixId);
                         imageLocationListener.imageLocation(imageMatrixId,imageRect.left,imageRect.right,imageRect.top,imageRect.bottom,type);
                    } else {
                        Log.i("capture","imageLocationListener is null");
                    }
                    mShowFrame = false;
                    invalidate();
                    return super.onTouchEvent(event);
                }

                // 旋转控制点，点击后以图片中心为基准，计算当前旋转角度
                if (touchMode == TOUCH_CLOSE) {
                    // 关闭图标
                    if(imageCloseClickListener != null) {
                        imageCloseClickListener.onImageClose(imageMatrixId);

                        ViewGroup parent = (ViewGroup) getParent();
                        if(parent != null) {
                            ViewGroup grand = (ViewGroup) parent.getParent();
                            grand.removeView(parent);
                        }

                        return true;
                    }
                }

                if(shapeType == 2) {
                    if(touchMode == TOUCH_CONTROL_5 ||touchMode == TOUCH_CONTROL_6 || touchMode == TOUCH_CONTROL_7) {
                        return true;
                    }
                }


                mDownX = x;
                mDownY = y;

                mLastX = x;
                mLastY = y;

                mShowFrame = true;
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                Log.i("scale","MotionEvent.Action_MOve");
                touchMove(x,y,imageRect);
                mLastX = x;
                mLastY = y;
                invalidate();
                float offsetX = Math.abs(x - mDownX);
                float offsetY = Math.abs(y - mDownY);
                float offset = dp2px(getContext(),10f);
                return true;
            case MotionEvent.ACTION_UP: {
                touchMode = null;

            }
            break;
            default:
                Log.i("scale", "event: " + event.getAction());
        }
        return super.onTouchEvent(event);
    }

    private void touchMove(float x, float y,  RectF imageRect) {
        // 左上角x坐标
        float left = imageRect.left;
        // 左上角y坐标
        float top = imageRect.top;
        // 右下角x坐标
        float right = imageRect.right;
        // 右下角y坐标
        float bottom = imageRect.bottom;
        // 总的缩放距离，斜角
        float totalTransOblique = getDistanceOf2Points(left, top, right, bottom);
        // 总的缩放距离，水平;
        float totalTransHorizontal = getDistanceOf2Points(left, top, right, top);
        // 总的缩放距离，垂直
        float totalTransVertical = getDistanceOf2Points(left, top, left, bottom);
        // 当前缩放距离
        float scaleTrans = getDistanceOf2Points(mLastX, mLastY, x, y);
        // 缩放系数，x轴方向
        float scaleFactorX  =1;
        // 缩放系数，y轴方向
        float scaleFactorY = 1f;
        // 缩放基准点x坐标
        float scaleBaseX = 1f;
        // 缩放基准点y坐标
        float scaleBaseY = 1f;

        switch (touchMode) {
            case TOUCH_IMAGE : {
                mImgMatrix.postTranslate(x - mLastX, y - mLastY);
                setImageMatrix(mImgMatrix);
                return;
            }
            case TOUCH_CONTROL_1: {
                // 缩小
                if (x - mLastX > 0) {
                     scaleFactorX = (totalTransOblique - scaleTrans) / totalTransOblique;
                } else {
                     scaleFactorX = (totalTransOblique + scaleTrans) / totalTransOblique;
                }
                scaleFactorY = scaleFactorX;
                // 右下角
                scaleBaseX = imageRect.right;
                scaleBaseY = imageRect.bottom;
            }
            break;
            case TOUCH_CONTROL_2: {
                Log.i("scale","touch_control_2 move");
                // 缩小
                 if (x - mLastX < 0) {
                     scaleFactorX = (totalTransOblique - scaleTrans) / totalTransOblique;
                } else {
                     scaleFactorX = (totalTransOblique + scaleTrans) / totalTransOblique;
                }
                scaleFactorY = scaleFactorX;
                // 左下角
                scaleBaseX = imageRect.left;
                scaleBaseY = imageRect.bottom;
            }
            break;
            case TOUCH_CONTROL_3 : {
                // 缩小
                 if (x - mLastX > 0) {
                     scaleFactorX = (totalTransOblique - scaleTrans) / totalTransOblique;
                } else {
                     scaleFactorX =(totalTransOblique + scaleTrans) / totalTransOblique;
                }
                scaleFactorY = scaleFactorX;
                // 右上角
                scaleBaseX = imageRect.right;
                scaleBaseY = imageRect.top;
            }
            break;
            case TOUCH_CONTROL_4 : {
                // 缩小
                 if (x - mLastX < 0) {
                     scaleFactorX = (totalTransOblique - scaleTrans) / totalTransOblique;
                } else {
                     scaleFactorX =(totalTransOblique + scaleTrans) / totalTransOblique;
                }
                scaleFactorY = scaleFactorX;
                // 左上角
                scaleBaseX = imageRect.left;
                scaleBaseY = imageRect.top;
            }
            break;
            case TOUCH_CONTROL_5: {
                if(shapeType == 2)
                    return;
                // 缩小
                if (x - mLastX > 0) {
                    scaleFactorX =  (totalTransHorizontal - scaleTrans) / totalTransHorizontal;
                } else {
                    scaleFactorX = (totalTransHorizontal + scaleTrans) / totalTransHorizontal;
                }
                scaleFactorY = 1f;
                // 右上角
                scaleBaseX = imageRect.right;
                scaleBaseY = imageRect.top;
            }
            break;
            case TOUCH_CONTROL_6 : {
                if(shapeType == 2) {
                    return;
                }
                // 缩小
                 if (x - mLastX < 0) {
                     scaleFactorX = (totalTransHorizontal - scaleTrans) / totalTransHorizontal;
                } else {
                     scaleFactorX = (totalTransHorizontal + scaleTrans) / totalTransHorizontal;
                }
                scaleFactorY = 1f;
                // 左上角
                scaleBaseX = imageRect.left;
                scaleBaseY = imageRect.top;
            }
            break;
            case TOUCH_CONTROL_7 : {
                if(shapeType ==2) {
                    return;
                }
                // 缩小
                scaleFactorX = 1f;
                if (y - mLastY < 0) {
                    scaleFactorY =   (totalTransVertical - scaleTrans) / totalTransVertical;
                } else {
                    scaleFactorY =   (totalTransVertical + scaleTrans) / totalTransVertical;
                }
                // 左上角
                scaleBaseX = imageRect.left;
                scaleBaseY = imageRect.top;
            }
            break;
        }

        // 最小缩放值限制
        Matrix scaleMatrix = new Matrix(mImgMatrix);
        scaleMatrix.postScale(scaleFactorX, scaleFactorY, scaleBaseX, scaleBaseY);
        RectF scaleRectF = getImageRectF(this, scaleMatrix);
        if (scaleRectF.right - scaleRectF.left < mScaleDotRadius * 1
                || scaleRectF.bottom - scaleRectF.top < mScaleDotRadius * 1
        ) {
            return;
        }
        Log.i("scale","postScale");
        // 缩放
        mImgMatrix.postScale(scaleFactorX, scaleFactorY, scaleBaseX, scaleBaseY);
        Log.i("scale","set Image Matrix");
        setImageMatrix(mImgMatrix);
    }

    private float dp2px(Context context, Float dp) {
        return TypedValue.applyDimension(COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    interface ImageCloseClickListener  {
        void onImageClose(int matrixId);
    }

    interface ImageLocationListener {
        void imageLocation(int matrixId,float startX,float startY,float endX,float endY,RegionType type);
    }

    public void registerImageLocationListener(ImageLocationListener listener){
        Log.i("capture","register matrixId: "+ imageMatrixId);
        MatrixViewHelper.getInstance().addListener(imageMatrixId,listener);
        MatrixViewHelper.getInstance().print();
    }

    public void registerImageCloseListener(ImageCloseClickListener listener) {
        this.imageCloseClickListener = listener;
    }

    public int getImageMatrixId(){
        return imageMatrixId;
    }
}
