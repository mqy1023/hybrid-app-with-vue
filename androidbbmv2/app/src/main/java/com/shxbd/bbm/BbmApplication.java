package com.shxbd.bbm;

import android.app.Application;

import com.shxbd.bbm.utils.SharedPreferencesHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import cn.jpush.android.api.JPushInterface;

public class BbmApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);

        UMConfigure.setLogEnabled(true);

        // 选用MANUAL页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL);

        SharedPreferencesHelper.init(this);

        //开启极光
        JPushInterface.setDebugMode(BuildConfig.DEBUG ? true : false);
        JPushInterface.init(this);

//        Log.e("jiguang", JPushInterface.getRegistrationID(this));
    }
}
