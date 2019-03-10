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
            printf("\ncheck mmap failed");
            sleep(5);
            respondCheck(NULL);
            return NULL;
        }
        printf("\ncheck mmap success");
        while (1){
            if(exitflag==0){
                printf("\nexit");
                break;
            }
            int asyn=msync(p, 100, MS_ASYNC);
            if(asyn==-1){
                printf("\nmasync failed");
                //失败
                break;
            }
            int checknumber=char2number(p[0]);
            //退出进程的消息
            if(checknumber==EXIT_CODE){
                printf("\nexit break");
                exitflag=0;
                break;
            }
            printf("\ncheck data lset=%d  check=%d",lastcheck,checknumber);
            if (lastcheck!=checknumber) {
                lastcheck=checknumber;
                //检查当前服务是否存活
                if(checknumber == CHECK_STATUS){
                    printf("\ncheck status");
                    if(servicestatus==0){
                        send_service_failed();
                    }else{
                        send_service_success();
                    }
                }
                //检查当前底层的版本号码
                if(checknumber==CHECK_VERSION){
                    send_native_version(VERSION);
                }
            }
            lastcheck=checknumber;
            sleep(3);
        }
        if(exitflag==0){
            exit(0);
        }else{
            sleep(2);
            respondCheck(NULL);
        }
    }else{
        //打开失败，重新打开
        printf("\ncheck open failed");
        //获取不到文件,等待10秒，重新读取
        sleep(10);
        respondCheck(NULL);
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
