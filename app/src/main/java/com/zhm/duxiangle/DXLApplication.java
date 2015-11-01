package com.zhm.duxiangle;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

import io.rong.imkit.RongIM;

public class DXLApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(getApplicationContext());

        /**
         * 初始化融云
         */
        RongIM.init(this);
    }

}