package com.handscape.sdk;

import com.handscape.sdk.inf.IHSKeyBeanManager;
import com.handscape.sdk.inf.IHSTouchCmdReceive;
import com.handscape.sdk.touch.HSTouchCommand;
import com.handscape.sdk.touch.HSTouchDispatch;

/**
 * 触摸管理器
 */
class HSTouchManager {


    private HSTouchDispatch hsTouchDispatch;

    public HSTouchManager() {
        hsTouchDispatch = new HSTouchDispatch();
    }

    public HSTouchDispatch getHsTouchDispatch() {
        return hsTouchDispatch;
    }

    public void setKeyBeanManager(IHSKeyBeanManager commondManager) {
        if (hsTouchDispatch != null) {
            hsTouchDispatch.setKeyBeanManager(commondManager);
        }
    }

    public void setTouchCmdReceive(IHSTouchCmdReceive touchCmdReceive) {
        if (hsTouchDispatch != null) {
            hsTouchDispatch.setTouchCmdReceive(touchCmdReceive);
        }
    }

    /**
     * 添加触摸指令
     *
     * @param command
     */
    public void addCmd(HSTouchCommand command) {
        if (hsTouchDispatch != null) {
            hsTouchDispatch.addCmd(command);
        }
    }


}
