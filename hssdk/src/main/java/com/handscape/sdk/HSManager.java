package com.handscape.sdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;

import java.util.UUID;

import com.handscape.sdk.ble.HSBluetoothGattCmd;
import com.handscape.sdk.inf.IBleStatusChangeCallback;
import com.handscape.sdk.inf.IHSConnectCallback;
import com.handscape.sdk.inf.IHSTouchCmdReceive;
import com.handscape.sdk.inf.IHShandleTouchEvent;
import com.handscape.sdk.util.HSUtils;
import com.handscape.sdk.inf.IHSKeyBeanManager;
import com.handscape.sdk.inf.IHSBleScanCallBack;
import com.handscape.sdk.inf.IHSConnectRecevive;

/**
 * 蓝牙触摸SDK入口
 */
public class HSManager {

    private static Context mContext;

    private int screenWidth, screenHeight;

    public static Context getContext() {
        return mContext;
    }

    private HSBleManager hsBleManager;

    private HSTouchManager hsTouchManager;

    private HSStatusManager statusManager;

    private static HSManager instance = null;

    public static HSManager getinstance(Context context, IHSConnectRecevive recevive) {
        mContext = context;
        if (instance == null) {
            DisplayMetrics displayMetrics = HSUtils.getScreenSize(context);
            int width = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
            int height = Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels);
            instance = new HSManager(width, height, recevive);
        }
        return instance;
    }

    private HSManager(int width, int height, IHSConnectRecevive recevive) {
        this.screenWidth = width;
        this.screenHeight = height;
        statusManager = new HSStatusManager(mContext);
        hsTouchManager = new HSTouchManager();
        hsBleManager = new HSBleManager(mContext, this, screenWidth, screenHeight, hsTouchManager.getHsTouchDispatch(), recevive);
    }

    public void setIBleStatusChangeCallback(IBleStatusChangeCallback changeCallback) {
        if (statusManager != null) {
            statusManager.setBleStatusChangeCallback(changeCallback);
        }
    }

    public void setBleStatus(int status) {
        if (statusManager != null) {
            statusManager.setstatus(status);
        }
    }

    public void removeAllCmd() {
        if (hsTouchManager != null && hsTouchManager.getHsTouchDispatch() != null) {
            hsTouchManager.getHsTouchDispatch().removeAllCmd();
        }
    }

    /**
     * 判断是否支持BLE
     *
     * @return
     */
    public boolean isSupportBle() {
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        return true;
    }


    /**
     * 开始扫描
     *
     * @param time：时间限制
     * @param iBleScanCallBack
     */
    public boolean startScanning(final long time, final IHSBleScanCallBack iBleScanCallBack) {
        if (hsBleManager != null && hsBleManager.startScanning(iBleScanCallBack, time)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 扫描并且自动连接
     *
     * @param scanningTimeout   扫描时长
     * @param connectingTimeout 连接超时
     * @param supportName       符合要求的设备名称
     * @param commonCallback    连接回调
     * @return
     */
    public boolean startScanningWithAutoConnecting(final long scanningTimeout,
                                                   final long connectingTimeout,
                                                   final String[] supportName,
                                                   final IHSConnectCallback commonCallback) {
        if (hsBleManager != null &&
                hsBleManager.startScanningWithAutoConnecting(
                        scanningTimeout, connectingTimeout,
                        false, supportName,
                        commonCallback)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断系统中连接的设备是否有符合要求的
     *
     * @param scanningTimeout   扫描时长
     * @param connectingTimeout 连接超时
     * @param supportName       符合要求的设备名称
     * @param commonCallback    连接回调
     * @return
     */
    public boolean checkSystemConnect(final long scanningTimeout,
                                      final long connectingTimeout,
                                      final String[] supportName,
                                      final IHSConnectCallback commonCallback) {
        if (hsBleManager != null) {
            return hsBleManager.startScanningWithAutoConnecting(
                    scanningTimeout, connectingTimeout,
                    true, supportName,
                    commonCallback);
        } else {
            if(commonCallback!=null){
                commonCallback.actionfinish();
            }
            return false;
        }
    }

    public boolean isConnect() {
        if (statusManager != null) {
            if (statusManager.getStatus() == BluetoothAdapter.STATE_CONNECTED) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public BluetoothGatt getConnectGatt() {
        if (hsBleManager != null)
            return hsBleManager.getClientBluetoothGatt();
        return null;

    }


    /**
     * 停止扫描
     */
    public void stopScanning(final IHSBleScanCallBack iBleScanCallBack) {
        if (hsBleManager != null) {
            hsBleManager.stopScanning();
            if (iBleScanCallBack != null) {
                iBleScanCallBack.scanfinish();
            }
        }
    }


    /**
     * 连接后自动将指令转化到OnReceive中
     *
     * @param device：要连接的设备
     * @param time：时间
     * @param connectCallback：连接状态接口
     */
    public void connect(final BluetoothDevice device, long time, final IHSConnectCallback connectCallback) {
        if (hsBleManager != null) {
            hsBleManager.connect(device, time, connectCallback);
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (hsBleManager != null) {
            hsBleManager.disconnect();
        }
    }


    public void setKeyBeanManager(IHSKeyBeanManager commondManager) {
        if (hsTouchManager != null) {
            hsTouchManager.setKeyBeanManager(commondManager);
        }
    }


    public void setTouchCmdReceive(IHSTouchCmdReceive touchCmdReceive) {
        if (hsTouchManager != null) {
            hsTouchManager.setTouchCmdReceive(touchCmdReceive);
        }
    }

    /**
     * 获取已经连接的设备
     *
     * @return
     */
    public BluetoothDevice getConnectedDevice() {
        if (hsBleManager != null) {
            return hsBleManager.getConnectedDevice();
        }
        return null;
    }


    /**
     * 获取本机蓝牙适配器
     *
     * @return
     */
    public BluetoothAdapter getBleAdapter() {
        if (hsBleManager != null) {
            return hsBleManager.getBleAdapter();
        }
        return null;
    }


    /**
     * 向指定的特征值中写指定的数据
     *
     * @param serviceUUid：特征值所在的serviceUUid
     * @param charUUid：特征值的uuid
     * @param value：写入的值
     * @return
     */
    public boolean writeCharacteristic(final UUID serviceUUid, final UUID charUUid, byte[] value) {
        if (hsBleManager != null) {
            return hsBleManager.writeCharacteristic(serviceUUid, charUUid, value);
        }
        return false;
    }

    /**
     * 打开/关闭通知
     *
     * @param enable
     * @return
     */
    public boolean setCharacteristicNotification(final UUID serviceUUid, UUID characteristiUUid, boolean enable) {
        if (hsBleManager != null) {
            return hsBleManager.setCharacteristicNotification(serviceUUid, characteristiUUid, enable);
        }
        return false;

    }

    /**
     * 向指定的特征值描述符中中写指定的数据
     *
     * @param serviceUUid
     * @param charUUid
     * @param descriptoUUid
     * @param value
     * @return
     */
    public boolean writeDescriptor(final UUID serviceUUid, final UUID charUUid, final UUID descriptoUUid, byte[] value) {
        if (hsBleManager != null) {
            return hsBleManager.writeDescriptor(serviceUUid, charUUid, descriptoUUid, value);
        }
        return false;
    }

    /**
     * 读取指定的特征值
     *
     * @param serviceUUid
     * @param characteristiUUid
     * @return
     */
    public boolean readCharacteristic(UUID serviceUUid, UUID characteristiUUid) {
        if (hsBleManager != null) {
            return hsBleManager.readCharacteristic(serviceUUid, characteristiUUid);
        }
        return false;
    }

    /**
     * 设置自定义的触摸事件转化器
     *
     * @param defineHsTouchEventProcess
     */
    public void setDefineHsTouchEventProcess(IHShandleTouchEvent defineHsTouchEventProcess) {
        if (hsBleManager != null && hsBleManager.getHsBluetoothGattCmd() != null) {
            hsBleManager.getHsBluetoothGattCmd().setDefineHsTouchEventProcess(defineHsTouchEventProcess);
        }
    }

    /**
     * 模拟按键的入口
     * 真正添加按键的入口
     * 必须在创建该对象的线程中进行发送
     *
     * @param touchAction
     * @param pointerID
     * @param eventX
     * @param eventY
     * @throws Exception
     */
    public void addCommand(int touchAction, int pointerID, float eventX, float eventY) throws Exception {
        if (hsBleManager != null && hsBleManager.getHsBluetoothGattCmd() != null) {
            hsBleManager.getHsBluetoothGattCmd().addCommand(0, touchAction, pointerID, eventX, eventY, screenWidth, screenHeight);
        }
    }

    public HSBluetoothGattCmd getHsBluetoothGattCmd() {
        if (hsBleManager != null)
            return hsBleManager.getHsBluetoothGattCmd();
        return null;
    }

    /**
     * 释放所有资源，在蓝牙扫描不到设备的时候调用
     */
    public void realease() {
        if (hsBleManager != null) {
            hsBleManager.realease();
        }
    }


}
