package com.handscape.sdk.touch;

import android.util.Log;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import com.handscape.sdk.inf.IHSTouchCmdReceive;
import com.handscape.sdk.util.HSMultipleMotionBuilder;
import com.handscape.sdk.util.HSPacketData;
import com.handscape.sdk.util.HSTouchMapKeyUtils;
import com.handscape.sdk.inf.IHSKeyBeanManager;

/**
 * 触摸指令调度器
 */
public class HSTouchDispatch implements Runnable {


    public static final String TAG = HSTouchDispatch.class.getName();

    private Thread mDispatchThread;

    private final int MAX_COMMAND_COUNT = HSTouchCommand.MAX_TOUCH_COMMAND_COUNT;
    //存储当前需要执行的命令
    private ArrayBlockingQueue<HSTouchCommand> mTouchCommandQueue = new ArrayBlockingQueue<>(1000);
    //存储以后需要执行的命令
    private ArrayList<HSTouchCommand> mScheduledTouch = new ArrayList<>(1000);
    //存储顺序执行的命令
    private HSOrderTouchCmdManager hsOrderTouchCmdManager = HSOrderTouchCmdManager.getinstance();
    //    private ArrayList<HSTouchCommand> mOrderTouchQueue = new ArrayList<>(1000);
    //生成触摸指令用到的数据结构
    private HSTouchCommand[] mCommands = new HSTouchCommand[this.MAX_COMMAND_COUNT];
    //代表当前的目标指令
    private HSTouchCommand mCurrentCmd = null;
    //标记当前是否有指令在执行
    private boolean isEmptyPending = false;

    private IHSKeyBeanManager commondManager;

    public void setKeyBeanManager(IHSKeyBeanManager commondManager) {
        this.commondManager = commondManager;
    }

    private IHSTouchCmdReceive touchCmdReceive;

    public void setTouchCmdReceive(IHSTouchCmdReceive touchCmdReceive) {
        this.touchCmdReceive = touchCmdReceive;
    }

    private static int screenWidth, screenHeight;

    private boolean flag = false;



    public HSTouchDispatch() {
        int[] size = HSTouchMapKeyUtils.getScreenSize();
        screenWidth = size[0];
        screenHeight = size[1];
        flag = true;
        mDispatchThread = new Thread(this);
        mDispatchThread.setPriority(Thread.MAX_PRIORITY);
        mDispatchThread.start();
    }


    @Override
    public void run() {
        while (flag) {
            try {
                checkOrder();
                checkScheduledCmd();
                dispatchCmd();
                Thread.sleep(1);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 延时执行的命令逻辑
     *
     * @throws Exception
     */
    private void checkScheduledCmd() throws Exception {
        Iterator<HSTouchCommand> touchs = this.mScheduledTouch.iterator();
        while (touchs.hasNext()) {
            HSTouchCommand cmd = touchs.next();
            if (!cmd.isAfterNow()) {
                touchs.remove();
                addCmd(cmd);
            }
        }
    }

    private void checkOrder() {
        HSTouchCommand[] command = hsOrderTouchCmdManager.getCmd();
        if (command != null && command.length > 0) {
            addCmd(command);
        }
    }

    //执行可以执行的命令
    private void dispatchCmd() throws Exception {
        if (mTouchCommandQueue.size() > 0) {
            ArrayList<HSTouchCommand> queue = new ArrayList<>();
            this.mTouchCommandQueue.drainTo(queue);
            for (HSTouchCommand command : queue) {
                //去掉不可用的点
                if (!HSTouchCommand.isInvalidTouch(command.getId())) {
                    if (command.isAfterNow()) {
                        mScheduledTouch.add(command);
                        continue;
                    }
                    mCommands[command.getId() - 1] = command;
                    mCurrentCmd = command;
                    isEmptyPending = false;
                    String cmdStr = makeTouchTrimString(mCurrentCmd);
                    String motionStr=makeTouchEventString(mCurrentCmd);
                    sendTouchCommandData(cmdStr,motionStr);
                    if (this.mCurrentCmd.getAction() == MotionEvent.ACTION_UP) {
                        this.mCommands[this.mCurrentCmd.getId() - 1] = null;
                    }
                    this.mCurrentCmd = null;
                    this.isEmptyPending = true;

                }
            }
        }
    }

    /**
     * 添加触摸指令
     * 参数是单个指令
     * 如果上次的点和这一次一样，则不添加，去掉重复点
     *
     * @param command
     */
    public void addCmd(final HSTouchCommand command) {

        if (command == null) {
            return;
        }

        if (command.isAfterNow()) {
            //如果需要延迟执行，则将任务放在延迟的队列里面
            if (mScheduledTouch.contains(command)) {
                mScheduledTouch.add(command);
            }
        } else {
            if (!mTouchCommandQueue.contains(command)) {
                mTouchCommandQueue.offer(command);
            }
        }
    }


    /**
     * 添加触摸指令
     * 参数为数组
     *
     * @param command
     */
    public void addCmd(final HSTouchCommand[] command) {
        if (command == null || command.length == 0) {
            return;
        }
        for (int i = 0; i < command.length; i++) {
            addCmd(command[i]);
        }
    }

    public void sendTouchCommandData(final String command,final String eventStr) {
        Log.v(TAG, getTouchCount() + " " + command+" "+eventStr);
        try {
            if (command == null) {
                return;
            }
            if (!isEmptyPending) {
                if(touchCmdReceive!=null){
                    touchCmdReceive.onCmdStrReceive(command);
                    int rotation = HSTouchMapKeyUtils.getScreenRotation();
                    final MotionEvent event = new HSMultipleMotionBuilder(rotation, screenWidth, screenHeight, commondManager).build(new HSPacketData("0|sendevent " + eventStr));
                    touchCmdReceive.onTouchCmdReceive(event);
                }
            }
        } catch (Exception e) {
            this.mCurrentCmd = null;
            this.isEmptyPending = true;
            removeAllCmd();
            e.printStackTrace();
        }
    }

    public void removeAllCmd() {
        for (int i = 0; i < mCommands.length; i++) {
            mCommands[i] = null;
        }
    }

    /*
     * 生成触摸指令]"
     * */
    private String makeTouchEventString(HSTouchCommand command) {
        String result = "touch " + command.getAction()
                + " " + getTouchCount() + " " + command.getId()
                + " " + touchedCmd2String();
        return result;
    }


    private String makeTouchTrimString(HSTouchCommand command) {
        StringBuilder builder = new StringBuilder();
        int count = getTouchCount();
        int id = command.getId();
        builder.append(command.getAction());
        builder.append(" ");
        if (count < 10) {
            builder.append("0" + count);
        }
        builder.append(" ");
        if (id < 10) {
            builder.append("0" + id);
        } else {
            builder.append(id);
        }
        builder.append(" ");
        builder.append(touchedCmd2TrimString());
        return builder.toString();
    }


    private String touchedCmd2String() {
        StringBuilder builder = new StringBuilder();
        boolean firstCmd = true;
        for (int i = 0; i < this.MAX_COMMAND_COUNT; i++) {
            if (this.mCommands[i] != null) {
                if (firstCmd) {
                    firstCmd = false;
                } else {
                    builder.append(" ");
                }
                builder.append(this.mCommands[i].getStream());
            }
        }
        return builder.toString();
    }

    private String touchedCmd2TrimString() {
        StringBuilder builder = new StringBuilder();
        boolean firstCmd = true;
        for (int i = 0; i < this.MAX_COMMAND_COUNT; i++) {
            if (this.mCommands[i] != null) {
                if (firstCmd) {
                    firstCmd = false;
                } else {
                    builder.append(" ");
                }
                builder.append(this.mCommands[i].getTrimStream());
            }
        }
        return builder.toString();
    }


    private int getTouchCount() {
        int count = 0;
        for (int i = 0; i < MAX_COMMAND_COUNT; i++) {
            if (this.mCommands[i] != null) {
                count++;
            }
        }
        return count;
    }


}
