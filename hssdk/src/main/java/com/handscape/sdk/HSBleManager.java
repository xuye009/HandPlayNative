package com.handscape.sdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.util.List;
import java.util.UUID;

import com.handscape.sdk.ble.HSBluetoothGattCmd;
import com.handscape.sdk.ble.HSLeScanCallBack;
import com.handscape.sdk.inf.IHSConnectCallback;
import com.handscape.sdk.inf.IHSConnectRecevive;
import com.handscape.sdk.touch.HSTouchDispatch;
import com.handscape.sdk.util.HSUtils;
import com.handscape.sdk.inf.IHSBleScanCallBack;

/**
 * 蓝牙连接管理器
 */
class HSBleManager {

    private Context mContext;
    private IHSConnectRecevive connectRecevive;

//    private int mapwidth;
//    private int mapheight;
//    private HSTouchDispatch dispatch;
//
//
//    private HSManager hsManager;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mAdapter;

    private BluetoothGatt clientBluetoothGatt;


    private Handler mScheduledExecutorHandler = null;


    private HSBluetoothGattCmd hsBluetoothGattCmd;


    public HSBluetoothGattCmd getHsBluetoothGattCmd() {
        return hsBluetoothGattCmd;
    }

    /**
     * 判断是否在扫描中
     */
    private boolean isScanning = false;

    public boolean isScanning() {
        return isScanning;
    }

    public BluetoothAdapter getBleAdapter() {
        return mAdapter;
    }


    //蓝牙回调类
    private HSLeScanCallBack hsScanCallback = null;

    public HSBleManager(Context context, HSManager hsManager, int mapwidth, int mapheight, HSTouchDispatch dispatch, IHSConnectRecevive connectRecevive) {
        mScheduledExecutorHandler = new Handler();
        this.mContext = context;
//        this.mapwidth = mapwidth;
//        this.mapheight = mapheight;
//        this.dispatch = dispatch;
//        this.hsManager = hsManager;
        this.connectRecevive = connectRecevive;
        hsBluetoothGattCmd = new HSBluetoothGattCmd(mapwidth, mapheight, dispatch, hsManager);
        hsBluetoothGattCmd.setIhsConnectRecevive(this.connectRecevive);
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mAdapter = bluetoothManager.getAdapter();
        hsScanCallback = new HSLeScanCallBack();
        initadapter();
    }

    /**
     * 开启扫描
     *
     * @param iBleScanCallBack：扫描需要的回调接口
     * @param time                       扫描限制时间
     */
    public boolean startScanning(final IHSBleScanCallBack iBleScanCallBack, final long time) {
        if (isScanning) {
            if (iBleScanCallBack != null) {
                iBleScanCallBack.scanfailed(IHSBleScanCallBack.ERROR_ISSCANNING);
            }
            return false;
        }
        if (hsScanCallback != null) {
            hsScanCallback.setiBleScanCallBack(iBleScanCallBack);
        }
        if (!initadapter()) {
            if (iBleScanCallBack != null) {
                try {
                    iBleScanCallBack.scanfailed(IHSBleScanCallBack.ERROR_INITADAPTER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
        //如果可以扫描
        if (mAdapter != null && (mAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE || mAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                if (!mAdapter.startLeScan(hsScanCallback)) {
                    if (iBleScanCallBack != null) {
                        try {
                            iBleScanCallBack.scanfailed(IHSBleScanCallBack.ERROR_STARTLESCAN);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                } else {
                    isScanning = true;
                    schedule(new Runnable() {
                        @Override
                        public void run() {
                            stopScanning();
                            if (iBleScanCallBack != null) {
                                iBleScanCallBack.scanfinish();
                            }
                        }
                    }, time);
                    return true;
                }
            } else {
                Log.v("xuye","开始");
                isScanning = true;
                mAdapter.getBluetoothLeScanner().startScan(hsScanCallback.getMyScanCallback());
                schedule(new Runnable() {
                    @Override
                    public void run() {
                        stopScanning();
                        if (iBleScanCallBack != null) {
                            iBleScanCallBack.scanfinish();
                        }
                    }
                }, time);
                return true;
            }
        } else {
            if (iBleScanCallBack != null) {
                try {
                    iBleScanCallBack.scanfailed(IHSBleScanCallBack.ERROR_ISSCANNING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

    /**
     * * 扫描并且自动连接
     *
     * @param scanningTimeout：扫描时长
     * @param connectingTimeout：连接超时
     * @param onlyStsrem             是否只判断系统是否连接
     * @param supportName：符合要求的设备名称
     * @param supportName：支持的名称
     * @param connectCallback：连接回调
     * @return true：开始扫描；false：启动扫描失败
     */
    public boolean startScanningWithAutoConnecting(final long scanningTimeout,
                                                   final long connectingTimeout,
                                                   final boolean onlyStsrem,
                                                   final String[] supportName,
                                                   final IHSConnectCallback connectCallback) {
        boolean flag = false;
        BluetoothDevice device = null;
        //首先获取系统已经连接的设备
        List<BluetoothDevice> systemConnectingDeviceList = HSUtils.getSystemConnectingDevice();
        for (int i = 0; i < systemConnectingDeviceList.size(); i++) {
            if (HSUtils.isContainDevice(systemConnectingDeviceList.get(i).getName(), supportName)) {
                device = systemConnectingDeviceList.get(i);
                flag = true;
                break;
            }
        }
        if (flag && device != null) {
            connect(device, connectingTimeout, connectCallback);
            return true;
        } else {
            if (onlyStsrem) {
                if(connectCallback!=null){
                    connectCallback.actionfinish();
                }
                return false;
            }
            ihsBleScanCallBack.setConnectingTimeOut(connectingTimeout);
            ihsBleScanCallBack.setSupportName(supportName);
            ihsBleScanCallBack.setCommonCallback(connectCallback);
            return startScanning(ihsBleScanCallBack, scanningTimeout);
        }
    }

    /**
     * 停止扫描
     */
    public void stopScanning() {
        if (mAdapter != null && mAdapter.isEnabled()) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                mAdapter.stopLeScan(hsScanCallback);
                isScanning = false;
            } else {
                mAdapter.getBluetoothLeScanner().stopScan(hsScanCallback.getMyScanCallback());
                isScanning = false;
            }
        }
    }

    /**
     * 开始连接
     *
     * @param device：需要连接的设备
     * @param connectCallback：连接回调
     */
    public void connect(final BluetoothDevice device, final long time, final IHSConnectCallback connectCallback) {
        if (device != null && initadapter()) {
            clientBluetoothGatt = device.connectGatt(mContext, false, hsBluetoothGattCmd);
            schedule(new Runnable() {
                @Override
                public void run() {
                    if (connectCallback != null) {
                        connectCallback.actionfinish();
                    }
                }
            }, time);
        } else {
            if (connectCallback != null) {
                connectCallback.actionfinish();
            }
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (clientBluetoothGatt != null) {
            clientBluetoothGatt.disconnect();
            clientBluetoothGatt.close();
            clientBluetoothGatt = null;
        }
    }

    /**
     * 重新连接
     *
     * @return
     */
    public boolean connect() {
        if (clientBluetoothGatt != null) {
            return clientBluetoothGatt.connect();
        }
        return false;
    }

    /**
     * 发现服务
     */
    public void discoverservice() {
        if (clientBluetoothGatt != null) {
            clientBluetoothGatt.discoverServices();
        }
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
        if (clientBluetoothGatt == null) {
            return false;
        }

        BluetoothGattService service = clientBluetoothGatt.getService(serviceUUid);
        if (service == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(charUUid);
        if (characteristic == null) {
            return false;
        }
        characteristic.setValue(value);
        return clientBluetoothGatt.writeCharacteristic(characteristic);
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
        if (clientBluetoothGatt == null) {
            return false;
        }

        BluetoothGattService service = clientBluetoothGatt.getService(serviceUUid);
        if (service == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(charUUid);
        if (characteristic == null) {
            return false;
        }
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(descriptoUUid);
        if (descriptor == null) {
            return false;
        }
        descriptor.setValue(value);
        return clientBluetoothGatt.writeDescriptor(descriptor);
    }

    /**
     * 读取指定的特征值
     *
     * @param serviceUUid
     * @param characteristiUUid
     * @return
     */
    public boolean readCharacteristic(UUID serviceUUid, UUID characteristiUUid) {
        if (clientBluetoothGatt == null) {
            return false;
        }

        BluetoothGattService service = clientBluetoothGatt.getService(serviceUUid);
        if (service == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristiUUid);
        if (characteristic == null) {
            return false;
        }
        clientBluetoothGatt.setCharacteristicNotification(characteristic, true);
        return clientBluetoothGatt.readCharacteristic(characteristic);
    }


    public boolean setCharacteristicNotification(final UUID serviceUUid, UUID characteristiUUid, boolean enable) {
        if (clientBluetoothGatt == null) {
            return false;
        }
        BluetoothGattService service = clientBluetoothGatt.getService(serviceUUid);
        if (service == null) {
            return false;
        }
        if (hsBluetoothGattCmd != null) {
            return hsBluetoothGattCmd.setCharacteristicNotification(clientBluetoothGatt, service, characteristiUUid, enable);
        }
        return false;
    }


    /**
     * 获取当前连接的设备
     *
     * @return
     */
    public BluetoothDevice getConnectedDevice() {
        if (clientBluetoothGatt != null) {
            return clientBluetoothGatt.getDevice();
        }
        return null;
    }

    public BluetoothGatt getClientBluetoothGatt() {
        return clientBluetoothGatt;
    }


    //初始化蓝牙设备
    private boolean initadapter() {
        if (mAdapter == null) {
            mAdapter = bluetoothManager.getAdapter();
        }
        if (mAdapter == null) {
            return false;
        }
        if (!mAdapter.isEnabled()) {
            mAdapter.enable();
        }
        if (!mAdapter.isEnabled()) {
            return false;
        }
        return true;
    }

    /**
     * 关闭扫描
     * 释放蓝牙连接
     */
    public void realease() {
        try {
            stopScanning();
            if (clientBluetoothGatt != null) {
                clientBluetoothGatt.disconnect();
                clientBluetoothGatt.close();
                clientBluetoothGatt = null;
            }
            HSUtils.refreshBleAppFromSystem(mContext);
            HSUtils.releaseAllScanClient();
            hsScanCallback = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 延迟执行任务
     *
     * @param runnable：执行的任务
     * @param time：延迟的时间/毫秒
     */
    private void schedule(Runnable runnable, long time) {
        if (mScheduledExecutorHandler != null) {
            mScheduledExecutorHandler.postDelayed(runnable, time);
        }
    }

    private AutoConnectingCallBack ihsBleScanCallBack = new AutoConnectingCallBack();

    class AutoConnectingCallBack implements IHSBleScanCallBack {

        private long timeout;

        public void setConnectingTimeOut(long timeout) {
            this.timeout = timeout;
        }

        private String[] supportName;

        public void setSupportName(String[] deviceName) {
            this.supportName = deviceName;
        }

        private IHSConnectCallback connectCallback;

        public void setCommonCallback(IHSConnectCallback commonCallback) {
            this.connectCallback = commonCallback;
        }

        private boolean flag = false;

        @Override
        public void scanfailed(int code) {
            flag = false;
            if (connectCallback != null) {
                connectCallback.actionfinish();
            }
        }

        @Override
        public void scanfinish() {
            if (connectCallback != null) {
                connectCallback.actionfinish();
            }
        }

        @Override
        public void onScanResult(BluetoothDevice device, int rssi) {
            if (HSUtils.isContainDevice(device.getName(), supportName)) {
                flag = true;
                stopScanning();
                connect(device, timeout, connectCallback);
            }
        }

        @Override
        public void onBatchScanResults(List<BluetoothDevice> deviceList) {

        }
    }

}
