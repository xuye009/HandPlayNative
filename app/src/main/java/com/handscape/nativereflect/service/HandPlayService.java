package com.handscape.nativereflect.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;

import com.handscape.nativereflect.HSHandleTouchEventImpl;
import com.handscape.nativereflect.HSKeyBeanManagerImpl;
import com.handscape.nativereflect.command.DeviceConnectReceiveImpl;
import com.handscape.nativereflect.plug.PlugManager;
import com.handscape.sdk.HSManager;
import com.handscape.sdk.inf.IHSTouchCmdReceive;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;


/***
 * 常驻后台服务
 * 管理蓝牙设备的连接，指令的写入等等
 */
public class HandPlayService extends Service implements IHSTouchCmdReceive {


    public static void startService(Context context, Bundle data) {
        Intent intent = new Intent(context, HandPlayService.class);
        intent.putExtras(data);
        context.startService(intent);
    }

    private HSManager mSdkManager;

    private HandlerThread handlerThread,mFileWriteThread;
    private Handler mThreadHandler,mFileWriteHandler;

    //插件管理类
    private PlugManager plugManager;


    private File mCmdFile;
    private FileOutputStream fileOutputStream;
    private FileChannel fileChannel;

    private HSHandleTouchEventImpl hsHandleTouchEvent;

    private HSKeyBeanManagerImpl hsKeyBeanManager;

    @Override
    public void onCreate() {
        super.onCreate();
        //实例化蓝牙SDK管理器
        mSdkManager = HSManager.getinstance(getApplicationContext(), DeviceConnectReceiveImpl.getInstance());
        mSdkManager.setTouchCmdReceive(this);
        //按键管理
        hsKeyBeanManager=new HSKeyBeanManagerImpl();
        hsHandleTouchEvent =HSHandleTouchEventImpl.getInstance(hsKeyBeanManager);
        mSdkManager.setDefineHsTouchEventProcess(hsHandleTouchEvent);
        //其它线程
        handlerThread = new HandlerThread("");
        mFileWriteThread=new HandlerThread("filewrite");
        handlerThread.start();
        mFileWriteThread.start();
        mFileWriteHandler=new Handler(mFileWriteThread.getLooper());
        mThreadHandler = new Handler(handlerThread.getLooper());
        plugManager = new PlugManager(this);
        mCmdFile = new File(HSManager.getContext().getExternalCacheDir() + "/touch.txt");
        try {
            fileOutputStream = new FileOutputStream(mCmdFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //解析参数
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            if (bundle.getBoolean(ServiceStartHelp.SHOW_PLUG, false)) {
                //显示悬浮球
                String pkgName = bundle.getString(ServiceStartHelp.PKGNAME);
                plugManager.show(pkgName);
            }
        }
        return START_STICKY;
    }


    public HSManager getmSdkManager() {
        return mSdkManager;
    }

    public HSKeyBeanManagerImpl getHsKeyBeanManager() {
        return hsKeyBeanManager;
    }

    public HSHandleTouchEventImpl getHsHandleTouchEvent() {
        return hsHandleTouchEvent;
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

    @Override
    public void onTouchCmdReceive(MotionEvent event) {
        //接收到合成的触摸事件
        Log.v("xuyeCmd", "" + event);

    }

    @Override
    public void onCmdStrReceive(final String command) {
        //接收到触摸指令数据
        Log.v("xuyeCmd", "" + command);
//        mFileWriteHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    fileChannel = fileOutputStream.getChannel();
//                    ByteBuffer byteBuffer = ByteBuffer.wrap(command.getBytes());
//                    fileChannel.write(byteBuffer, 0);
//                    Log.v("xuyeCmd", "" + command);
//                } catch (Exception e) {
//                    Log.v("xuyeCmd", "write error");
//                    e.printStackTrace();
//                } finally {
//
//                }
//            }
//        });


    }

    public class HandPlayBind extends Binder {

        public HandPlayBind() {
        }

        public HandPlayService getService() {
            return HandPlayService.this;
        }

    }

}
