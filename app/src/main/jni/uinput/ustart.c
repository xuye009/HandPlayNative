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
#include <linux/uinput.h>
#include <linux/input.h>
#include <err.h>
#include <errno.h>
#include <unistd.h>


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

static int fd = 0;

int main() {
    if (fd <= 0) {
        fd = open("/dev/input/event5", O_WRONLY | O_NONBLOCK);
    }
    struct input_event action[5];
    action[0] = getevent(EV_ABS, ABS_MT_PRESSURE, 0x01);
    action[1] = getevent(EV_ABS, ABS_MT_TOUCH_MAJOR, 0x04);
    action[2] = getevent(EV_ABS, ABS_MT_POSITION_X, 800);
    action[3] = getevent(EV_ABS, ABS_MT_POSITION_Y, 1000);
    action[4] = getevent(EV_SYN, SYN_MT_REPORT, 0);
    write(fd, &action, sizeof(action));
    int i = write(fd, &action, sizeof(action));
    printf("\nwrite=%d\n", i);
    return 0;
}
