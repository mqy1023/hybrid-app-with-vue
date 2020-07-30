package com.shxbd.bbm;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.WebViewClient;
import com.shxbd.bbm.event.BackUploadImgUrlEvent;
import com.shxbd.bbm.event.JPushEvent;
import com.shxbd.bbm.event.JumpBrowserEvent;
import com.shxbd.bbm.event.JumpH5Event;
import com.shxbd.bbm.event.PickerImgUploadEvent;
import com.shxbd.bbm.utils.AndroidBugWorkaround;
import com.shxbd.bbm.utils.AndroidInterface;
import com.shxbd.bbm.utils.CommonUtil;
import com.shxbd.bbm.utils.Constants;
import com.shxbd.bbm.utils.DataCleanManager;
import com.shxbd.bbm.utils.MetaDataUtil;
import com.shxbd.bbm.utils.SharedPreferencesHelper;
import com.shxbd.bbm.utils.UploadAsyncTask;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.analytics.MobclickAgent;
import com.zzti.fengyongge.imagepicker.PhotoSelectorActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;


public class MainActivity extends AppCompatActivity {

    private FrameLayout mFrameLayout;
    private LinearLayout mAgentLinearLayout;
    private LinearLayout mSplashLinearLayout;
    private LinearLayout mLinearLayout;

    private AgentWeb mAgentWeb;

    private ViewPager mViewPager;
    private List<View> listGuide = new ArrayList<>();
    private ImageView[] imgGuide = null;
    private TextView tvNow;//立即体验

    private String curUrl = ""; // 当前打开的路由

    private RxPermissions rxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        setContentView(R.layout.activity_splash);

//        AndroidBug5497Workaround.assistActivity(this); // 解决h5软键盘不能顶起布局
        AndroidBugWorkaround.assistActivity(this);//解决webview页面键盘不能顶起布局

//        StatusBarUtil.setTranslucent(this, 160); // 状态栏透明
        StatusBarUtil.setDarkMode(this);
        StatusBarUtil.setTranslucent(this);

        EventBus.getDefault().register(this);

        // 清空极光消息
        NotificationManager nm =(NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();

        //防止home键 切进来 重启应用
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
        }

        String userId = (String) SharedPreferencesHelper.getSharedPreference(Constants.USER_ID, "");
        if (!"".equals(userId) && userId != null) { // 有用户id设置极光别名
//            JPushInterface.setAlias(this, 1, userId);
        }

        rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.CAMERA, Manifest.permission.ACCESS_NOTIFICATION_POLICY, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        // I can control the camera now
                    } else {
                        // Oups permission denied
                    }
                    openNotification();
                });
        initRooView();
        initGuideData();
    }

    private void openNotification() {

        NotificationManagerCompat notification = NotificationManagerCompat.from(this);
        boolean isEnabled = notification.areNotificationsEnabled();
        if (!isEnabled) {
            //未打开通知
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("请在“通知”中打开通知权限")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                intent.putExtra("android.provider.extra.APP_PACKAGE", MainActivity.this.getPackageName());
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  //5.0
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                intent.putExtra("app_package", MainActivity.this.getPackageName());
                                intent.putExtra("app_uid", MainActivity.this.getApplicationInfo().uid);
                                startActivity(intent);
                            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {  //4.4
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                            } else if (Build.VERSION.SDK_INT >= 15) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                intent.setData(Uri.fromParts("package", MainActivity.this.getPackageName(), null));
                            }
//                            startActivity(intent);
                            startActivityForResult(intent, 200);
                        }
                    })
                    .create();
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }
    }

    private void initGuideData() {
        listGuide.add(getLayoutInflater().inflate(R.layout.item_wel_1, null));
        listGuide.add(getLayoutInflater().inflate(R.layout.item_wel_2, null));
        View v2 = getLayoutInflater().inflate(R.layout.item_wel_3, null);
        listGuide.add(v2);
        tvNow = v2.findViewById(R.id.tv_open_id);
        tvNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesHelper.put(Constants.IS_SHOW_GUIDE,false); // 不再出现引导页
                mViewPager.setVisibility(View.GONE);
            }
        });
        mViewPager.setAdapter(new ViewPagerAdapter(listGuide));
        mViewPager.setOnPageChangeListener(new ViewPagerPageChangeListener());
    }

    private void initRooView() {
        mAgentLinearLayout = (LinearLayout) findViewById(R.id.agentweb_webview_id);
        mSplashLinearLayout = (LinearLayout) findViewById(R.id.splash_bg_id);
        mViewPager = (ViewPager) findViewById(R.id.view_page_id);
        initWebView();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 第一次打开引导页
                if ((Boolean) SharedPreferencesHelper.getSharedPreference(Constants.IS_SHOW_GUIDE, true)) {
                    mViewPager.setVisibility(View.VISIBLE);
                } else {// 不是第一次打开
                    mViewPager.setVisibility(View.GONE);
                }
                overridePendingTransition(0, 0);
//                mSplashLinearLayout.setVisibility(View.GONE);
            }
        }, 600);
    }

    private void initWebView() {
        String appVersion = MetaDataUtil.getAppVersion(MainActivity.this);

        String source = MetaDataUtil.getHeaderSource(MainActivity.this);
        source = source.replace("BBM","").replace("bbm","");

        String jiguangId = JPushInterface.getRegistrationID(this); // deviceid用的极光id
        String model = android.os.Build.MODEL; // 手机型号

        String timestamp = String.valueOf(System.currentTimeMillis());

//        String openUrl = "http://192.168.81.172:8089/#/";
//        String openUrl = "http://daifu.jiajiahebao.com/web#/";
//        String openUrl = "http://www.hahaha.vip/web#/";
//        String openUrl = "http://t-v2.hahaha.vip//web#/";
        String openUrl = Constants.APP_HOST + "web#/";
//        if ((Boolean) SharedPreferencesHelper.getSharedPreference(Constants.IS_HAVE_USE, false)) {
//            openUrl += "home";
//        } else {
//            openUrl +=  "game";
//        }
        String openType = (String) SharedPreferencesHelper.getSharedPreference(Constants.OPEN_TYPE, "0");
        // 0:打开登录页，1: 打开一级代理的home，2：打开二级代理的dispatch
        if ("1".equals(openType)) {
            openUrl += "home";
        } else if ("2".equals(openType)) {
            openUrl += "dispatch";
        } else {
            openUrl += "login";
        }

        openUrl += "?version=" + appVersion + "&src=" + source + "&jgid=" + jiguangId + "&model=" + CommonUtil.fliterChinese(model) + "&t=" + timestamp;

        AgentWebConfig.clearDiskCache(this); // 清空缓存

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mAgentLinearLayout, new LinearLayout.LayoutParams(-1, -1))
                //.useDefaultIndicator()
                .closeIndicator()
                .setWebViewClient(mWebViewClient)
                .createAgentWeb()
                .ready()
//                .go("http://daifu.jiajiahebao.com/web#/game?version=" + appVersion + "&source=" + source + "&jgid=" + jiguangId + "&model=" + model);
                .go(openUrl);

        // h5调用android端
        mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface(mAgentWeb, this));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(JumpH5Event event) {
        Log.e("jumpUrl", event.getJumpUrl());
        Log.e("jumpUrl", event.getParams());
        // android端调用h5
        mAgentWeb.getJsAccessEntrace().quickCallJs("callByNativeJump", event.getJumpUrl(), event.getParams());
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PickerImgUploadEvent event) {
        Log.e("event", event.getToken());

        mAgentWeb.getJsAccessEntrace().quickCallJs("callByAndroidShowLoading");

        rxPermissions
                .request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        Intent intent = new Intent(this, PhotoSelectorActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("limit", 1 );//number是选择图片的数量
                        startActivityForResult(intent, 0);
                    } else {
                        Toast.makeText(MainActivity.this, "请先打开权限", Toast.LENGTH_LONG).show();
                    }
                });
//        mAgentWeb.getJsAccessEntrace().quickCallJs("callByAndroidImgUrl", "http://hipai-test-test.oss-cn-shanghai.aliyuncs.com/post/15668982710808170005d64f85fc553828459.jpg");
    }
    @Subscribe(threadMode = ThreadMode.MAIN) // , sticky = true
    public void onMessageEvent(BackUploadImgUrlEvent event) {
        Log.e("event", event.getImgUrl());
        // android端调用h5, 返回h5端图片的链接
        mAgentWeb.getJsAccessEntrace().quickCallJs("callByAndroidHideLoading");
        String imgUrl = event.getImgUrl();
        if (!"".equals(imgUrl) && imgUrl != null) {
            mAgentWeb.getJsAccessEntrace().quickCallJs("callByAndroidImgUrl", event.getImgUrl());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) // , sticky = true
    public void onMessageEvent(JumpBrowserEvent event) {
        Intent intent=new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri openUrl = Uri.parse(event.getJumpUrl());
        intent.setData(openUrl);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN) // , sticky = true
    public void onMessageEvent(JPushEvent event) { // 设置极光id
        String jiguangId = JPushInterface.getRegistrationID(this); // deviceid用的极光id
        if (!"".equals(jiguangId) && jiguangId != null) {
            // android端调用h5
            mAgentWeb.getJsAccessEntrace().quickCallJs("callByNativeJgId", jiguangId);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (data != null) {
                    List<String> paths = (List<String>) data.getExtras().getSerializable("photos");//path是选择拍照或者图片的地址数组
                    //处理代码
                    Log.e("paths=", paths.size() + "");
                    Log.e("paths=", paths.get(0) + "");
                    new UploadAsyncTask().execute(paths.get(0)); // 上传图片接口
                }
                break;
            case 200: // 极光推送设置
                String jiguangId = JPushInterface.getRegistrationID(this); // deviceid用的极光id
                if (!"".equals(jiguangId) && jiguangId != null) {
                    // android端调用h5
                    mAgentWeb.getJsAccessEntrace().quickCallJs("callByNativeJgId", jiguangId);
                }
            default:
                break;
        }
        mAgentWeb.getJsAccessEntrace().quickCallJs("callByAndroidHideLoading");
        super.onActivityResult(requestCode, resultCode, data);
    }

    class ViewPagerPageChangeListener implements ViewPager.OnPageChangeListener {


        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int page, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int page) {
        }
    }

    class ViewPagerAdapter extends PagerAdapter {

        private List<View> list = null;

        public ViewPagerAdapter(List<View> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }


    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.e("url==", url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mSplashLinearLayout.setVisibility(View.GONE); // 隐藏启动页
//            hideLoadingDialogView();
            try {
                String[] urlArr = url.split("#");
                if (urlArr.length == 1) {
                    curUrl = "";
                } else {
                    curUrl = urlArr[1];
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPageFinished(view, url);
        }
    };

    @Override
    public void onBackPressed() {
        if (!mAgentWeb.back()){
            super.onBackPressed();
        }

    }

    private long firstTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long secondTime = System.currentTimeMillis();

        // 在首页、玩法、抢单、我的页面是点击两次退出应用
        if (keyCode == KeyEvent.KEYCODE_BACK && ("/home".equals(curUrl)) || "/game".equals(curUrl) || ("/dispatch".equals(curUrl)) || ("/user".equals(curUrl)) || ("/user2".equals(curUrl)) || (curUrl.contains("login"))) {
            if (secondTime - firstTime < 2000) {
                AgentWebConfig.clearDiskCache(this); // 清空缓存
                DataCleanManager.clearAllCache(MainActivity.this);
                MainActivity.this.finish();
            } else {
                Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume", "onResume");
        MobclickAgent.onResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause", "onPause");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "onDestroy");
        EventBus.getDefault().unregister(this);
        if (mAgentWeb != null) {
            AgentWebConfig.clearDiskCache(this); // 清空缓存
            DataCleanManager.clearAllCache(MainActivity.this);
            mAgentWeb.getWebCreator().getWebView().removeAllViews();
            mAgentWeb.destroy();
            mAgentWeb = null;
        }
    }
}
