package com.example.a75800.zes_psngertransportation.Util;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by 75800 on 2017/8/19.
 */

public class MyLeanCloudApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"AICrPFCrXhH5dGQAGUE1tTtb-gzGzoHsz","7OEKPLFlv6aiQRTGQB6T6A8m");
        AVOSCloud.setDebugLogEnabled(true);
    }
}
