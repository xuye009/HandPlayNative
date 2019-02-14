package com.handscape.nativereflect.command;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import com.handscape.sdk.inf.IHSConnectRecevive;

/**
 * 收到设备连接的回调
 */
public class DeviceConnectReceiveImpl implements IHSConnectRecevive {

    private static DeviceConnectReceiveImpl instance=new DeviceConnectReceiveImpl();

    public static DeviceConnectReceiveImpl getInstance() {
        return instance;
    }

    public DeviceConnectReceiveImpl(){

    }

    @Override
    public void onDeviceVerifySuccess(BluetoothGatt gatt, int status) {

    }

    @Override
    public void onDeviceVerifyFailed() {

    }

    @Override
    public void onDeviceConnected(BluetoothGatt gatt, int status) {

    }

    @Override
    public void onDeviceDisConnected(BluetoothGatt gatt, int status) {

    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

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
}
