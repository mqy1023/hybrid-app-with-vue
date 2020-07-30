package com.shxbd.bbm.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.request.target.NotificationTarget;
import com.google.gson.Gson;
import com.shxbd.bbm.R;
import com.shxbd.bbm.bean.PushMessage;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 自定义JPush message 接收器,包括操作tag/alias的结果返回(仅仅包含tag/alias新接口部分)
 */
public class MyJPushMessageReceiver extends JPushMessageReceiver {

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        // TagAliasOperatorHelper.getInstance().onTagOperatorResult(context,jPushMessage);

        super.onTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        //TagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context,jPushMessage);
        super.onCheckTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        // TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context,jPushMessage);
        super.onAliasOperatorResult(context, jPushMessage);
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        // TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context,jPushMessage);
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }

    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        String extras = customMessage.extra;
        buildNotification1(context, extras);
        super.onMessage(context, customMessage);
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage message) { // 接受到通知消息

        super.onNotifyMessageArrived(context, message);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) { // 点击打开通知消息
        super.onNotifyMessageOpened(context, message);

    }

    private static final String PRIMARY_CHANNEL = "bbm";
    private NotificationManager mNotificationManager;
    private Notification notification;

    public void buildNotification1(final Context context, final String content) {
        final PushMessage pushMessage = new Gson().fromJson(content, PushMessage.class);

        NotificationChannel mNotificationChannel;
        NotificationCompat.Builder mNotification;
        mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel = new NotificationChannel(PRIMARY_CHANNEL, pushMessage.getTitle(), NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(mNotificationChannel);

            mNotification = new NotificationCompat.Builder(context, PRIMARY_CHANNEL);
            mNotification.setChannelId(PRIMARY_CHANNEL);
        } else {
            mNotification = new NotificationCompat.Builder(context);
        }

        // 删除通知
        PendingIntent clickPendingIntent = buildDelContent(context, Integer.parseInt(pushMessage.getMessage_id()), pushMessage.getJump_url());

        mNotification.setSmallIcon(R.mipmap.ic_launcher);
        mNotification.setContentTitle(pushMessage.getTitle());
//        mNotification.setContentText(pushMessage.getMessage_id() + pushMessage.getJump_url() + pushMessage.getContent());
        mNotification.setContentText(pushMessage.getContent());
        mNotification.setContentIntent(clickPendingIntent);


        notification = mNotification.build();
        mNotificationManager.notify(Integer.parseInt(pushMessage.getMessage_id()), notification);
    }

    public PendingIntent buildDelContent(final Context context, final int notificationId, final  String jumpUrl) {

        Log.e("type==", notificationId + "");

        Intent intentCancel = new Intent(context, NotificationBroadcastReceiver.class);
        intentCancel.setAction("notification_cancelled");
        intentCancel.putExtra(NotificationBroadcastReceiver.TYPE, notificationId); // type的值
        intentCancel.putExtra(NotificationBroadcastReceiver.JUMPURL, jumpUrl); // type的值
        int id = (int) (System.currentTimeMillis() / 1000);
        return PendingIntent.getBroadcast(context, id, intentCancel, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void buildNotification(final Context context, final String content) {
        final PushMessage pushMessage = new Gson().fromJson(content, PushMessage.class);
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "notification channel";
            // 用户可以看到的通知渠道的描述
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel("1", name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            notificationManager.createNotificationChannel(mChannel);
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.item_notification);
        remoteViews.setTextViewText(R.id.noti_title, pushMessage.getTitle());
        remoteViews.setTextViewText(R.id.noti_context, pushMessage.getContent());
        //需要修改字符串
        int hashCode = pushMessage.hashCode();
        PendingIntent clickPendingIntent = buildClickContent(context, content, hashCode);
        final Notification notification = new NotificationCompat.Builder(context)
                .setContent(remoteViews)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setChannelId("1")
                .build();
        if (Build.VERSION.SDK_INT >= 16) {
            notification.bigContentView = remoteViews;
        }
        final NotificationTarget notificationTarget = new NotificationTarget(context, R.id.noti_icon, remoteViews, notification, hashCode);
//        if (!TextUtils.isEmpty(pushMessage.getImage_url())) {
//            RequestOptions requestOptions = new RequestOptions().skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher).dontAnimate();
//
//            Glide.with(context).asBitmap().load(pushMessage.getImage_url()).apply(requestOptions).into(notificationTarget);
//        }
        notification.contentIntent = clickPendingIntent;
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT;
        notification.defaults = Notification.DEFAULT_ALL;
        notificationManager.notify(hashCode, notification);
    }

    public PendingIntent buildClickContent(final Context context, final String extraMap, final int notificationId) {
//        Intent clickIntent = new Intent(context, MainFragmentActivity.class);//对Mainfragmentactivity配置singletask，来做点击通知栏跳转的页面
//        //添加其他数据
//        clickIntent.putExtra(CodeCons.PushMessage, extraMap);
//        clickIntent.putExtra("NotificationId", notificationId);
//        EventBus.getDefault().post(new JumpH5Event("game", "aaaa"));
        Toast.makeText(context, "123456", Toast.LENGTH_SHORT).show();
        return PendingIntent.getActivity(context, notificationId, null, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
