package com.handscape.sdk.touch;


import android.util.Log;

public class HSTouchCommand {    //class tf
    public static int MAX_TOUCH_COMMAND_COUNT = 32;
    public static int CUSTOM_TOUCH_START_INDEX = 1;
    public static boolean[] commandEvents = new boolean[MAX_TOUCH_COMMAND_COUNT];

    private long eventTime;
    private int mPointerId;
    private int action;
    private int mPx;
    private int mPy;

    private final int mkeyCode;//触摸命令所属的keyCode
    private int mIndex;//事件所处的顺序
    private long mDelayTime;//距离上一个事件的间隔
    private boolean isRemoveOrder = false;//是否从命令队列移除
    private boolean orderFlag = false;//是否是命令指令

    public static int setNewTouchDown() {
        synchronized (commandEvents) {
            for (int tchId = CUSTOM_TOUCH_START_INDEX; tchId < MAX_TOUCH_COMMAND_COUNT; tchId++) {
                if (!commandEvents[tchId]) {
                    commandEvents[tchId] = true;
                    return tchId;
                }
            }
            return 0;
        }
    }

    public static void setTouchUp(int tchId) {
        synchronized (commandEvents) {
            commandEvents[tchId] = false;
        }
    }

    public static void reset() {
        synchronized (commandEvents) {
            for (int index = CUSTOM_TOUCH_START_INDEX; index < MAX_TOUCH_COMMAND_COUNT; index++) {
                commandEvents[index] = false;
            }
        }
    }

    public boolean isRemoveOrder() {
        return isRemoveOrder;
    }

    public boolean isOrderFlag() {
        return orderFlag;
    }

    public void setOrderFlag(boolean orderFlag) {
        this.orderFlag = orderFlag;
    }

    public void setRemoveOrder(boolean removeOrder) {
        isRemoveOrder = removeOrder;
    }

    public static boolean isInvalidTouch(int touchId) {
        return touchId > MAX_TOUCH_COMMAND_COUNT || touchId < CUSTOM_TOUCH_START_INDEX;
    }

    public static HSTouchCommand makeCommand(int pointerId, int mkeyCode, int action, int pX, int pY, long time, int mIndex, long mDelayTime) {
        return new HSTouchCommand(pointerId, mkeyCode, action, pX, pY, time, mIndex, mDelayTime);
    }

    public static HSTouchCommand newCommand(int pointerId, int mkeyCode, int action, int px, int py) {
        return new HSTouchCommand(pointerId, mkeyCode, action, px, py, System.currentTimeMillis(), 0, 0);
    }

    HSTouchCommand(int pointerId, int mkeyCode, int action, int px, int py, long time, int mIndex, long mDelayTime) {
        this.mkeyCode = mkeyCode;
        this.mPointerId = pointerId;
        this.action = action;
        this.eventTime = time;
        this.mPx = px;
        this.mPy = py;
        this.mIndex = mIndex;
        this.mDelayTime = mDelayTime;
    }

    public void setPointerId(int mPointerId) {
        this.mPointerId = mPointerId;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public int getkeyCode() {
        return mkeyCode;
    }

    public int getIndex() {
        return mIndex;
    }

    public long getDelayTime() {
        return mDelayTime;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    public void setDelayTime(long mDelayTime) {
        this.mDelayTime = mDelayTime;
    }

    public int getId() {
        return this.mPointerId;
    }

    public int getX() {
        return this.mPx;
    }

    public int getY() {
        return this.mPy;
    }

    public void releaseTouch() {
        setTouchUp(this.mPointerId);
    }

    public String getStream() {
        return this.mPointerId + " " + this.mPx + " " + this.mPy;
    }


    public String getTrimStream() {

        StringBuilder builder = new StringBuilder();
        int id = this.mPointerId;
        int action = this.action;
        int x = this.mPx;
        int y = this.mPy;

        if (id < 10) {
            builder.append("0" + id);
        } else {
            builder.append(id);
        }
        builder.append(" ");
        builder.append(action);
        builder.append(" ");
        if (x < 10) {
            builder.append("000" + x);
        } else if (x >= 10 && x < 100) {
            builder.append("00" + x);
        } else if (x >= 100 && x < 1000) {
            builder.append("0" + x);
        } else {
            builder.append(x);
        }
        builder.append(" ");
        if (y < 10) {
            builder.append("000" + y);
        } else if (y >= 10 && y < 100) {
            builder.append("00" + y);
        } else if (y >= 100 && y < 1000) {
            builder.append("0" + y);
        } else {
            builder.append(y);
        }

        return builder.toString();
    }

    public int getAction() {
        return action;
    }

    public void setPos(int px, int py) {
        this.mPx = px;
        this.mPy = py;
    }

    public boolean isValidPos() {
        return (this.mPx == -1 || this.mPy == -1) ? false : true;
    }

    public boolean isAfterNow() {
        return nowTime() < this.eventTime;
    }

    protected long nowTime() {
        return System.currentTimeMillis();
    }

    public HSTouchCommand copy() {
        HSTouchCommand cmd = HSTouchCommand.makeCommand(mPointerId, mkeyCode, action, mPx, mPy, eventTime, mIndex, mDelayTime);
        return cmd;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        HSTouchCommand command = (HSTouchCommand) obj;
        if (this.mPointerId != command.mPointerId) {
            return false;
        }
        if (this.action != command.action) {
            return false;
        }
        if (this.mPx != command.mPx) {
            return false;
        }
        if (this.mPy != command.mPy) {
            return false;
        }
//        if (this.eventTime != command.eventTime) {
//            return false;
//        }
        return true;
    }

    @Override
    public String toString() {
        return "TouchCommand(" + this.mPointerId + ", " + this.action + ", " + this.mPx + ", " + this.mPy + ", " + this.eventTime + ")";
    }
}
