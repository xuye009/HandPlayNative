#include <stdio.h>
#include <unistd.h>
#include <linux/uinput.h>
#include <linux/input.h>
#include <linux/types.h>
#include "checkdev.c"

void checkdev();


int main() {
    pid_t pid;
    pid = fork();
    if (pid == 0) {
        setsid();
        //获取进程id并且写入文件
        pid_t p = getpid();
        //将pid发送到APP端
        send_fock_process_success(p);
        //检查驱动
        checkdev();
    } else if (pid > 0) {
    } else {
        printf("fork failed\n");
    }
    return 0;
}
