#include <stdio.h>
#include <unistd.h>
#include <linux/uinput.h>
#include <linux/input.h>
#include <linux/types.h>
#include "checkdev.c"

void checkdev();

static int lastcheck=0;

void *respondCheck(void *arg) {
    char checkdata[1] = {-1};
    while (1){
        int checkFd = open(checkPath, O_RDONLY | O_NONBLOCK);
        if (checkFd > 0) {
            int readid = read(checkFd, checkdata, sizeof(checkdata));
            if (readid > 0) {
                int checknumber = char2number(checkdata[0]);
                if (checknumber == 1&&lastcheck!=checknumber) {
                    lastcheck=checknumber;
                    //检查当前服务是否存活
                    printf("\ncheck");
                    if(servicestatus==0){
                        send_service_failed();
                    }else{
                        send_service_success();
                    }
                }
                lastcheck=checknumber;
            }
        }
        close(checkFd);
        usleep(1000*100);
    }
    return  NULL;
}

int main() {
    pid_t pid;
    pid = fork();
    if (pid == 0) {
        setsid();
        //获取进程id并且写入文件
        pid_t p = getpid();
        //将pid发送到APP端
        send_fock_process_success(p);
        //开启服务回应状态线程
        pthread_t id;
        pthread_create(&id, NULL, respondCheck, NULL);
        //检查驱动
        checkdev();
        sleep(3);
    } else if (pid > 0) {
    } else {
        printf("fork failed\n");
    }
    return 0;
}
