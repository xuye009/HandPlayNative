//
// Created by xuye on 2019/2/27.
//
#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include <stdlib.h>
#include <dirent.h>
#include <sys/ioctl.h>
#include <sys/inotify.h>
#include <sys/limits.h>
#include <sys/poll.h>
#include <linux/input.h>
#include <err.h>
#include <pthread.h>
#include <malloc.h>
#include <sys/mman.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <string.h>
#define char2number(x) (x-'0');
//发送需要单指触摸的消息
void send_need_singletouch_msg(){
    system("am start -n com.handscape.nativereflect/com.handscape.nativereflect.activity.DeviceActivationActivity --ei touch 1 ");
}

//发送需要双手触摸的消息
void send_need_multtouch_msg(){
    system("am start -n com.handscape.nativereflect/com.handscape.nativereflect.activity.DeviceActivationActivity --ei touch 2 ");
}

//发送激活成功的消息
void send_actionsuccess_msg(){
    system("am start -n com.handscape.nativereflect/com.handscape.nativereflect.activity.DeviceActivationActivity  --ei action 1");
}

//发送激活失败的消息
void send_actionfailed_msg(){
    system("am start -n com.handscape.nativereflect/com.handscape.nativereflect.activity.DeviceActivationActivity  --ei action 0");
}

//代表当前设备是协议A
void send_slot_a(){
    system("am start -n com.handscape.nativereflect/com.handscape.nativereflect.activity.DeviceActivationActivity  --ei slot 0");
}

//代表当前设备是协议B
void send_slot_b(){
    system("am start -n com.handscape.nativereflect/com.handscape.nativereflect.activity.DeviceActivationActivity  --ei slot 1");
}

//发送驱动位置
void send_event_path(int i){
    if(i==0){
        system("am start -n com.handscape.nativereflect/com.handscape.nativereflect.activity.DeviceActivationActivity  --ei dev 0");
    }
    if(i==1){
        system("am start -n com.handscape.nativereflect/com.handscape.nativereflect.activity.DeviceActivationActivity  --ei dev 1");
    }
    if(i==2){
        system("am start -n com.handscape.nativereflect/com.handscape.nativereflect.activity.DeviceActivationActivity  --ei dev 2");
    }
    if(i==3){
        system("am start -n com.handscape.nativereflect/com.handscape.nativereflect.activity.DeviceActivationActivity  --ei dev 3");
    }
    if(i==4){
        system("am start -n com.handscape.nativereflect/com.handscape.nativereflect.activity.DeviceActivationActivity  --ei dev 4");
    }
    if(i==5){
        system("am start -n com.handscape.nativereflect/com.handscape.nativereflect.activity.DeviceActivationActivity  --ei dev 5");
    }
    if(i==6){
        system("am start -n com.handscape.nativereflect/com.handscape.nativereflect.activity.DeviceActivationActivity  --ei dev 6");
    }
    if(i==7){
        system("am start -n com.handscape.nativereflect/com.handscape.nativereflect.activity.DeviceActivationActivity  --ei dev 7");
    }
    if(i==8){
        system("am start -n com.handscape.nativereflect/com.handscape.nativereflect.activity.DeviceActivationActivity  --ei dev 8");
    }
    if(i==9){
        system("am start -n com.handscape.nativereflect/com.handscape.nativereflect.activity.DeviceActivationActivity  --ei dev 9");
    }
    if(i==10){
        system("am start -n com.handscape.nativereflect/com.handscape.nativereflect.activity.DeviceActivationActivity  --ei dev 10");
    }

}