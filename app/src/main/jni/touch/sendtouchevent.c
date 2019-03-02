#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <dirent.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <sys/inotify.h>
#include <sys/limits.h>
#include <sys/poll.h>
#include <linux/input.h>
#include <err.h>
#include <errno.h>
#include <unistd.h>
#include <pthread.h>
#include <time.h>
#include <sys/time.h>
#include "bean.c"

int write_event(int fd, __u16 type, __u16 code, __s32 value);

struct input_event getevent(__u16 type, __u16 code, __s32 value);

void touch_a(int fd, int id, int isSingle, int action, int x, int y);

void click_a(int fd, int id, int isSingle, int x, int y);

void click_b(int fd, int id, int isSingle, int x, int y);


struct input_event inputEvent;

static int fd = -1;

void sendtouch(char *devpath,char *slot,int action, int id, int isSingle, int x, int y) {
    if (fd == -1) {
        fd = open(devpath, O_WRONLY | O_NONBLOCK);
    }
    printf("\nclick");
    if(strcmp(slot,"a")==0){
        //a协议
        click_a(fd, id, isSingle, x, y);
    }
    if(strcmp(slot,"b")==0){
        //b协议
        click_b(fd,id,isSingle,x,y);
    }
}


/**
 * 单击协议A
 * @param fd
 * @param id
 * @param isSingle
 * @param action
 * @param x
 * @param y
 */
void click_a(int fd, int id, int isSingle, int x, int y) {

    if (isSingle == 0) {
        //代表当前没有手指按下，发送按下和抬起指令
        struct input_event action[8];
        action[0] = getevent(EV_ABS, ABS_MT_PRESSURE, 0x01);
        action[1] = getevent(EV_ABS, ABS_MT_TOUCH_MAJOR, 4);
        //坐标
        action[2] = getevent(EV_ABS, ABS_MT_POSITION_X, x);
        action[3] = getevent(EV_ABS, ABS_MT_POSITION_Y, y);
        //按下
        action[4] = getevent(EV_KEY, BTN_TOUCH, 1);
        action[5] = getevent(EV_SYN, SYN_REPORT, 0);
        //抬起
        action[6] = getevent(EV_KEY, BTN_TOUCH, 0);
        action[7] = getevent(EV_SYN, SYN_REPORT, 0);
        write(fd, &action, sizeof(action));
    } else {
        //不打断屏幕
        struct input_event action[5];
        action[0] = getevent(EV_ABS, ABS_MT_PRESSURE, 0x01);
        action[1] = getevent(EV_ABS, ABS_MT_TOUCH_MAJOR, 0x04);
        action[2] = getevent(EV_ABS, ABS_MT_POSITION_X, x);
        action[3] = getevent(EV_ABS, ABS_MT_POSITION_Y, y);
        action[4] = getevent(EV_SYN, SYN_MT_REPORT, 0);
        write(fd, &action, sizeof(action));
    }
}

/**
 * 单击协议B
 * @param fd
 * @param id
 * @param isSingle
 * @param action
 * @param x
 * @param y
 */
void click_b(int fd, int id, int isSingle, int x, int y) {

    if(isSingle==0){
        //代表当前没有手指按下，发送按下和抬起指令
        struct input_event action[12];
        action[0] = getevent(EV_ABS, ABS_MT_SLOT, 10);
        action[1] = getevent(EV_ABS, ABS_MT_TRACKING_ID, 0);
        //压力值和面积
        action[2] = getevent(EV_ABS, ABS_MT_PRESSURE, 0x01);
        action[3] = getevent(EV_ABS, ABS_MT_TOUCH_MAJOR, 4);
        //坐标
        action[4] = getevent(EV_ABS, ABS_MT_POSITION_X, x);
        action[5] = getevent(EV_ABS, ABS_MT_POSITION_Y, y);
        //按下
        action[6] = getevent(EV_KEY, BTN_TOUCH, 1);
        action[7] = getevent(EV_SYN, SYN_REPORT, 0);
        //抬起
        action[8] = getevent(EV_ABS, ABS_MT_SLOT, 10);
        action[9] = getevent(EV_ABS, ABS_MT_TRACKING_ID, -1);
        action[10] = getevent(EV_KEY, BTN_TOUCH, 0);
        action[11] = getevent(EV_SYN, SYN_REPORT, 0);
        write(fd, &action, sizeof(action));
    }else{
        //不打断屏幕
        struct input_event action[10];
        action[0] = getevent(EV_ABS, ABS_MT_SLOT, 3);
        action[1] = getevent(EV_ABS, ABS_MT_TRACKING_ID, 2);
        action[2] = getevent(EV_ABS, ABS_MT_PRESSURE, 0x01);
        action[3] = getevent(EV_ABS, ABS_MT_TOUCH_MAJOR, 0x04);
        action[4] = getevent(EV_ABS, ABS_MT_POSITION_X, x);
        action[5] = getevent(EV_ABS, ABS_MT_POSITION_Y, y);
        action[6] = getevent(EV_SYN, SYN_REPORT, 0);
        action[7] = getevent(EV_ABS, ABS_MT_SLOT, 3);
        action[8] = getevent(EV_ABS, ABS_MT_TRACKING_ID, -1);
        action[9] = getevent(EV_SYN, SYN_REPORT, 0);
        write(fd, &action, sizeof(action));
    }
}


/**
 * 触摸A协议
 * @param fd
 * @param isSingle
 * @param x
 * @param y
 */
void touch_a(int fd, int id, int isSingle, int action, int x, int y) {

    if (isSingle == 0) {
        //打断屏幕
        if (action == 0) {
            //按下
            struct input_event downaction[8];
            downaction[0] = getevent(EV_ABS, ABS_MT_PRESSURE, 0x01);
            downaction[1] = getevent(EV_ABS, ABS_MT_POSITION_X, x);
            downaction[2] = getevent(EV_ABS, ABS_MT_POSITION_Y, y);
            downaction[3] = getevent(EV_ABS, ABS_MT_TRACKING_ID, id);
            downaction[4] = getevent(EV_SYN, SYN_MT_REPORT, 0);
            downaction[5] = getevent(EV_KEY, BTN_TOUCH, 1);
            downaction[6] = getevent(EV_SYN, SYN_MT_REPORT, 0);
            downaction[7] = getevent(EV_SYN, SYN_REPORT, 0);
//            event[8] = getevent(fd, EV_KEY, BTN_TOUCH, 0);
//            event[9] = getevent(fd, EV_SYN, SYN_MT_REPORT, 0);
//            event[10] = getevent(fd, EV_SYN, SYN_REPORT, 0);
            int ret = write(fd, &downaction, sizeof(downaction));

        }
        if (action == 1) {
            //抬起
            struct input_event upaction[3];
            upaction[0] = getevent(EV_ABS, ABS_MT_TRACKING_ID, id);
            upaction[1] = getevent(EV_KEY, BTN_TOUCH, 0);
            upaction[2] = getevent(EV_SYN, SYN_REPORT, 0);
            int ret = write(fd, &upaction, sizeof(upaction));

        }
        if (action == 2) {
            //移动
            struct input_event moveaction[5];
            moveaction[0] = getevent(EV_ABS, ABS_MT_PRESSURE, 0x01);
            moveaction[1] = getevent(EV_ABS, ABS_MT_POSITION_X, x);
            moveaction[2] = getevent(EV_ABS, ABS_MT_POSITION_Y, y);
            moveaction[3] = getevent(EV_ABS, ABS_MT_TRACKING_ID, id);
            moveaction[4] = getevent(EV_SYN, SYN_MT_REPORT, 0);
            int ret = write(fd, &moveaction, sizeof(moveaction));
        }


    } else {
        //不打断屏幕
        struct input_event event[5];
        event[0] = getevent(EV_ABS, ABS_MT_PRESSURE, 0x01);
        event[1] = getevent(EV_ABS, ABS_MT_POSITION_X, x);
        event[2] = getevent(EV_ABS, ABS_MT_POSITION_Y, y);
        event[3] = getevent(EV_ABS, ABS_MT_TRACKING_ID, id);
        event[4] = getevent(EV_SYN, SYN_MT_REPORT, 0);
        int ret = write(fd, &event, sizeof(event));
    }
}

struct input_event getevent(__u16 type, __u16 code, __s32 value) {
    struct input_event ev;
    ev.type = type;
    ev.code = code;
    ev.value = value;
    struct timeval us;
    gettimeofday(&us, NULL);
    ev.time = us;
    return ev;
}
