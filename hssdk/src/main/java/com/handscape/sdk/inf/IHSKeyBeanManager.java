package com.handscape.sdk.inf;

import java.util.HashMap;
import java.util.List;

import com.handscape.sdk.bean.HSBaseKeyBean;

//蓝牙触摸指令的接收、保存、管理
//按键映射的管理
public interface IHSKeyBeanManager {

    Object getConfigObj();

    void setConfigObj(Object obj);

    //获取未定义的按键映射
    HashMap<Integer, HSBaseKeyBean> getUnDefineMap();

    //添加未定义的按键
    void addUndefineMap(Integer id, HSBaseKeyBean point);

    //移除未定义的按键
    void removeUndefineKeyMap(Integer id);

    //清空未定义的按键
    void clearUndefineKeyMap();

    //获取已经定义的按键映射
    HashMap<Integer, List<HSBaseKeyBean>> getDefineKeyMap();

    //增加按键映射
    void addDefineKeyMap(Integer id, HSBaseKeyBean point);

    //增加按键映射
    void addDefineKeyMap(Integer id, List<HSBaseKeyBean> point);

    //移除按键
    void removeDefineKeyMap(int id, int index);

    //清空按键映射
    void clearDefineKeyMap();

    //是否在按键配置界面
    boolean isInConfigMode();

    void removeBean(int id, int index);

    int getKeyCode(float touchX, float touchY);

    HSBaseKeyBean getBean(int pId);
     void initPid(int pid);
    /**
     * 根据ID获取按键的Keycode
     *
     * @param id
     * @return
     */
    int getKeyCode(int oldKeyCode, int id);

    /**
     * 模拟按键的入口
     *
     * @param touchAction
     * @param pointerID
     * @param eventX
     * @param eventY
     * @throws Exception
     */
    void addCommand(int touchAction, int pointerID, float eventX, float eventY);

    /**
     * 获取APP类型
     */
    int getAppType();

    /**
     * 获取包名
     * @return
     */
    String getPkgNAme();


    boolean issynchronize(int code);


}
