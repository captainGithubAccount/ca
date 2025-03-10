package com.you.company.rtcpgvd.utils;


import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.you.company.rtcpgvd.view.IntervalDialog;
import com.you.company.rtcpgvd.view.SampleCompletDialog;

public class DialogUtil {

    public static void showDialog(FragmentManager manager,IntervalDialog.OnTimeSetFinish listener) {
        FragmentTransaction transaction = manager.beginTransaction();
        IntervalDialog dialog = new IntervalDialog();
        dialog.setTimeSetListener(listener);
        dialog.show(manager,"");
    }

    public static void showTimeCompletDialog(FragmentManager manager) {
        SampleCompletDialog dialog = new SampleCompletDialog();
        dialog.show(manager,"");
    }
}
