package com.handscape.nativereflect;

import android.graphics.PointF;

import com.handscape.sdk.bean.HSBaseKeyBean;

/**
 * 按键
 */
public class KeyBean extends HSBaseKeyBean {

    @Override
    public PointF map(int mainPid, int index, int action, float touchX, float touchY, int keyCode) {
        return getHsKeyData().getPoint();
    }
}
