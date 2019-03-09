#include <stdio.h>
#include <unistd.h>
#include <linux/uinput.h>
#include <linux/input.h>
#include <linux/types.h>
#include "checkdev.c"

void checkdev();

static int lastcheck=0;

//开启监听检查服务的线程
void *respondCheck(void *arg) {
    char checkdata[1] = {-1};
    int checkFd = open(checkPath, O_RDONLY | O_NONBLOCK);
    if(checkFd>0){
        //文件打开成功
        char *p = mmap(NULL, sizeof(checkdata), PROT_READ, MAP_PRIVATE, checkFd, 0);
        close(checkFd);
        if (p == NULL || p == (void *) -1) {
            sleep(10);
            respondCheck();
            return;
        }
        printf("\ncheck mmap success");
        while (1){
            if(exitflag==0){
                break;
            }
            int asyn=msync(p, 100, MS_ASYNC);
            if(asyn==-1){
                //失败
                break;
            }
            int checknumber=char2number(checkdata[0]);
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
            usleep(1000*1000);
        }
        if(exitflag==0){
            exit(0);
        }
    }else{
        //打开失败，重新打开
        printf("\ncheck open failed");
        //获取不到文件,等待10秒，重新读取
        sleep(10);
        respondCheck();
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
