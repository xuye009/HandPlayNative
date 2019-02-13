package com.handscape.sdk.ble;

import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import com.handscape.sdk.touch.HSTouchCommand;

import java.util.Arrays;

/**
 * 错误点处理
 */
class HSCmdDeal {

    private Cmd[] cmdArrays = new Cmd[HSTouchCommand.MAX_TOUCH_COMMAND_COUNT];

    public HSCmdDeal() {

    }

    public void add(int touchAction, int pointerID, float eventX, float eventY) {
        Cmd cmd = cmdArrays[pointerID];
        if (cmd != null) {
            cmd.set(touchAction, eventX, eventY, SystemClock.uptimeMillis());
        } else {
            cmd = new Cmd(pointerID,touchAction, eventX, eventY, SystemClock.uptimeMillis());
        }
        cmdArrays[pointerID] = cmd;
    }

    public void check(int action) {
        for (int i = 0; i < cmdArrays.length; i++) {
            Cmd cmd = cmdArrays[i];
            if (cmd != null&&action==MotionEvent.ACTION_UP) {
                cmd.loginfo();
            }
        }


    }

    private class Cmd {
        public Cmd(int id,int touchAction, float eventX, float eventY, long time) {
            this.pointId=id;
            this.touchAction[touchAction] = touchAction;
            this.eventX = eventX;
            this.eventY = eventY;
            this.time[touchAction] = time;
        }

        public void set(int touchAction, float eventX, float eventY, long time) {
            this.touchAction[touchAction] = touchAction;
            this.eventX = eventX;
            this.eventY = eventY;
            this.time[touchAction] = time;
        }

        int[] touchAction = new int[]{-1, -1, -1};
        float eventX;
        float eventY;
        int pointId=-1;
        long[] time = new long[]{SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), SystemClock.uptimeMillis()};
        long[] timeS=new long[]{SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), SystemClock.uptimeMillis()};
        public void loginfo() {
            timeS[0]=  SystemClock.uptimeMillis()-time[0];
            timeS[1] =  SystemClock.uptimeMillis()-time[2] ;
            timeS[2] =  SystemClock.uptimeMillis()-time[1] ;

            Log.v("xuyearraycmd", "时间差="+pointId + Arrays.toString(timeS) + " " + Arrays.toString(touchAction));
        }


    }

}
