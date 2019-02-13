package com.handscape.sdk.touch;

import android.util.Log;

/**
 * 记录机壳是否漏点
 */
class HSTouchDebug {

    private static final String TAG = HSTouchDebug.class.getName();

    private static HSTouchDebug instance = new HSTouchDebug();

    public static HSTouchDebug getInstance() {
        return instance;
    }

    //总计的
    private long downNumber = 0;
    private long otherNumber = 0;
    private long upNumber = 0;
    private long[] downArray = new long[32];
    private long[] upArray = new long[32];
    private long[] otherArray = new long[32];

    private HSTouchDebug() {

    }


    public void addPoint(int touchID, int x, int y, int state) {

        switch (state) {
            case 0:
                //按下
                Log.v(TAG, "0 info id=" + touchID + " x=" + x + " y=" + y + " state=" + state);
                downNumber++;
                downArray[touchID]++;
                break;
            case 1:
                Log.v(TAG, "1 info id=" + touchID + " x=" + x + " y=" + y + " state=" + state);
                //移动
                break;
            case 2:
                //抬起
                Log.v(TAG, "2 info id=" + touchID + " x=" + x + " y=" + y + " state=" + state);
                upNumber++;
                upArray[touchID]++;
                break;
            default:
                //抬起
                Log.v(TAG, "default info id=" + touchID + " x=" + x + " y=" + y + " state=" + state);
                otherNumber++;
                otherArray[touchID]++;
                break;
        }

        if (state == 2) {
            Log.v(TAG, "total down=" + downNumber + " total up=" + (upNumber + otherNumber));
//            log();
        }
    }


    private void log() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            if (downArray[i] != 0) {
                builder.append("id=" + i + " down=" + downArray[i]);
                if (upArray[i] != 0) {
                    builder.append(" up=" + (upArray[i] + otherArray[i]) + "\n");
                }
                if (downArray[i] == upArray[i] && downArray[i] == Long.MAX_VALUE) {
                    downArray[i] = 0;
                    upArray[i] = 0;
                }
            }
        }
        Log.v(TAG, builder.toString());


    }


}
