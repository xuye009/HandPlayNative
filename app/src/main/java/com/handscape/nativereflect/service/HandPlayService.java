package com.handscape.nativereflect.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import com.handscape.nativereflect.command.DeviceConnectReceiveImpl;
import com.handscape.nativereflect.plug.PlugManager;
import com.handscape.sdk.HSManager;


/***
 * 常驻后台服务
 * 管理蓝牙设备的连接，指令的写入等等
 */
public class HandPlayService extends Service {


    public static void startService(Context context, Bundle data) {
        Intent intent = new Intent(context, HandPlayService.class);
        intent.putExtras(data);
        context.startService(intent);
    }

    private HSManager mSdkManager;

    private HandlerThread handlerThread;
    private Handler mThreadHandler;

    //插件管理类
    private PlugManager plugManager;

    @Override
    public void onCreate() {
        super.onCreate();
        //实例化蓝牙SDK管理器
        mSdkManager = HSManager.getinstance(getApplicationContext(), DeviceConnectReceiveImpl.getInstance());
        handlerThread = new HandlerThread("");
        handlerThread.start();
        mThreadHandler = new Handler(handlerThread.getLooper());
        plugManager = new PlugManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //解析参数
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();

            if(bundle.getBoolean(ServiceStartHelp.SHOW_PLUG,false)){
                //显示悬浮球
                String pkgName=bundle.getString(ServiceStartHelp.PKGNAME);
                plugManager.show(pkgName);
            }
        }
        return START_STICKY;
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
