#include <stdio.h>
#include <unistd.h>
#include <linux/uinput.h>
#include <linux/input.h>
#include <linux/types.h>
#include "readcmd.c"

void start();


int main() {
    pid_t pid;
    printf("start progress\n");
    pid = fork();
    if (pid == 0) {
        setsid();
        //获取进程id并且写入文件
        pid_t p=getpid();
        printf("fork process success pid=%d\n",p);
        int pidFd=open(pidfile,O_RDWR,O_CREAT);
        char a[10]={0};
        sprintf(a,"%d",p);
        printf("\nwrite pid=%s\n",a);
        int wrid= write(pidFd,a, sizeof(a));
        if(wrid>0){
            char buf[10] = {-1};
            wrid= read(pidFd,buf, sizeof(buf));
            if(wrid>0){
                printf("\nread  pid=%s\n",buf);
            }
        }
        close(pidFd);
        //进入死循环
        start();
    } else if (pid > 0) {
    } else {
        printf("fork failed\n");
    }
    return 0;
}