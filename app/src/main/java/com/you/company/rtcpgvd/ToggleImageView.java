package com.you.company.rtcpgvd;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class ToggleImageView extends androidx.appcompat.widget.AppCompatImageView {

    private boolean status = true;
    private Drawable ctrlImgOn;
    private Drawable ctrlImgOff;

    public ToggleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ToggleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        if(attrs == null) {
            return;
        }
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.StatusImage);
        int indexCount = typedArray.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int attr = typedArray.getIndex(i);
            if( R.styleable.StatusImage_ctrlStatus == attr)
                status = typedArray.getBoolean(attr,status);
            if(R.styleable.StatusImage_ctrlImgOff == attr)
                ctrlImgOff = typedArray.getDrawable(attr);
            if(R.styleable.StatusImage_ctrlImgOn == attr)
                ctrlImgOn = typedArray.getDrawable(attr);
        }
        if(status) {
            setImageDrawable(ctrlImgOn);
        } else {
            setImageDrawable(ctrlImgOff);
        }
        typedArray.recycle();

    }

    public void switchStatus() {
        if(status) {
            status = false;
            setImageDrawable(ctrlImgOff);
        } else {
            status = true;
            setImageDrawable(ctrlImgOn);
        }
    }

    public boolean getStatus(){
        return status;
    }

}
