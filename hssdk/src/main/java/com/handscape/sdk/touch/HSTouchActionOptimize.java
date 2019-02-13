package com.handscape.sdk.touch;

/**
 * 优化触摸数据
 * 消除
 */
class HSTouchActionOptimize implements Runnable {

    public static final String TAG = HSTouchActionOptimize.class.getName();

    private static HSTouchActionOptimize instance = null;

    public static HSTouchActionOptimize getInstance(HSCharacteristHandle characteristHandle) {
        if (instance == null) {
            instance = new HSTouchActionOptimize(characteristHandle);
        }
        return instance;
    }

    //检测错误点的线程
    private Thread mThread;

    private HSCharacteristHandle hsCharacteristHandle;

    private boolean flag = true, sengFlag = false;

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    private HSTouchActionOptimize(HSCharacteristHandle characteristHandle) {
        this.hsCharacteristHandle = characteristHandle;
        mThread = new Thread(this);
        mThread.start();
    }

    //背部机壳支持最大5根手指
    private TouchActionBean[] touchActionBean = new TouchActionBean[32];


    public boolean optimize(int touchID, int x, int y, int state, byte vector) {
        TouchActionBean actionBean = new TouchActionBean(touchID, x, y, state, vector, System.currentTimeMillis());
        //判断是否为重复点
        if (touchActionBean[touchID] == null || !actionBean.equals(touchActionBean[touchID])) {
            touchActionBean[touchID] = actionBean;
            return true;
        } else {
            //更新时间
            touchActionBean[touchID].time = System.currentTimeMillis();
            touchActionBean[touchID].setSendFlag(false);
            return false;
        }
    }

    @Override
    public void run() {
        while (flag) {
            for (int i = 0; i < touchActionBean.length; i++) {
                TouchActionBean bean = touchActionBean[i];
                if (bean != null) {
                    long time = bean.time;
                    int state = bean.state;
                    boolean sendflag=bean.isSendFlag();
                    //检测在按下或者移动状态停止超过200毫秒
                    if (!sendflag&&(state == 0 || state == 1) && ((time - System.currentTimeMillis()) < -200)) {
                        hsCharacteristHandle.sendPoint(bean.touchId, bean.eventX, bean.eventY, 2, bean.vector);
                        bean.setSendFlag(true);
                    }
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    class TouchActionBean {

        byte vector;
        int touchId;
        int eventX;
        int eventY;
        int state;
        long time;
        boolean sendFlag=false;

        public TouchActionBean(int touchId, int eventX, int eventY, int state, byte vector, long time) {
            this.touchId = touchId;
            this.eventX = eventX;
            this.eventY = eventY;
            this.state = state;
            this.vector = vector;
            this.time = time;
            this.sendFlag=false;
        }

        public boolean isSendFlag() {
            return sendFlag;
        }

        public void setSendFlag(boolean sendFlag) {
            this.sendFlag = sendFlag;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj instanceof TouchActionBean) {
                TouchActionBean bean = (TouchActionBean) obj;
                if (touchId != bean.touchId ||
                        eventX != bean.eventX ||
                        eventY != bean.eventY ||
                        state != bean.state) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }
    }


}
