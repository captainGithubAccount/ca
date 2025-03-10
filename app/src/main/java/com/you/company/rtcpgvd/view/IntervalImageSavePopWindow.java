package com.you.company.rtcpgvd.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;

public class IntervalImageSavePopWindow extends PopupWindow {

    private IntervalImageSaveLayout mView;

    public interface DismissListener {
        void returnTimeVal(String val);
    }

    DismissListener dismissListener;

    public IntervalImageSavePopWindow(Context context) {
        super(context);
        mView = new IntervalImageSaveLayout(context);
        this.setContentView(mView);
        this.setFocusable(true);
    }

    public void setDismissListener(DismissListener listener) {
        dismissListener = listener;
    }

    public void show(View anchorView) {
        int width = anchorView.getWidth();
        setWidth(width);
        showAsDropDown(anchorView);
    }

    @Override
    public void dismiss() {
        if(dismissListener != null) {
            Log.i("capture","time: "+ mView.getTime());
            dismissListener.returnTimeVal(mView.getTime());
        }
        super.dismiss();
    }

    public static IntervalImageSavePopWindow create(Context context){
        return new IntervalImageSavePopWindow(context);
    }
}
