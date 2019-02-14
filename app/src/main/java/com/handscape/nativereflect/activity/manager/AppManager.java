package com.handscape.nativereflect.activity.manager;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.handscape.nativereflect.bean.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理安装的APP
 */
public class AppManager {

    private Context context;

    public AppManager(Context context) {
        this.context = context;
    }


    /**
     * 获取所有的应用列表
     *
     * @return
     */
    public ArrayList<ApplicationInfo> getAllApp() {
        ArrayList<ApplicationInfo> data = new ArrayList<>();
        PackageManager manager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> queryIntentActivities = manager.queryIntentActivities(intent, PackageManager.GET_UNINSTALLED_PACKAGES);
        for (int i = 0; i < queryIntentActivities.size(); i++) {
            ApplicationInfo applicationInfo = queryIntentActivities.get(i).activityInfo.applicationInfo;
            data.add(applicationInfo);
        }
        return data;
    }

}


