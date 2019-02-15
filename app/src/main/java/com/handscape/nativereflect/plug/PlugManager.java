package com.handscape.nativereflect.plug;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

import com.handscape.nativereflect.plug.drag.FloatBar;
import com.handscape.nativereflect.plug.drag.PlugMainBar;

/**
 * 插件管理类
 */
public class PlugManager {


    private Context context;
    private WindowManager windowManager;
    private String pkgName;
    //悬浮球
    private FloatBar floatBar;
    //设置界面
    private PlugMainBar plugMainBar;

    public PlugManager(Context context) {
        this.context = context;
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void show(String pkgName) {
        //显示悬浮球
        this.pkgName = pkgName;
        floatBar = FloatBar.getinstance(context, true, this);
        windowManager.addView(floatBar, floatBar.getmBarLayoutParams());
    }

    public void updateview(View view, WindowManager.LayoutParams params) {
        windowManager.updateViewLayout(view, params);
    }

    public void removeView(View view) {
        windowManager.removeView(view);
    }

    //显示悬浮球
    public void showFloatBar(){
        floatBar.setVisibility(View.VISIBLE);
    }


    //显示设置菜单
    public void showPlugMain() {
        plugMainBar = new PlugMainBar(context, this);
        windowManager.addView(plugMainBar, plugMainBar.getLayoutParams());
        //隐藏悬浮球
        floatBar.setVisibility(View.GONE);
    }


}
