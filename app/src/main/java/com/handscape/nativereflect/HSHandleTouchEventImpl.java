package com.handscape.nativereflect;

import android.graphics.PointF;
import android.view.MotionEvent;

import com.handscape.nativereflect.inf.IHSReceiveEvent;
import com.handscape.sdk.inf.IHShandleTouchEvent;
import com.handscape.sdk.touch.HSTouchCommand;
import com.handscape.sdk.util.HSTouchMapKeyUtils;

import java.util.HashMap;
import java.util.Map;

public class HSHandleTouchEventImpl implements IHShandleTouchEvent {

    private static HSHandleTouchEventImpl instance = null;

    public static HSHandleTouchEventImpl getInstance(HSKeyBeanManagerImpl hsKeyBeanManager) {
        if (instance == null) {
            instance = new HSHandleTouchEventImpl(hsKeyBeanManager);
        }
        return instance;
    }

    private IHSReceiveEvent ihsReceiveEvent;

    public void setIhsReceiveEvent(IHSReceiveEvent ihsReceiveEvent) {
        this.ihsReceiveEvent = ihsReceiveEvent;
    }

    //存储id和Keycode的对应
    private Map<Integer, Integer> oldId2KeyCode = new HashMap<>();
    private HSKeyBeanManagerImpl hsKeyBeanManager;

    private HSHandleTouchEventImpl(HSKeyBeanManagerImpl hsKeyBeanManager) {
        this.hsKeyBeanManager = hsKeyBeanManager;

    }

    @Override
    public HSTouchCommand[] handleTouchEvent(int touchAction, int pointerID, float eventX, float eventY, float windowWidth, float windowHeight) {
        //获取到原始数据
        int keycode = 0;
        if (touchAction == MotionEvent.ACTION_DOWN) {
            keycode = HSTouchMapKeyUtils.makeKeycode(eventX, eventY);
            oldId2KeyCode.put(pointerID, keycode);
        } else {
            if (oldId2KeyCode != null && oldId2KeyCode.get(pointerID) != null) {
                keycode = oldId2KeyCode.get(pointerID);
            }
        }
        if (ihsReceiveEvent != null) {
            ihsReceiveEvent.receive(keycode,touchAction, pointerID, eventX, eventY);
        }

        HSTouchCommand[] commands = new HSTouchCommand[1];
        if(hsKeyBeanManager!=null&&hsKeyBeanManager.getBean(pointerID)!=null){
            PointF pointF= hsKeyBeanManager.getBean(pointerID).map(pointerID,0,touchAction,eventX,eventY,keycode);
            HSTouchCommand cmd = HSTouchCommand.newCommand(pointerID, keycode, touchAction, (int) pointF.x, (int) pointF.y);
            commands[0] = cmd;
        }else{
            HSTouchCommand cmd = HSTouchCommand.newCommand(pointerID, keycode, touchAction, (int) eventX, (int) eventY);
            commands[0] = cmd;
        }
        return commands;
    }
}
