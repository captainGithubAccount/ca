package com.you.company.rtcpgvd;

import android.content.Context;

public class ConfigMgr {

    private static ConfigMgr instance = new ConfigMgr();

    private Context mContext;

    private ConfigMgr(){

    }

    public static ConfigMgr getInstance (){
        return instance;
    }

    public void attachContext(Context context){
        mContext = context;
    }

    public Context globalContext(){
        return mContext;
    }
}
