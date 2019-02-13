package com.handscape.sdk.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

import com.handscape.sdk.util.HSUUID;

/**
 * 基本的处理器
 * 选择接收多指指令
 */
class HSBluetoothGatt extends BluetoothGattCallback {

    private static String TAG = HSBluetoothGatt.class.getName();

    public HSBluetoothGatt() {
    }


    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (BluetoothGatt.GATT_SUCCESS == status) {
            switch (newState) {
                case BluetoothGatt.STATE_CONNECTED:
                    onDeviceConnected(gatt, newState);
                    if (gatt != null && (gatt.getServices() == null || gatt.getServices().size() == 0)) {
                        gatt.discoverServices();
                    } else if (gatt != null && gatt.getServices().size() > 0) {
                        onServicesDiscovered(gatt, status);
                    }
                    break;
                case BluetoothGatt.STATE_CONNECTING:
                    break;
                case BluetoothGatt.STATE_DISCONNECTED:
                    onDeviceDisConnected(gatt, newState);
                    if (gatt != null) {
                        gatt.close();
                    }
                    break;
                case BluetoothGatt.STATE_DISCONNECTING:
                    onDeviceDisConnected(gatt, newState);
                    break;
            }
        }
    }

    @Override
    public void onServicesDiscovered(final BluetoothGatt gatt, int status) {;
        if (gatt != null) {
            BluetoothGattService service = gatt.getService(HSUUID.s_TOUCH_SERVICE);
            if (service != null) {
                setCharacteristicNotification(gatt, service, HSUUID.s_TOUCH_DATA_MULTIPLE, true);
            }
        }else{
            onDeviceVerifyFailed();
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        if (descriptor != null && gatt != null && HSUUID.s_CHARACTERISTIC_CONFIG.equals(descriptor.getUuid())) {
            if (HSUUID.s_TOUCH_DATA_MULTIPLE.equals(descriptor.getCharacteristic().getUuid())) {
                onDeviceVerifySuccess(gatt, status);
            }
        }
    }

    /**
     * 接收到数据
     *
     * @param gatt
     * @param characteristic
     */
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

    }

    public final boolean setCharacteristicNotification(final BluetoothGatt gatt, final BluetoothGattService service, UUID uuid, boolean enable) {
        if (gatt == null || service == null || service.getCharacteristic(uuid) == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuid);
        boolean success = gatt.setCharacteristicNotification(characteristic, enable);
        if (success) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(HSUUID.s_CHARACTERISTIC_CONFIG);
            if (descriptor != null) {
                byte[] val = enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE :
                        BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                descriptor.setValue(val);
                boolean flag = gatt.writeDescriptor(descriptor);
                return flag;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
    }

    @Override
    public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
    }

    @Override
    public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
    }

    /**
     * 设备验证成功
     * 在所有服务初始化完成后才会执行
     *
     * @param gatt
     * @param status
     */
    protected void onDeviceVerifySuccess(BluetoothGatt gatt, int status) {
    }

    protected void onDeviceVerifyFailed() {
    }

    /**
     * 设备连接成功
     *
     * @param gatt
     * @param status
     */
    protected void onDeviceConnected(BluetoothGatt gatt, int status) {
    }

    /**
     * 设备断开连接
     *
     * @param gatt
     * @param status
     */
    protected void onDeviceDisConnected(BluetoothGatt gatt, int status) {
    }



}
