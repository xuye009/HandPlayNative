//
// Created by xuye on 2019/2/27.
//

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
#include "sendmsgtoactivity.c"
#include "consts.h"
#include "readcmd.c"


void start(char *devPath, char *slot);

struct input_event readEvent;

char devChar[20] = {-1};
char devCharcmp[20] = {-1};
char slotChar[1] = {-1};
char slotCharcmp[1] = {-1};

//检查驱动
void checkdev() {
    //首先检查是否已经检测过驱动
    int dev = open(devPath, O_RDONLY | O_NONBLOCK);
    if (dev > 0) {
        //打开文件
        read(dev, devChar, sizeof(devChar));
        close(dev);
    }
    int slot = open(slotPath, O_RDONLY | O_NONBLOCK);
    if (slot > 0) {
        //打开文件
        read(slot, slotChar, sizeof(slotChar));
        close(slot);
    }
    //进入驱动判断逻辑
    if (strcmp(devCharcmp, devChar) == 0) {
        printf("\nstart run");
        int wds[11] = {-1};
        struct pollfd *mufds;
        mufds = calloc(11, sizeof(mufds[0]));
        for (int i = 0; i < 11; i++) {
            mufds[i].fd = open(devpathList[i], O_RDONLY | O_NONBLOCK);
            mufds[i].events = POLLIN;
            wds[i] = inotify_add_watch(mufds[i].fd, devpathList[i], IN_DELETE | IN_CREATE);
        }
        int checksingle = 0;
        int slota = 0;
        int slotb = 0;
        int path = 0;
        send_need_singletouch_msg();
        while (1) {
            int pret = poll(mufds, 11, 10);
            if (pret > 0) {
                for (int i = 0; i < 11; i++) {
                    int res = read(mufds[i].fd, &readEvent, sizeof(readEvent));
                    if (res > 0) {
                        //a协议
                        if (readEvent.code == BTN_TOUCH) {
                            if (readEvent.value == 0) {
                                //抬起
                                slota = 1;
                                checksingle++;
                                if (checksingle == 1) {
                                    send_need_multtouch_msg();
                                }
                            }
                        }
                        if (readEvent.code == ABS_MT_TRACKING_ID) {
                            //b协议
                            if (readEvent.value == -1) {
                                //抬起
                                slotb = 1;
                                checksingle++;
                                if (checksingle == 1) {
                                    send_need_multtouch_msg();
                                }
                            }
                        }
                        if (readEvent.code == ABS_MT_SLOT) {
                            //b协议
                            slotb = 1;
                        }
                        if (readEvent.code == SYN_REPORT) {
                            path = i;
                        }
                    }
                }
            }
            if (checksingle >= 2) {
                send_event_path(path);
                switch (path) {
                    case 0:
                        strcpy(devChar,"/dev/input/event0");
                        break;
                    case 1:
                        strcpy(devChar,"/dev/input/event1");
                        break;
                    case 2:
                        strcpy(devChar,"/dev/input/event2");
                        break;
                    case 3:
                        strcpy(devChar,"/dev/input/event3");
                        break;
                    case 4:
                        strcpy(devChar,"/dev/input/event4");
                        break;
                    case 5:
                        strcpy(devChar,"/dev/input/event5");
                        break;
                    case 6:
                        strcpy(devChar,"/dev/input/event6");
                        break;
                    case 7:
                        strcpy(devChar,"/dev/input/event7");
                        break;
                    case 8:
                        strcpy(devChar,"/dev/input/event8");
                        break;
                    case 9:
                        strcpy(devChar,"/dev/input/event9");
                        break;
                    case 10:
                        strcpy(devChar,"/dev/input/event10");
                        break;
                }
                if (slotb == 1) {
                    send_slot_b();
                    strcpy(slotChar,"b");
                } else {
                    if (slota == 1) {
                        send_slot_a();
                        strcpy(slotChar,"a");
                    }
                }
                break;
            }
        }
        for (int i = 0; i < 11; i++) {
            inotify_rm_watch(mufds[i].fd, wds[i]);
        }
    }
    //发送激活成功逻辑
    send_actionsuccess_msg();
    //开始启动读取驱动和发送驱动位置信息逻辑
    start(devChar, slotChar);
}
