package com.you.company.rtcpgvd;

import android.app.Application;
import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "868ec2fb6c", true);
        CrashReport.setAllThreadStackEnable(getApplicationContext(), true, false);
    }

    @Override
    protected void attachBaseContext(Context base) {
        ConfigMgr.getInstance().attachContext(base);
        super.attachBaseContext(base);
    }
}
