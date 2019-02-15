package com.handscape.nativereflect.inf;

public interface IHSReceiveEvent {

    //接收到触摸指令
    void receive(int keycode,int touchAction, int pointerID, float eventX, float eventY);
}
