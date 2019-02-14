package com.handscape.nativereflect.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import com.handscape.nativereflect.command.DeviceConnectReceiveImpl;
import com.handscape.sdk.HSManager;
import com.handscape.sdk.inf.IHSBleScanCallBack;

/***
 * 常驻后台服务
 * 管理蓝牙设备的连接，指令的写入等等
 */
public class HandPlayService extends Service {


    private HSManager mSdkManager;

    private HandlerThread handlerThread;
    private Handler mThreadHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        //实例化蓝牙SDK管理器
        mSdkManager = HSManager.getinstance(getApplicationContext(), DeviceConnectReceiveImpl.getInstance());
        handlerThread = new HandlerThread("");
        handlerThread.start();
        mThreadHandler = new Handler(handlerThread.getLooper());

    }


    public HSManager getmSdkManager() {
        return mSdkManager;
    }

    public void runWithOutUIThread(Runnable runnable) {
        if (mThreadHandler != null) {
            mThreadHandler.post(runnable);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new HandPlayBind();
    }


    public class HandPlayBind extends Binder {

        public HandPlayBind() {
        }

        public HandPlayService getService() {

            return HandPlayService.this;
        }


    }

}
