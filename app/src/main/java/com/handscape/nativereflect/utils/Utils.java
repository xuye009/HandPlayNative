package com.handscape.nativereflect.utils;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.text.TextUtils;

import com.handscape.sdk.util.HSConsts;

public class Utils {

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


}
