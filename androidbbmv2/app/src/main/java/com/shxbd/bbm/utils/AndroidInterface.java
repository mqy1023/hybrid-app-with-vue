package com.shxbd.bbm.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.webkit.JavascriptInterface;

import com.just.agentweb.AgentWeb;
import com.shxbd.bbm.event.JPushEvent;
import com.shxbd.bbm.event.JumpBrowserEvent;
import com.shxbd.bbm.event.PickerImgUploadEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedHashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

public class AndroidInterface {

    private AgentWeb mAgentWeb;
    private Context context;

    public AndroidInterface(AgentWeb mAgentWeb, Context context) {
        this.mAgentWeb = mAgentWeb;
        this.context = context;
    }

    @JavascriptInterface // 挑选图片并上传
    public void openPickerImgUpload(String token) {
//        Toast.makeText(context, token, Toast.LENGTH_LONG).show();
        EventBus.getDefault().post(new PickerImgUploadEvent(token));
    }

    @JavascriptInterface // 根据用户id设置极光推送的别名
    public void setJPushAlias(String userId) {
        SharedPreferencesHelper.put(Constants.USER_ID, userId);
//        if (JPushInterface.isPushStopped(context)) {
//            JPushInterface.resumePush(context); // 开启推送
//        }
        JPushInterface.setAlias(context, 1, userId);
    }
    @JavascriptInterface // 根据代理级别设置极光推送的tag标签
    public void setJPushTag(String userType) {
        Set<String> tagSet = new LinkedHashSet<String>();
        tagSet.add(userType);
        JPushInterface.setTags(context, 1, tagSet);
    }

    @JavascriptInterface // 用户已登录过了
    public void setUserHadOpen() {
        SharedPreferencesHelper.put(Constants.IS_HAVE_USE, true);
    }
    @JavascriptInterface // 0:打开登录页，1: 打开一级代理的home，2：打开二级代理的dispatch
    public void setOpenType(String type) {
        SharedPreferencesHelper.put(Constants.OPEN_TYPE, type);
    }
    @JavascriptInterface // 清除用户id，停止推送
    public void clearUserId() {
        SharedPreferencesHelper.put(Constants.USER_ID, "");
//        JPushInterface.stopPush(context); // 停止推送
        // 清空极光推送消息
        NotificationManager nm =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
    }

    @JavascriptInterface // 跳转浏览器
    public void jumpBrowser(String url) {
//        Log.e("jumpBrowser", url);
        EventBus.getDefault().post(new JumpBrowserEvent(url));
    }

    @JavascriptInterface // 获取极光id
    public void getJPushId() {
        EventBus.getDefault().post(new JPushEvent());
    }

    @JavascriptInterface // 清空缓存
    public void clearAndroidCache() {
        DataCleanManager.clearAllCache(context);
    }

    @JavascriptInterface // 代理app烙印
    public void isBbmAgentApp() {
        mAgentWeb.getJsAccessEntrace().quickCallJs("callByNativeIsBbmAgentApp");
    }

}
