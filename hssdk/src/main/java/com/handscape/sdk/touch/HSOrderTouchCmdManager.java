package com.handscape.sdk.touch;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MotionEvent;

import com.handscape.sdk.bean.HSBaseKeyBean;

import java.util.ArrayList;
import java.util.Arrays;;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 命令队列管理
 */
public class HSOrderTouchCmdManager {

    public static final String TAG = HSOrderTouchCmdManager.class.getName();

    //按照顺序保存命令队列
    public ConcurrentHashMap<Integer, OrderCmdBean> orderCmdBeanHashMap = new ConcurrentHashMap<>();
    ;

    private static HSOrderTouchCmdManager instance = new HSOrderTouchCmdManager();

    public static HSOrderTouchCmdManager getinstance() {
        return instance;
    }

    private HandlerThread mThread;
    private Handler mHandler;

    private HSOrderTouchCmdManager(){
        mThread=new HandlerThread("");
        mThread.start();
        mHandler=new Handler(mThread.getLooper());
    }

    public void addCmd(final int oldPointId, final int keyCode,final  int repeatCount,final  int index,
                       final  long delayTime,final  HSTouchCommand command, final List<HSBaseKeyBean> keyBeanList) {
        if (orderCmdBeanHashMap != null&&orderCmdBeanHashMap.get(keyCode)!=null && orderCmdBeanHashMap.get(keyCode).isInRepeat()) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                OrderCmdBean orderCmdBean = null;
                if (command.getAction() == MotionEvent.ACTION_DOWN && index == 0) {
                    //代表按下
                    orderCmdBean = new OrderCmdBean(oldPointId, keyCode, repeatCount, keyBeanList);
                } else {
                    orderCmdBean = orderCmdBeanHashMap.get(keyCode);
                }
                if (orderCmdBean != null) {
                    orderCmdBean.addCmd(command);
                    orderCmdBean.setDelayTime(index, delayTime);
                    orderCmdBeanHashMap.put(keyCode, orderCmdBean);
                }
            }
        });
    }

    public void setDelayTime(final int keyCode,final  int index,final  long delayTime) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                OrderCmdBean orderCmdBean = orderCmdBeanHashMap.get(keyCode);
                if (orderCmdBean != null) {
                    orderCmdBean.setDelayTime(index, delayTime);
                }
            }
        });

    }

    public HSTouchCommand[] getCmd() {
        if (orderCmdBeanHashMap != null && !orderCmdBeanHashMap.isEmpty()) {
            boolean flag = true;
            ArrayList<HSTouchCommand> arrayList = new ArrayList<>();
            HSTouchCommand[] commands = null;
            for (Integer key : orderCmdBeanHashMap.keySet()) {
                if (orderCmdBeanHashMap != null && !orderCmdBeanHashMap.isEmpty()
                        && orderCmdBeanHashMap.get(key) != null) {
                    HSTouchCommand cmd = orderCmdBeanHashMap.get(key).getCmd();
                    if (cmd != null) {
                        arrayList.add(cmd);
                    }
                }
            }
            commands = new HSTouchCommand[arrayList.size()];
            for (int a = 0; a < arrayList.size(); a++) {
                commands[a] = arrayList.get(a);
            }
            return commands;
        }
        return null;
    }


    static class OrderCmdBean {
        //对应的按键区域编码
        private int keyCode;
        //
        private int oldPointId;

        private List<HSBaseKeyBean> keyBeanList;

        //剩余重复次数
        private int repeatCount, totlaRepeatCount;
        //间隔时间数组
        private long[] delayTimeArray;
        //原始命令队列，从按下到抬起
        private LinkedBlockingDeque<HSTouchCommand> commandArrayList;
        private LinkedBlockingDeque<HSTouchCommand> commandArrayList2;

        private boolean shouldDelete = false;
        private boolean endFlag = false;

        public boolean isShouldDelete() {
            return shouldDelete;
        }

        private long downTime = 0, upTime = 0;

        private boolean isInRepeat = false;

        public boolean isInRepeat() {
            return isInRepeat;
        }

        private HSTouchCommand lastCmd;


        public OrderCmdBean(int oldPointId, int keyCode, int repeatCount, List<HSBaseKeyBean> keyBeanList) {
            this.oldPointId = oldPointId;
            this.keyCode = keyCode;
            this.repeatCount = repeatCount;
            this.totlaRepeatCount = repeatCount;
            this.keyBeanList = keyBeanList;
            this.delayTimeArray = new long[repeatCount];
            this.commandArrayList = new LinkedBlockingDeque<>(1000);
            this.commandArrayList2 = new LinkedBlockingDeque<>(1000);
        }

        public void setDelayTime(int index, long time) {
            if (delayTimeArray != null && index >= 0 && index < delayTimeArray.length) {
                delayTimeArray[index] = time;
            }
        }

        public void addCmd(HSTouchCommand command) {
            //去重处理，主要消除在MOVE的情况下，消除按键
            if (lastCmd != null && lastCmd.equals(command)) {
                return;
            }
            Log.v(TAG, "" + isInRepeat);
            //消除
            if (isInRepeat && (commandArrayList.size() > 0 || commandArrayList2.size() > 0)) {
                Log.v(TAG, "aaaaa");
                return;
            }


            if (commandArrayList != null && !commandArrayList.contains(command)) {
                lastCmd = command.copy();
                commandArrayList.add(command);
            }
        }

        public HSTouchCommand getCmd() {
            if (commandArrayList.size() > 0) {
                HSTouchCommand command = commandArrayList.getFirst();
                if (command == null || command.isAfterNow()) {
                    return null;
                } else {
                    if (keyBeanList != null && repeatCount > 0 && repeatCount <= keyBeanList.size()) {
                        int index = keyBeanList.size() - repeatCount;
                        if (command.getAction() == MotionEvent.ACTION_DOWN) {
                            endFlag = false;
                            downTime = command.getEventTime();
                            if (index > 0) {
                                keyBeanList.get(index - 1).getHsKeyData().setKeyPointId(0);
                            }
                        }
                        if (command.getAction() == MotionEvent.ACTION_UP) {
                            endFlag = true;
                            upTime = command.getEventTime();
                        }
                        command.setIndex(index);
                        commandArrayList2.add(command);
                        commandArrayList.removeFirst();
                        return command;
                    } else {
                        if (keyBeanList == null) {
                            command.setIndex(0);
                            commandArrayList.removeFirst();
                            return command;
                        }
                    }
                }
            } else {
                if (repeatCount > 1 && endFlag && commandArrayList2.size() > 0) {
                    isInRepeat = true;
                    long actionTime = upTime - downTime;
                    repeatCount--;
                    int index = keyBeanList.size() - repeatCount;
                    for (HSTouchCommand cmd : commandArrayList2) {
                        cmd.setEventTime(cmd.getEventTime() + actionTime + delayTimeArray[index]);
                    }
                    commandArrayList.addAll(commandArrayList2);
                    commandArrayList2.clear();
                }
                if (repeatCount == 1) {
                    commandArrayList2.clear();
                    isInRepeat = false;
                }
            }
            return null;
        }

        @Override
        public String toString() {

            return "keycode=" + keyCode + " oldPid=" + oldPointId + " beanSize=" + keyBeanList.size() + " repeatCount=" + repeatCount + " delayTimeArray=" + Arrays.toString(delayTimeArray) + " cmdArray=" + commandArrayList.toString();
        }
    }


}