package com.handscape.sdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;

import com.handscape.sdk.inf.IBleStatusChangeCallback;

/**
 * 蓝牙状态管理类
 */
final class HSStatusManager {

    public static final String TAG = HSStatusManager.class.getName();

    private Context context;

    private volatile int status;

    private HandlerThread mThread;

    private Handler mHandler;

    private IBleStatusChangeCallback bleStatusChangeCallback;

    public HSStatusManager(Context context) {
        this.context = context;
        mThread = new HandlerThread("status");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
        register();
    }


    public int getStatus() {
        return status;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        unregister();
    }

    public void setstatus(final int status) {
        this.status = status;
        notifyStatus();
    }

    public void setBleStatusChangeCallback(IBleStatusChangeCallback bleStatusChangeCallback) {
        this.bleStatusChangeCallback = bleStatusChangeCallback;
    }

    private BroadcastReceiver mStatusChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                status = state;
                notifyStatus();
            }
        }
    };


    private void notifyStatus() {
        if (bleStatusChangeCallback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    bleStatusChangeCallback.statucchange(status);
                }
            });
        }
    }


    /**
     * 注册
     */
    public void register() {
        IntentFilter stateChangeFilter = new IntentFilter(
                BluetoothAdapter.ACTION_STATE_CHANGED);
        IntentFilter connectedFilter = new IntentFilter(
                BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter disConnectedFilter = new IntentFilter(
                BluetoothDevice.ACTION_ACL_DISCONNECTED);
        context.registerReceiver(mStatusChangeReceiver, stateChangeFilter);
        context.registerReceiver(mStatusChangeReceiver, connectedFilter);
        context.registerReceiver(mStatusChangeReceiver, disConnectedFilter);
    }

    /**
     * 解绑
     */
    public void unregister() {
        context.unregisterReceiver(mStatusChangeReceiver);
    }

}
