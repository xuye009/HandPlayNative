//
// Created by xuye on 2019/2/27.
//

#ifndef NATIVEHANDYPLAY_CONSTS_H
#define NATIVEHANDYPLAY_CONSTS_H

#define char2number(x) (x-'0');

//static const char *pidfile="/data/local/tmp/pid.txt";
//static const char *eventfile="/data/local/tmp/event.txt";
static const char *devpathList[] = {"/dev/input/event0", "/dev/input/event1", "/dev/input/event2",
                                    "/dev/input/event3",
                                    "/dev/input/event4", "/dev/input/event5", "/dev/input/event6",
                                    "/dev/input/event7", "/dev/input/event8", "/dev/input/event9",
                                    "/dev/input/event10"};
static const char *cmdPath = "/sdcard/Android/data/com.handscape.nativereflect/cache/touch.txt";
static const char *devPath = "/sdcard/Android/data/com.handscape.nativereflect/cache/event.txt";
static const char *slotPath = "/sdcard/Android/data/com.handscape.nativereflect/cache/slot.txt";
static const char *pidPath = "/sdcard/Android/data/com.handscape.nativereflect/cache/pid.txt";
static const char *checkPath = "/sdcard/Android/data/com.handscape.nativereflect/cache/service.txt";

//当前底层服务的版本号
const static volatile int VERSION=1;

//定义和上层APP通信的数据
//退出进程
const static  volatile int EXIT_CODE=9;
//获取当前底层服务的版本
const static volatile int CHECK_STATUS=1;
//获取当前运行状态
const static volatile int CHECK_VERSION=2;




//标识状态
static volatile int servicestatus = 0;
//是否退出服务的标识,==0时退出服务
static volatile int exitflag = 1;


#endif //NATIVEHANDYPLAY_CONSTS_H
