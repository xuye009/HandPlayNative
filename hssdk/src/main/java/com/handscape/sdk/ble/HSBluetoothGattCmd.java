package com.handscape.sdk.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.handscape.sdk.HSManager;
import com.handscape.sdk.inf.IHSConnectRecevive;
import com.handscape.sdk.inf.IHShandleTouchEvent;
import com.handscape.sdk.touch.HSCharacteristHandle;
import com.handscape.sdk.touch.HSTouchCommand;
import com.handscape.sdk.touch.HSTouchDispatch;
import com.handscape.sdk.touch.HSTouchEventProcess;
import com.handscape.sdk.util.HSTouchMapKeyUtils;

import java.util.Arrays;

public class HSBluetoothGattCmd extends HSBluetoothGatt {

    public static final String TAG = HSBluetoothGattCmd.class.getName();

    private HSCharacteristHandle hsCharacteristHandle;

    private HSTouchEventProcessImpl hsTouchEventProcess;

    private HSManager hsManage;
    private Handler mThreadHandler;

    private HSTouchDispatch dispatch;

    private IHSConnectRecevive ihsConnectRecevive;

    private IHShandleTouchEvent defineHsTouchEventProcess;

    private BluetoothGatt mBlueGatt;

    private HandlerThread mThread;

    public void setBleGatt(BluetoothGatt bleGatt) {
        this.mBlueGatt = bleGatt;
    }

    public BluetoothGatt getmBlueGatt() {
        return mBlueGatt;
    }

    public HSBluetoothGattCmd(int mapwidth, int mapheight, HSTouchDispatch dispatchm, HSManager hsManage) {
        this.dispatch = dispatchm;
        this.hsManage = hsManage;
        mThread = new HandlerThread("addCommond");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        hsTouchEventProcess = new HSTouchEventProcessImpl();
        hsCharacteristHandle = new HSCharacteristHandle(mapwidth, mapheight, hsTouchEventProcess);
    }

    /**
     * 模拟按键的入口
     * 真正添加按键的入口
     * 必须在创建该对象的线程中进行发送
     *
     * @param angle
     * @param touchAction
     * @param pointerID
     * @param eventX
     * @param eventY
     * @param windowWidth
     * @param windowHeight
     */
    public void addCommand(double angle, int touchAction, int pointerID, float eventX, float eventY, float windowWidth, float windowHeight) {
        hsTouchEventProcess.handleTouchEvent(angle, touchAction, pointerID, eventX, eventY, windowHeight, windowHeight);
    }

    public void setIhsConnectRecevive(IHSConnectRecevive ihsConnectRecevive) {
        this.ihsConnectRecevive = ihsConnectRecevive;
    }

    /**
     * 设置自定义的触摸事件转化器
     *
     * @param defineHsTouchEventProcess
     */

    public void setDefineHsTouchEventProcess(IHShandleTouchEvent defineHsTouchEventProcess) {
        this.defineHsTouchEventProcess = defineHsTouchEventProcess;
    }

    @Override
    protected void onDeviceVerifySuccess(BluetoothGatt gatt, int status) {
        if (ihsConnectRecevive != null) {
            ihsConnectRecevive.onDeviceVerifySuccess(gatt, status);
        }
    }

    @Override
    protected void onDeviceVerifyFailed() {
        if (ihsConnectRecevive != null) {
            ihsConnectRecevive.onDeviceVerifyFailed();
        }
    }

    @Override
    protected void onDeviceConnected(BluetoothGatt gatt, int status) {
        if (ihsConnectRecevive != null) {
            ihsConnectRecevive.onDeviceConnected(gatt, status);
        }
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        try {
            hsManage.setBleStatus(newState);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onConnectionStateChange(gatt, status, newState);
    }

    @Override
    protected void onDeviceDisConnected(BluetoothGatt gatt, int status) {
        Log.v("xuye", "onDeviceDisConnected");
        if (ihsConnectRecevive != null) {
            ihsConnectRecevive.onDeviceDisConnected(gatt, status);
        }
        if (mBlueGatt != null) {
            mBlueGatt.disconnect();
            mBlueGatt.close();
            mBlueGatt = null;
        }

    }

    @Override
    public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (hsCharacteristHandle != null) {
                        hsCharacteristHandle.pause(gatt, characteristic);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (ihsConnectRecevive != null) {
                        ihsConnectRecevive.onCharacteristicChanged(gatt, characteristic);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        if (ihsConnectRecevive != null) {
            ihsConnectRecevive.onCharacteristicRead(gatt, characteristic, status);
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        try {
            super.onDescriptorWrite(gatt, descriptor, status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ihsConnectRecevive != null) {
            ihsConnectRecevive.onDescriptorWrite(gatt, descriptor, status);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        if (ihsConnectRecevive != null) {
            ihsConnectRecevive.onCharacteristicWrite(gatt, characteristic, status);
        }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);
        if (ihsConnectRecevive != null) {
            ihsConnectRecevive.onDescriptorRead(gatt, descriptor, status);
        }
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        super.onMtuChanged(gatt, mtu, status);
        if (ihsConnectRecevive != null) {
            ihsConnectRecevive.onMtuChanged(gatt, mtu, status);
        }
    }

    @Override
    public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        super.onPhyRead(gatt, txPhy, rxPhy, status);
        if (ihsConnectRecevive != null) {
            ihsConnectRecevive.onPhyRead(gatt, txPhy, rxPhy, status);
        }
    }

    @Override
    public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        if (ihsConnectRecevive != null) {
            ihsConnectRecevive.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        super.onReadRemoteRssi(gatt, rssi, status);
        if (ihsConnectRecevive != null) {
            ihsConnectRecevive.onReadRemoteRssi(gatt, rssi, status);
        }
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        super.onReliableWriteCompleted(gatt, status);
        if (ihsConnectRecevive != null) {
            ihsConnectRecevive.onReliableWriteCompleted(gatt, status);
        }
    }

    class HSTouchEventProcessImpl extends HSTouchEventProcess {

        private Handler mHandler;

        private HSCmdDeal cmdDeal;

        public HSTouchEventProcessImpl() {
            mHandler = new Handler();
            cmdDeal = new HSCmdDeal();
        }

        @Override
        public void handleTouchEvent(final double angle, final int touchAction, final int pointerID, final float eventX, final float eventY, final float windowWidth, final float windowHeight) {
            mThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (defineHsTouchEventProcess != null) {
                            HSTouchCommand[] commands = defineHsTouchEventProcess.handleTouchEvent(touchAction, pointerID, eventX, eventY, windowWidth, windowHeight);
                            if (dispatch != null && commands != null && commands.length > 0) {
                                dispatch.addCmd(commands);
                            }
                        } else {
                            HSTouchCommand cmd = HSTouchCommand.newCommand(pointerID, 0, touchAction, (int) eventX, (int) eventY);
                            if (dispatch != null && cmd != null) {
                                dispatch.addCmd(cmd);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
