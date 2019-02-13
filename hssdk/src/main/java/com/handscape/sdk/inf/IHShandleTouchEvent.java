package com.handscape.sdk.inf;

import com.handscape.sdk.touch.HSTouchCommand;

public interface IHShandleTouchEvent {
    HSTouchCommand[] handleTouchEvent(int touchAction, int pointerID, float eventX,
                                    float eventY, float windowWidth, float windowHeight);
}
