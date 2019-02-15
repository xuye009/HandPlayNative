package com.handscape.sdk.util;

import android.graphics.PointF;
import android.os.SystemClock;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;

import com.handscape.sdk.bean.HSBaseKeyBean;
import com.handscape.sdk.inf.IHSKeyBeanManager;

/**
 * 和多指令配合使用
 */
public class HSMultipleMotionBuilder {
    private String TAG = HSMultipleMotionBuilder.class.getSimpleName();
    private final int TOUCH_DEVICE_ID = 0xFFFF;

    private int screenRotation = 0, screenWidth = 0, screenheight = 0;

    private IHSKeyBeanManager iCommondManager = null;

    public HSMultipleMotionBuilder(int rotation, int screenWidth, int ScreenHeight, IHSKeyBeanManager iCommondManager) {
        this.screenRotation = rotation;
        this.screenWidth = screenWidth;
        this.screenheight = ScreenHeight;
        this.iCommondManager = iCommondManager;
    }

    public MotionEvent build(HSPacketData data) {
        int pointerCount = data.getIntPacketContent(3);
        if (pointerCount <= 0) {
            return null;
        }
        long nowTime = SystemClock.uptimeMillis();
        int action = getMotionAction(data, pointerCount);
        int mainAction = data.getIntPacketContent(2);
        PointerProperties[] properties = getPointerProperties(data, pointerCount);
        PointerCoords[] coords = getPointerCoords(data, pointerCount, mainAction);

        return MotionEvent.obtain(nowTime, nowTime, action,
                pointerCount, properties, coords,
                0, 0, 0.0f,
                0.0f, TOUCH_DEVICE_ID, 0,
//                InputDeviceCompat.SOURCE_TOUCHSCREEN,
                InputDevice.SOURCE_TOUCHSCREEN
                , 0);
    }

    private PointerProperties[] getPointerProperties(HSPacketData data, int count) {
        PointerProperties[] properties = new PointerProperties[count];
        for (int i = 0; i < count; i++) {
            properties[i] = new PointerProperties();
            properties[i].clear();
            properties[i].id = data.getIntPacketContent(5 + (i * 3));
            properties[i].toolType = MotionEvent.TOOL_TYPE_FINGER;
        }
        return properties;
    }


    private int temp = -1;

    /**
     * 一对多的时候，需要通过按键的PointId去寻找相应的映射点。
     * 以防止多个按键之间互相有影响
     *
     * @param data
     * @param count
     * @param action
     * @return
     */
    private PointerCoords[] getPointerCoords(HSPacketData data, int count, int action) {
        //从机器壳子数据获取多指触摸坐标的数据
        PointerCoords[] coords = new PointerCoords[count];
        int mainPtrId = data.getIntPacketContent(4);
        //根据壳子现在的手指数目进行循环
        for (int i = 0; i < count; i++) {
            //获取手机壳传递来的坐标数据X，Y
            int iPointId = data.getIntPacketContent(5 + (i * 3));
            int iX = data.getIntPacketContent(5 + (i * 3) + 1);
            int iY = data.getIntPacketContent(5 + (i * 3) + 2);
            coords[i] = new PointerCoords();
            coords[i].clear();
            //屏幕旋转角度-0的时候，手机壳的坐标刚好和屏幕一一对应
            float touchX = iX;
            float touchY = iY;
            if (iCommondManager != null) {
                HSBaseKeyBean hsBaseKeyBean= iCommondManager.getBean(iPointId);
                if(hsBaseKeyBean!=null){
                    int pKeyCode = hsBaseKeyBean.getHsKeyData().getKeyCode();
                    int pIndex = hsBaseKeyBean.getHsKeyData().getKeyIndex();
                    int pId = hsBaseKeyBean.getHsKeyData().getKeyPointId();
                    if (pId == iPointId) {
                        PointF mapPoint = hsBaseKeyBean.map(mainPtrId, hsBaseKeyBean.getHsKeyData().getKeyIndex(), action, touchX, touchY, pKeyCode);
                        if (mapPoint != null) {
                            touchX = mapPoint.x;
                            touchY = mapPoint.y;
                        }
                    }
                }
            }
            //设置处理完成后的坐标点
            coords[i].x = touchX;
            coords[i].y = touchY;
            coords[i].setAxisValue(MotionEvent.AXIS_X, touchX);
            coords[i].setAxisValue(MotionEvent.AXIS_Y, touchY);
            //设置触控区域的大小
            coords[i].setAxisValue(MotionEvent.AXIS_TOOL_MAJOR, 200);
            coords[i].setAxisValue(MotionEvent.AXIS_TOOL_MINOR, 200);
            coords[i].setAxisValue(MotionEvent.AXIS_TOUCH_MAJOR, 200);
            coords[i].setAxisValue(MotionEvent.AXIS_TOUCH_MINOR, 200);
            coords[i].pressure = 0.68f;
            coords[i].size = 0.6f;
        }
        return coords;
    }

    private int getMotionAction(HSPacketData data, int pointerCount) {
        int action = 0;
        int mainAction = data.getIntPacketContent(2);
        int mainPtrId = data.getIntPacketContent(4);
        action = mainAction;
        if (pointerCount <= 1) {
            return action;
        }
        int ptrIndex = 0;
        for (int i = 0; i < pointerCount; i++) {
            if (mainPtrId == data.getIntPacketContent(5 + (i * 3))) {
                ptrIndex = i;
                break;
            }
        }
        if (mainAction == MotionEvent.ACTION_DOWN) {
            action = ptrIndex << MotionEvent.ACTION_POINTER_INDEX_SHIFT | MotionEvent.ACTION_POINTER_DOWN;
        }
        if (mainAction == MotionEvent.ACTION_UP) {
            action = ptrIndex << MotionEvent.ACTION_POINTER_INDEX_SHIFT | MotionEvent.ACTION_POINTER_UP;
        }
        return action;
    }
}
