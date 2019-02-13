package com.handscape.sdk;

import android.util.Log;
import android.view.MotionEvent;

import com.handscape.sdk.touch.HSTouchCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 机器背壳按键ID生成类
 */
public class HSKeyPointIdManager {

    private static final String TAG = HSKeyPointIdManager.class.getName();

    //按键ID和区域对应的表
    private HashMap<Integer, Integer> pId2KeyCodeMap = new HashMap<>();

    //旧ID和新id
    private HashMap<Integer, Integer> oldPid2newPid = new HashMap<>();

    //存储id的数组
    private int[] pIdArray = new int[HSTouchCommand.MAX_TOUCH_COMMAND_COUNT];


    private static HSKeyPointIdManager instance = new HSKeyPointIdManager();

    public static HSKeyPointIdManager getInstance() {
        return instance;
    }

    public HSKeyPointIdManager() {
        clear();
    }


    /**
     * 生成新的id
     *
     * @return
     */
    public int makePointId(int action, int keycode, int oldId) {
        int pointId = 0;
        switch (action) {
            case MotionEvent.ACTION_DOWN://按下
                if (oldPid2newPid.get(oldId) == null) {
                    for (int i = 0; i < pIdArray.length; i++) {
                        if (pIdArray[i] == -1) {
                            pointId = i + 1;
                            pIdArray[i] = pointId;
                            break;
                        }
                    }
                    pId2KeyCodeMap.put(pointId, keycode);
                    oldPid2newPid.put(oldId, pointId);
                } else {
                    pointId = oldPid2newPid.get(oldId);
                }
                break;
            case MotionEvent.ACTION_MOVE://移动
                if (oldPid2newPid.get(oldId) != null) {
                    pointId = oldPid2newPid.get(oldId);
                }
                break;
            case MotionEvent.ACTION_UP://抬起
                if (oldPid2newPid.get(oldId) != null) {
                    pointId = oldPid2newPid.get(oldId);
                    pIdArray[pointId - 1] = -1;
                    pId2KeyCodeMap.remove(pointId);
                }
                oldPid2newPid.remove(oldId);
                Log.v(TAG, Arrays.toString(pIdArray));
                break;
        }
        return pointId;
    }

    /**
     * 根据id获取是哪个区域的按键
     *
     * @param pointId
     * @return
     */
    public int getKeyCode(int pointId) {
        if (pId2KeyCodeMap.containsKey(pointId)) {
            return pId2KeyCodeMap.get(pointId);
        }
        return -1;
    }

    /**
     * @return 已经存在的keyId
     */
    public List<Integer> getAllKeyCodes() {
        ArrayList<Integer> keycodeList = new ArrayList<>();
        ArrayList<Integer> returndata=new ArrayList<>();
        if(pId2KeyCodeMap!=null){
            keycodeList.addAll(pId2KeyCodeMap.values());
        }
        for(int i=0;i<keycodeList.size();i++){
            if(!returndata.contains(keycodeList.get(i))){
                returndata.add(keycodeList.get(i));
            }
        }
        return returndata;
    }

    /**
     * 根据oldId获取新的id
     */
//    public int getPidbyOldId(int oldId) {
//        if (oldPid2newPid.containsKey(oldId)) {
//            return oldPid2newPid.get(oldId);
//        }
//        return -1;
//    }

//    public int getCodeByOldId(int oldId) {
//        int newId = getPidbyOldId(oldId);
//        return getKeyCode(newId);
//    }


    /**
     * 清空数据
     */
    public void clear() {
        for (int i = 0; i < pIdArray.length; i++) {
            pIdArray[i] = -1;
        }
        pId2KeyCodeMap.clear();
        oldPid2newPid.clear();
    }


}
