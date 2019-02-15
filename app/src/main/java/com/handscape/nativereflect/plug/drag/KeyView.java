package com.handscape.nativereflect.plug.drag;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.handscape.nativereflect.plug.PlugManager;

/**
 * 按键
 */
public class KeyView extends BaseDragView{
    public KeyView(@NonNull Context context, PlugManager plugManager) {
        super(context, plugManager);
    }

    @Override
    public WindowManager.LayoutParams getLayoutParams() {
        return null;
    }

    @Override
    protected void onDown(MotionEvent event) {

    }

    @Override
    protected void onUp(float cx, float cy) {

    }

    @Override
    protected void onMove(MotionEvent event) {

    }

    @Override
    protected boolean isclick() {
        return false;
    }

    @Override
    protected View getContentView() {
        return null;
    }
}
