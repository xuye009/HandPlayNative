package com.handscape.nativereflect;

import android.util.Log;

import com.handscape.sdk.bean.HSBaseKeyBean;
import com.handscape.sdk.inf.IHSKeyBeanManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 映射管理器
 */
public class HSKeyBeanManagerImpl implements IHSKeyBeanManager {


    private HashMap<Integer, List<HSBaseKeyBean>> keyMap = new HashMap<>();

    @Override
    public HashMap<Integer, List<HSBaseKeyBean>> getDefineKeyMap() {
        return keyMap;
    }

    @Override
    public void addKeyMap(Integer keycode, HSBaseKeyBean point) {
        List<HSBaseKeyBean> keyBeans = keyMap.get(keycode);
        if (keyBeans == null) {
            keyBeans = new ArrayList<>();
        }
        keyBeans.clear();
        keyBeans.add(point);
        Log.v("xuyeCmd","add");
        keyMap.put(keycode, keyBeans);
    }

    @Override
    public void addAllKeyMap(Integer keycode, List<HSBaseKeyBean> point) {
        keyMap.put(keycode, point);
    }

    @Override
    public void removeKeyMap(int keycode, int index) {
        try {
            keyMap.get(keycode).remove(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearKeyMap() {
        keyMap.clear();
    }

    @Override
    public HSBaseKeyBean getBean(int pId) {
        if (keyMap == null) {
            return null;
        }
        for (Integer keycode : keyMap.keySet()) {
            if (keyMap.get(keycode) != null) {
                for (HSBaseKeyBean bean : keyMap.get(keycode)) {
                    if (bean != null && bean.getHsKeyData() != null && bean.getHsKeyData().getKeyPointId() == pId) {
                        return bean;
                    }
                }
            }
        }
        return null;
    }
}
