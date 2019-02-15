package com.handscape.nativereflect.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.LocationManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.TypedValue;

import com.handscape.sdk.util.HSConsts;

import java.util.ArrayList;
import java.util.List;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class Utils {



    public static int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }
    /**
     * 判断位置是否可用
     *
     * @param context
     * @return location is enable if return true, otherwise disable.
     */
    public static final boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (networkProvider || gpsProvider) return true;
        return false;
    }

    public static void setLocationService(Activity activity, int requestCode) {
        Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivityForResult(locationIntent, requestCode);
    }

    public static boolean isSupportDevice(BluetoothDevice device) {

        if (device == null || TextUtils.isEmpty(device.getName())) {
            return false;
        }

        for (int i = 0; i < HSConsts.supportDeviceNames.length; i++) {
            if (device.getName().startsWith(HSConsts.supportDeviceNames[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断指定的app是否运行在前台
     * @param context
     * @param pkgName
     * @return
     */
    public static boolean isBackground(Context context,String pkgName) {
        if (context != null) {
            ArrayList<Boolean> arrayList = new ArrayList<>();
            boolean result = true;
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processInfoList = am.getRunningAppProcesses();
            if (processInfoList != null) {
                for (ActivityManager.RunningAppProcessInfo processInfo : processInfoList) {
                    if (processInfo.processName.startsWith(pkgName)) {
                        boolean isBackGround =
                                processInfo.importance != IMPORTANCE_FOREGROUND
                                        && processInfo.importance != IMPORTANCE_VISIBLE;
                        arrayList.add(new Boolean(isBackGround));
                    }
                }
                for (int i = 0; i < arrayList.size(); i++) {
                    if (!arrayList.get(i)) {
                        result = false;
                        break;
                    }
                }
            }
            return result;
        }
        return false;
    }


}
