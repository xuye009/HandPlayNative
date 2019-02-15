package com.handscape.nativereflect.plug;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

import com.handscape.nativereflect.plug.drag.FloatBar;
import com.handscape.nativereflect.plug.drag.PlugMainBar;
import com.handscape.nativereflect.service.HandPlayService;

/**
 * 插件管理类
 */
public class PlugManager{


    private WindowManager windowManager;
    private String pkgName;

    //标记是否是在设置按键页面

    //悬浮球
    private FloatBar floatBar;
    //设置界面
    private PlugMainBar plugMainBar;
    //服务
    private HandPlayService handPlayService;

    public HandPlayService getHandPlayService() {
        return handPlayService;
    }

    public PlugManager(HandPlayService handPlayService) {
        this.handPlayService=handPlayService;
        this.windowManager = (WindowManager) handPlayService.getSystemService(Context.WINDOW_SERVICE);
    }

    public void show(String pkgName) {
        //显示悬浮球
        this.pkgName = pkgName;
        floatBar = FloatBar.getinstance(handPlayService, true, this);
        windowManager.addView(floatBar, floatBar.getmBarLayoutParams());
    }

    public void updateview(View view, WindowManager.LayoutParams params) {
        windowManager.updateViewLayout(view, params);
    }

    public void removeView(View view) {
        if(view instanceof PlugMainBar){
            handPlayService.getHsHandleTouchEvent().setIhsReceiveEvent(null);
        }
        windowManager.removeView(view);
    }

    //显示悬浮球
    public void showFloatBar(){
        floatBar.setVisibility(View.VISIBLE);
    }


    //显示设置菜单
    public void showPlugMain() {
        plugMainBar = new PlugMainBar(handPlayService, this);
        handPlayService.getHsHandleTouchEvent().setIhsReceiveEvent(plugMainBar);
        windowManager.addView(plugMainBar, plugMainBar.getLayoutParams());
        //隐藏悬浮球
        floatBar.setVisibility(View.GONE);
    }

    public void addView(View view, WindowManager.LayoutParams params){
        windowManager.addView(view,params);
    }


}
