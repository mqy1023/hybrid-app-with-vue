package com.shxbd.bbm.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.shxbd.bbm.event.JumpH5Event;

import org.greenrobot.eventbus.EventBus;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    public static final String TYPE = "type"; //这个type是为了Notification更新信息的，这个不明白的朋友可以去搜搜，很多
    public static final String JUMPURL = "jump_url"; // 跳转的url链接

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int type = intent.getIntExtra(TYPE, -1);

        Log.e("type=", type + "");
        String jumpUrl = intent.getStringExtra(JUMPURL);

        if (type != -1) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(type);
        }

        if (action.equals("notification_cancelled")) {
            //处理滑动清除和点击删除事件
            if (!"".equals(jumpUrl) && jumpUrl != null) {
                EventBus.getDefault().post(new JumpH5Event(jumpUrl, "aaaa"));
            }
        }
    }
}
