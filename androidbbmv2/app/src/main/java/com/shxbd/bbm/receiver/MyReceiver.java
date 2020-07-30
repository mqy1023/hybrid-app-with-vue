//package com.shxbd.bbm.receiver;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.v4.app.NotificationCompat;
//import android.text.TextUtils;
//import android.widget.RemoteViews;
//
//import cn.jpush.android.api.JPushInterface;
//
//import static android.content.Context.NOTIFICATION_SERVICE;
//
///**
// * 自定义接收器
// * <p>
// * 如果不定义这个 Receiver，则：
// * 1) 默认用户会打开主界面
// * 2) 接收不到自定义消息
// */
//public class MyReceiver extends BroadcastReceiver {
//
//    private static final String TAG = "JIGUANG-Example";
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        try {
//            Bundle bundle = intent.getExtras();
//
//            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
//                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
//
//            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//                //自定义推送消息
//                String messageId = bundle.getString(JPushInterface.EXTRA_MSG_ID);
//                String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//                String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//                buildNotification(context, extras);
//
//            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
//                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//
//            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
//
//
//            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
//                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
//
//            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
//                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
//            } else {
//            }
//        } catch (Exception e) {
//
//        }
//    }
//
//
//    /**
//     * 接受到对应消息后，消息的弹出处理
//     */
//    public void buildNotification(final Context context, final String content) {
//        final PushMessage pushMessage = new Gson().fromJson(content, PushMessage.class);
//        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            // 用户可以看到的通知渠道的名字.
//            CharSequence name = "notification channel";
//            // 用户可以看到的通知渠道的描述
//            String description = "notification description";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel mChannel = new NotificationChannel("1", name, importance);
//            // 配置通知渠道的属性
//            mChannel.setDescription(description);
//            // 设置通知出现时的闪灯（如果 android 设备支持的话）
//            mChannel.enableLights(true);
//            mChannel.setLightColor(Color.RED);
//            // 设置通知出现时的震动（如果 android 设备支持的话）
//            mChannel.enableVibration(true);
//            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//            //最后在notificationmanager中创建该通知渠道
//            notificationManager.createNotificationChannel(mChannel);
//        }
//        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.item_notification);
//        remoteViews.setTextViewText(R.id.noti_title, pushMessage.getTitle());
//        remoteViews.setTextViewText(R.id.noti_context, pushMessage.getContent());
//        //需要修改字符串
//        int hashCode = pushMessage.hashCode();
//        PendingIntent clickPendingIntent = buildClickContent(context, content, hashCode);
//        final Notification notification = new NotificationCompat.Builder(context)
//                .setContent(remoteViews)
//                .setSmallIcon(R.mipmap.hipai_logo)
//                .setDefaults(Notification.DEFAULT_VIBRATE)
//                .setPriority(Notification.PRIORITY_DEFAULT)
//                .setChannelId("1")
//                .build();
//        if (Build.VERSION.SDK_INT >= 16) {
//            notification.bigContentView = remoteViews;
//        }
//        final NotificationTarget notificationTarget = new NotificationTarget(context, R.id.noti_icon, remoteViews, notification, hashCode);
//        if (!TextUtils.isEmpty(pushMessage.getImage_url())) {
//            RequestOptions requestOptions = new RequestOptions().skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.mipmap.hipai_logo).placeholder(R.mipmap.hipai_logo).dontAnimate();
//
//            Glide.with(context).asBitmap().load(pushMessage.getImage_url()).apply(requestOptions).into(notificationTarget);
//        }
//        notification.contentIntent = clickPendingIntent;
//        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT;
//        notification.defaults = Notification.DEFAULT_ALL;
//        notificationManager.notify(hashCode, notification);
//    }
//
//    public PendingIntent buildClickContent(final Context context, final String extraMap, final int notificationId) {
//        Intent clickIntent = new Intent(context, MainFragmentActivity.class);//对Mainfragmentactivity配置singletask，来做点击通知栏跳转的页面
//        //添加其他数据
//        clickIntent.putExtra(CodeCons.PushMessage, extraMap);
//        clickIntent.putExtra("NotificationId", notificationId);
//        return PendingIntent.getActivity(context, notificationId, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//    }
//}
