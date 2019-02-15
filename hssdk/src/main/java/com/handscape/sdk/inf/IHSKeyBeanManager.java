package com.handscape.sdk.inf;

import java.util.HashMap;
import java.util.List;

import com.handscape.sdk.bean.HSBaseKeyBean;

//蓝牙触摸指令的接收、保存、管理
//按键映射的管理
public interface IHSKeyBeanManager {


    //获取已经定义的按键映射
    HashMap<Integer, List<HSBaseKeyBean>> getDefineKeyMap();

    //增加按键映射
    void addKeyMap(Integer keycode, HSBaseKeyBean point);

    //增加按键映射
    void addAllKeyMap(Integer keycode, List<HSBaseKeyBean> point);

    //移除按键
    void removeKeyMap(int keycode, int index);

    //清空按键映射
    void clearKeyMap();

    HSBaseKeyBean getBean(int pId);

}
