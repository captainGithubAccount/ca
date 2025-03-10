package com.you.company.rtcpgvd.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.you.company.rtcpgvd.ConfigMgr;
import com.you.company.rtcpgvd.R;

public class ToastUtils {

    private static Handler handler = new Handler(Looper.getMainLooper());

    public static void show(final Context ctx,final String text) {
        if(Looper.myLooper() == Looper.getMainLooper()){
            Toast.makeText(ctx,text,Toast.LENGTH_SHORT).show();
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx,text,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void show(final String text) {
        show(ConfigMgr.getInstance().globalContext(),text);
    }

    public static void showCorrectToast(String text) {
        LayoutInflater inflater = LayoutInflater.from(ConfigMgr.getInstance().globalContext());
        View customToastView = inflater.inflate(R.layout.layout_roundrect_toast, null);

        ImageView image = customToastView.findViewById(R.id.ic_toast);
        image.setBackgroundResource(R.mipmap.ic_correct_toast);
        TextView txt = customToastView.findViewById(R.id.toast_details);
        txt.setText(text);
        Toast toast = new Toast(ConfigMgr.getInstance().globalContext());
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(customToastView);
        toast.show();
    }

    public static void showCloseToast(String text) {
        LayoutInflater inflater = LayoutInflater.from(ConfigMgr.getInstance().globalContext());
        View customToastView = inflater.inflate(R.layout.layout_roundrect_toast, null);

        ImageView image = customToastView.findViewById(R.id.ic_toast);
        image.setBackgroundResource(R.mipmap.ic_close_toast);
        TextView txt = customToastView.findViewById(R.id.toast_details);
        txt.setText(text);
        Toast toast = new Toast(ConfigMgr.getInstance().globalContext());
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(customToastView);
        toast.show();
    }
}
