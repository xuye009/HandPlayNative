package com.handscape.sdk.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 一些公共的方法
 */
public class HSUtils {
    /**
     * 刷新系统蓝牙扫描缓存
     *
     * @param context
     */
    public static void refreshBleAppFromSystem(Context context) {
        //6.0以上才有该功能,不是6.0以上就算了
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        String pkgname = context.getPackageName();
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return;
        }
        if (!adapter.isEnabled()) {
            return;
        }
        try {
            Object mIBluetoothManager = getIBluetoothManager(adapter);
            Method isBleAppPresentM = mIBluetoothManager.getClass().getDeclaredMethod("isBleAppPresent");
            isBleAppPresentM.setAccessible(true);
            boolean isBleAppPresent = (Boolean) isBleAppPresentM.invoke(mIBluetoothManager);
            if (isBleAppPresent) {
                return;
            }
            Field mIBinder = BluetoothAdapter.class.getDeclaredField("mToken");
            mIBinder.setAccessible(true);
            Object mToken = mIBinder.get(adapter);

            //刷新偶尔系统无故把app视为非 BLE应用 的错误标识 导致无法扫描设备
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //8.0+ (部分手机是7.1.2 也是如此)
                Method updateBleAppCount = mIBluetoothManager.getClass().getDeclaredMethod("updateBleAppCount", IBinder.class, boolean.class, String.class);
                updateBleAppCount.setAccessible(true);
                //关一下 再开
                updateBleAppCount.invoke(mIBluetoothManager, mToken, false, pkgname);
                updateBleAppCount.invoke(mIBluetoothManager, mToken, true, pkgname);

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                try {
                    //6.0~7.1.1

                    Method updateBleAppCount = mIBluetoothManager.getClass().getDeclaredMethod("updateBleAppCount", IBinder.class, boolean.class);
                    updateBleAppCount.setAccessible(true);
                    //关一下 再开
                    updateBleAppCount.invoke(mIBluetoothManager, mToken, false);
                    updateBleAppCount.invoke(mIBluetoothManager, mToken, true);
                } catch (NoSuchMethodException e) {
                    //8.0+ (部分手机是7.1.2 也是如此)
                    try {
                        Method updateBleAppCount = mIBluetoothManager.getClass().getDeclaredMethod("updateBleAppCount", IBinder.class, boolean.class, String.class);
                        updateBleAppCount.setAccessible(true);
                        //关一下 再开
                        updateBleAppCount.invoke(mIBluetoothManager, mToken, false, pkgname);
                        updateBleAppCount.invoke(mIBluetoothManager, mToken, true, pkgname);
                    } catch (NoSuchMethodException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static final int CONNECTION_STATE_DISCONNECTED = 0;
    public static final int CONNECTION_STATE_CONNECTED = 1;
    public static final int CONNECTION_STATE_UN_SUPPORT = -1;

    /**
     * 获取蓝牙连接状态
     *
     * @param mac
     * @return
     */
    public static int getInternalConnectionState(String mac) {
        //该功能是在21 (5.1.0)以上才支持, 5.0 以及以下 都 不支持
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return CONNECTION_STATE_UN_SUPPORT;
        }
        if (Build.MANUFACTURER.equalsIgnoreCase("OPPO")) {//OPPO勿使用这种办法判断, OPPO无解
            return CONNECTION_STATE_UN_SUPPORT;
        }
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice remoteDevice = adapter.getRemoteDevice(mac);
        Object mIBluetooth = null;
        try {
            Field sService = BluetoothDevice.class.getDeclaredField("sService");
            sService.setAccessible(true);
            mIBluetooth = sService.get(null);
        } catch (Exception e) {
            return CONNECTION_STATE_UN_SUPPORT;
        }
        if (mIBluetooth == null) return CONNECTION_STATE_UN_SUPPORT;

        boolean isConnected;
        try {
            Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected");
            isConnectedMethod.setAccessible(true);
            isConnected = (Boolean) isConnectedMethod.invoke(remoteDevice);
            isConnectedMethod.setAccessible(false);
        } catch (Exception e) {
            //如果找不到,说明不兼容isConnected, 尝试去使用getConnectionState 判断
            try {
                Method getConnectionState = mIBluetooth.getClass().getDeclaredMethod("getConnectionState", BluetoothDevice.class);
                getConnectionState.setAccessible(true);
                int state = (Integer) getConnectionState.invoke(mIBluetooth, remoteDevice);
                getConnectionState.setAccessible(false);
                isConnected = state == CONNECTION_STATE_CONNECTED;
            } catch (Exception e1) {
                return CONNECTION_STATE_UN_SUPPORT;
            }
        }
        return isConnected ? CONNECTION_STATE_CONNECTED : CONNECTION_STATE_DISCONNECTED;
    }

    /**
     * 关闭/开启 BLE服务
     *
     * @param isEnable
     */
    public static void setLeServiceEnable(boolean isEnable) {

        Object mIBluetooth;
        try {
            Field sService = BluetoothDevice.class.getDeclaredField("sService");
            sService.setAccessible(true);
            mIBluetooth = sService.get(null);
        } catch (Exception e) {
            return;
        }
        if (mIBluetooth == null) return;

        try {
            if (isEnable) {
                Method onLeServiceUp = mIBluetooth.getClass().getDeclaredMethod("onLeServiceUp");
                onLeServiceUp.setAccessible(true);
                onLeServiceUp.invoke(mIBluetooth);
            } else {
                Method onLeServiceUp = mIBluetooth.getClass().getDeclaredMethod("onBrEdrDown");
                onLeServiceUp.setAccessible(true);
                onLeServiceUp.invoke(mIBluetooth);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放所有的蓝牙资源
     *
     * @return
     */
    public static boolean releaseAllScanClient() {
        try {
            Object mIBluetoothManager = getIBluetoothManager(BluetoothAdapter.getDefaultAdapter());
            if (mIBluetoothManager == null) return false;
            Object iGatt = getIBluetoothGatt(mIBluetoothManager);
            if (iGatt == null) return false;

            Method unregisterClient = getDeclaredMethod(iGatt, "unregisterClient", int.class);
            Method stopScan;
            int type;
            try {
                type = 0;
                stopScan = getDeclaredMethod(iGatt, "stopScan", int.class, boolean.class);
            } catch (Exception e) {
                type = 1;
                stopScan = getDeclaredMethod(iGatt, "stopScan", int.class);
            }

            for (int mClientIf = 0; mClientIf <= 40; mClientIf++) {
                if (type == 0) {
                    try {
                        stopScan.invoke(iGatt, mClientIf, false);
                    } catch (Exception ignored) {
                    }
                }
                if (type == 1) {
                    try {
                        stopScan.invoke(iGatt, mClientIf);
                    } catch (Exception ignored) {
                    }
                }
                try {
                    unregisterClient.invoke(iGatt, mClientIf);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            stopScan.setAccessible(false);
            unregisterClient.setAccessible(false);
//            BLESupport.getDeclaredMethod(iGatt, "unregAll").invoke(iGatt);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 是否开启位置服务
     *
     * @param context
     * @return
     */
    public static boolean hasLocationEnablePermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        int locationMode = Settings.Secure.LOCATION_MODE_OFF;
        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (locationMode != Settings.Secure.LOCATION_MODE_OFF) {
            return true;
        }
        return false;
    }

    /**
     * 跳转到位置服务请求界面
     *
     * @param activity
     * @param requestCode
     */
    public static void requestLocation(Activity activity, int requestCode) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转到位置服务请求界面
     *
     * @param fragment
     * @param requestCode
     */
    public static void requestLocation(Fragment fragment, int requestCode) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        fragment.startActivityForResult(intent, requestCode);
    }


    public static boolean checkBleDevice(Activity activity, BluetoothAdapter adapter, int requestCode) throws Exception {
        if (adapter != null) {
            if (!adapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivityForResult(enableBtIntent, requestCode);
                return false;
            } else {
                return true;
            }
        } else {
            throw new Exception("device not support ble");
        }
    }

    public static boolean checkBleDevice(Fragment fragment, BluetoothAdapter adapter, int requestCode) throws Exception {
        if (adapter != null) {
            if (!adapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                fragment.startActivityForResult(enableBtIntent, requestCode);
                return false;
            } else {
                return true;
            }
        } else {
            throw new Exception("device not support ble");
        }
    }


    public static Object getIBluetoothGatt(Object mIBluetoothManager) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method getBluetoothGatt = getDeclaredMethod(mIBluetoothManager, "getBluetoothGatt");
        return getBluetoothGatt.invoke(mIBluetoothManager);
    }


    public static Object getIBluetoothManager(BluetoothAdapter adapter) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method getBluetoothManager = getDeclaredMethod(BluetoothAdapter.class, "getBluetoothManager");
        return getBluetoothManager.invoke(adapter);
    }


    public static Field getDeclaredField(Class<?> clazz, String name) throws NoSuchFieldException {
        Field declaredField = clazz.getDeclaredField(name);
        declaredField.setAccessible(true);
        return declaredField;
    }


    public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method declaredMethod = clazz.getDeclaredMethod(name, parameterTypes);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }


    public static Field getDeclaredField(Object obj, String name) throws NoSuchFieldException {
        Field declaredField = obj.getClass().getDeclaredField(name);
        declaredField.setAccessible(true);
        return declaredField;
    }


    public static Method getDeclaredMethod(Object obj, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method declaredMethod = obj.getClass().getDeclaredMethod(name, parameterTypes);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }


    public static DisplayMetrics getScreenSize(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getRealMetrics(metrics);
        return metrics;
    }

    /**
     * 获取已经绑定的设备
     *
     * @return
     */
    public static Set<BluetoothDevice> getBondedDevices() {
        Set<BluetoothDevice> deviceSet = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        return deviceSet;
    }

    public static boolean isContainDevice(String devName, String... devNameSet) {
        if (TextUtils.isEmpty(devName)) {
            return false;
        }
        String sourceDev = devName.toUpperCase().replace(" ", "");
        for (String item : devNameSet) {
            String match = item.toUpperCase().replace(" ", "");
            if (sourceDev.startsWith(match)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取在系统中已经连接的蓝牙设备
     *
     * @return
     */
    public static List<BluetoothDevice> getSystemConnectingDevice() {
        List<BluetoothDevice> deviceList = new ArrayList<>();
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
        try {
            //得到连接状态的方法
            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            //打开权限
            method.setAccessible(true);
            int state = (int) method.invoke(adapter, (Object[]) null);
            if (state == BluetoothAdapter.STATE_CONNECTED) {
                Set<BluetoothDevice> devices = adapter.getBondedDevices();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    for (BluetoothDevice device : devices) {
                        Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                        method.setAccessible(true);
                        boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                        if (isConnected) {
                            deviceList.add(device);
                        }
                    }
                } else {
                    deviceList.addAll(devices);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceList;
    }


    /**
     * 生成模拟点
     *
     * @param oldPoint
     * @param code
     * @param index
     * @return
     */
    public static int makeVirtualCode(int oldPoint, int code, int index) {
        if (index == 0) {
            return oldPoint;
        } else if (code == HSTouchMapKeyUtils.KEY_HS_L1) {
            return index + 10;
        } else if (code == HSTouchMapKeyUtils.KEY_HS_L2) {
            return index + 37;
        } else if (code == HSTouchMapKeyUtils.KEY_HS_R1) {
            return index + 64;
        } else if (code == HSTouchMapKeyUtils.KEY_HS_R2) {
            return index + 91;
        }
        return oldPoint;
    }

    //压枪的模拟ID
    public static final int PressGunId = 4;


    /**
     * 根据PointID获取对应的区域编码
     *
     * @param pointId：按键的Point ID
     * @return ：-1:无效
     */
    public static int calculateCode(int pointId) {
        if (pointId >= 10 && pointId < 15) {
            return HSTouchMapKeyUtils.KEY_HS_L1;
        } else if (pointId >= 15 && pointId < 20) {
            return HSTouchMapKeyUtils.KEY_HS_L2;
        } else if (pointId >= 20 && pointId < 25) {
            return HSTouchMapKeyUtils.KEY_HS_R1;
        } else if (pointId >= 25 && pointId < 30) {
            return HSTouchMapKeyUtils.KEY_HS_R2;
        }
        return -1;
    }

    /**
     * px转dp
     *
     * @param context
     * @param dp
     * @return
     */
    public static float dp2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static float px2dp(Context context, float px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return  (px / scale + 0.5f);
    }


    public static float[] getPingMuSize(Context mContext) {
        int densityDpi = mContext.getResources().getDisplayMetrics().densityDpi;
        float scaledDensity = mContext.getResources().getDisplayMetrics().scaledDensity;
        float density = mContext.getResources().getDisplayMetrics().density;
        float xdpi = mContext.getResources().getDisplayMetrics().xdpi;
        float ydpi = mContext.getResources().getDisplayMetrics().ydpi;
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int height = mContext.getResources().getDisplayMetrics().heightPixels;
        // 这样可以计算屏幕的物理尺寸
        float width2 = (width / xdpi)*(width / xdpi);
        float height2 = (height / ydpi)*(width / xdpi);
        return new float[]{width2,height2};
    }

}
