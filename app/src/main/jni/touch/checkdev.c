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


void start(char *devPath,char *slot);
struct input_event readEvent;

char devChar[20]={-1};
char devCharcmp[20]={-1};
char slotChar[1]={-1};
char slotCharcmp[1]={-1};

//检查驱动
void checkdev() {
    //首先检查是否已经检测过驱动
    int dev=open(devPath,O_RDONLY | O_NONBLOCK);
    if(dev>0){
        //打开文件
        read(dev,devChar, sizeof(devChar));
        printf("\n%s\n",devChar);
        close(dev);
    }

    int slot=open(slotPath,O_RDONLY | O_NONBLOCK);
    if(slot>0){
        //打开文件
        read(slot,slotChar, sizeof(slotChar));
        printf("%s\n",slotChar);
        close(slot);
    }

    if(strcmp(devCharcmp,devChar)==0){
        int wds[11]={-1};
        struct pollfd *mufds;
        mufds = calloc(11, sizeof(mufds[0]));
        for (int i = 0; i < 11; i++) {
            mufds[i].fd = open(devpathList[i], O_RDONLY | O_NONBLOCK);
            mufds[i].events = POLLIN;
            wds[i]= inotify_add_watch(mufds[i].fd, devpathList[i], IN_DELETE | IN_CREATE);
        }
        printf("start check \n");
        int checksingle = 0;
        int slota = 0;
        int slotb = 0;
        int path=0;
        send_need_singletouch_msg();
        while (1) {
            int pret = poll(mufds, 11, 10);
            if (pret > 0) {
                for (int i = 0; i < 11; i++) {
                    int res = read(mufds[i].fd, &readEvent, sizeof(readEvent));
                    if (res > 0) {
                        //a协议
                        if (readEvent.code == BTN_TOUCH) {
                            if(readEvent.value==0){
                                //抬起
                                slota = 1;
                                checksingle++;
                                if (checksingle == 1) {
                                    send_need_multtouch_msg();
                                }
                            }
                        }
                        if(readEvent.code==ABS_MT_TRACKING_ID){
                            //b协议
                            if(readEvent.value==-1){
                                //抬起
                                slotb=1;
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
                            path=i;
                        }
                    }
                }
            }
            if (checksingle >= 2) {
                send_event_path(path);
                if(slotb==1){
                    send_slot_b();
                }else{
                    if(slota==1){
                        send_slot_a();
                    }
                }
                break;
            }
        }
        for (int i = 0; i < 11; i++) {
            inotify_rm_watch(mufds[i].fd, wds[i]);
        }
    }
    send_actionsuccess_msg();
    start(devChar,slotChar);
}
