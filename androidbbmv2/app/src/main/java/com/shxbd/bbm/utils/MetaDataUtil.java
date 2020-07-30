package com.shxbd.bbm.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class MetaDataUtil {

    public static String getHeaderSource(Context mContext) {
        String channel = "1";
        try {
            channel = getMetaDataStringApplication(mContext, "UMENG_CHANNEL", "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (channel == null || "".equals(channel)) {
            channel = "1";
        }
        return channel;
    }

    // 获取AndroidManifest中的值
    public static String getMetaDataStringApplication(Context mContext, String key, String defValue) {
        Bundle bundle = getAppMetaDataBundle(mContext.getPackageManager(), mContext.getPackageName());
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getString(key);
        }
        return defValue;
    }

    private static Bundle getAppMetaDataBundle(PackageManager packageManager,
                                               String packageName) {
        Bundle bundle = null;
        try {
            ApplicationInfo ai = packageManager.getApplicationInfo(packageName,
                    PackageManager.GET_META_DATA);
            bundle = ai.metaData;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return bundle;
    }

    public static String getAppVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }


}
