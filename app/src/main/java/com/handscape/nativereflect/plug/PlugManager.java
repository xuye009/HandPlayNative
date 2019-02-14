package com.handscape.nativereflect.plug;

import android.content.Context;
import android.view.WindowManager;

/**
 * 插件管理类
 */
public class PlugManager {


    private Context context;
    private WindowManager windowManager;

    public PlugManager(Context context){
        this.context=context;
        this.windowManager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }




}
