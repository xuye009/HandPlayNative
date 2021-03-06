package com.handscape.sdk.inf;

import android.view.MotionEvent;

/**
 * 触摸指令接收类
 */
public interface IHSTouchCmdReceive {
    //指令接收器
    void onTouchCmdReceive(MotionEvent event);
    //接收到的触摸指令字符
    void onCmdStrReceive(String cmd);
}
