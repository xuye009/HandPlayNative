
//读取触摸指令
#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include <dirent.h>
#include <sys/ioctl.h>
#include <sys/inotify.h>
#include <sys/limits.h>
#include <sys/poll.h>
#include <linux/input.h>
#include <err.h>
#include <pthread.h>
#include <malloc.h>
#include <sys/mman.h>
#include <fcntl.h>
#include <sys/stat.h>

#include "sendtouchevent.c"
#include "sendmsgtoactivity.c"

void sendtouch(char *devpath, char *slot, int action, int id, int isSingle, int x, int y);

int char2position(char aa, char bb, char cc, char dd);

static struct touch_action oldaction;

struct touch_action init(const char *buf);

int equal(struct touch_action a, struct touch_action b);

void copy(struct touch_action a, struct touch_action b);

struct pollfd *ufds;
int nfds;
int i;

static int upArray[100] = {-1};

//存储触摸屏产生的id
static int mt_id_array[20] = {-1};

static volatile int downNumber = 0;
static volatile int upNumber = 0;
static volatile int flag = 1;

static volatile int devicedownNumber = 0;
struct input_event readEvent;

int checkProgress() {
    int rt = 0;
    int pidFd = open(pidPath, O_RDONLY | O_NONBLOCK);
    if (pidFd > 0) {
        char rpiddata[10] = {-1};
        int rpid = read(pidFd, rpiddata, sizeof(rpiddata));
        if (rpid > 0) {
            char mypiddata[10] = {-1};
            pid_t mypid = getpid();
            printf("\ngetpid=%d", mypid);
            sprintf(mypiddata, "%d", mypid);
            if (strcmp(rpiddata, mypiddata) == 0) {
                //是本进程
//                printf("\nprogress eql");
                rt = 1;
            } else {
//                printf("\nprogress not eql");
                rt = 0;
            }
        }
    } else {
        printf("\nread pid error");
        rt = 1;
    }
    close(pidFd);
    return rt;
}

char *devpath;

/**
 * 读取触摸驱动
 * @return
 */
void *readDriver(void *arg) {
    printf("\nreadDriver");
    ufds = calloc(1, sizeof(ufds[0]));
    ufds[0].fd = open(devpath, O_RDONLY | O_NONBLOCK);
    ufds[0].events = POLLIN;
    inotify_add_watch(ufds[0].fd, devpath, IN_DELETE | IN_CREATE);
    nfds = 1;
    while (1) {
        if(exitflag==0){
            break;
        }
        int pret = poll(ufds, nfds, 10);
        if (pret > 0) {
            //触摸屏有触摸事件
            int res = read(ufds[0].fd, &readEvent, sizeof(readEvent));
            if (readEvent.code == BTN_TOUCH) {
                if (readEvent.value == 1) {
                    //按下事件
                    downNumber++;
                    printf("down %d\n", downNumber);
                }
                if (readEvent.value == 0) {
                    //抬起事件
                    upNumber++;
                    printf("up %d\n", upNumber);
                }
            }
        }
        usleep(1 * 1000);
    }
    if(exitflag==0){
        close(ufds[0].fd);
        exit(0);
    }
    return NULL;
}

static int lastAction = 4;

struct clickAction {
    int action;
    int x;
    int y;
};

struct clickAction clickActionArray[100];

/**
 * 读取指令文件并发送给驱动
 */
void sendTouchCmd(char *dev, char *slot) {
    char buf[100] = {-1};
    int fd = open(cmdPath, O_RDONLY | O_NONBLOCK);
    if (fd > 0) {
        char *p = mmap(NULL, sizeof(buf), PROT_READ, MAP_PRIVATE, fd, 0);
        close(fd);
        if (p == NULL || p == (void *) -1) {
            sleep(10);
            sendTouchCmd(dev, slot);
            return;
        }
        printf("\nmmap success");
        while (1) {
            if(exitflag==0){
                break;
            }
            servicestatus=1;
            //同步文件内容
            int asyn=msync(p, 100, MS_ASYNC);
            if(asyn==-1){
                //失败
                break;
            }
            //数量
            int temp = char2number(p[2]);
            int count = char2number(p[3]);
            count = 10 * temp + count;
            for (int i = 0; i < count; i++) {//循环读取每一个手指当前的动作
                int position = 8 + i * 15;
                int t = char2number(p[position]);
                //id
                int id = char2number(p[position + 1]);
                id = id + t * 10;
                //当前id对应的action
                ////按下 0、移动 2、抬起 1
                int mAction = char2number(p[position + 3]);
                if (mAction == 0 || mAction == 2) {
                    mAction = 0;
                }
                //x
                int x = char2position(p[position + 5], p[position + 6], p[position + 7],
                                      p[position + 8]);
                //y
                int y = char2position(p[position + 10], p[position + 11], p[position + 12],
                                      p[position + 13]);

                if (x == 0 || y == 0) {
                    continue;
                }

                if (clickActionArray[id].action == mAction && clickActionArray[id].x == x &&
                    clickActionArray[id].y == y) {
                } else {
                    if (mAction == 0) {
                        printf("\ninfo action=%d x=%d y=%d ", mAction, x, y);
                        //屏幕处于按下或者按住状态
                        if (downNumber - upNumber > 0) {
                            //按下状态
                            sendtouch(dev, slot, mAction, id, 1, x, y);
                        } else {
                            sendtouch(dev, slot, mAction, id, 0, x, y);
                        }
                    }
                    clickActionArray[id].action = mAction;
                    clickActionArray[id].x = x;
                    clickActionArray[id].y = y;
                }
            }
            usleep(1 * 1000 * 25);
        }
        if(exitflag==0){
            servicestatus=0;
            exit(0);
        }else{
            printf("\nmmap failed");
            //获取不到文件,等待10秒，重新读取
            sleep(10);
            sendTouchCmd(dev, slot);
        }
    } else {
        close(fd);
        printf("\nopen failed");
        //获取不到文件,等待10秒，重新读取
        sleep(10);
        sendTouchCmd(dev, slot);
    }
}

//void sendCmd(char *dev, char *slot) {
//    char buf[100] = {-1};
//    while (1) {
//        if(exitflag==0){
//            break;
//        }
//        int fd = open(cmdPath, O_RDONLY | O_NONBLOCK);
//        if (fd <= 0) {
//            continue;
//        }
//        int readid = read(fd, buf, sizeof(buf));
////            int check = checkProgress();
////            if (check == 0) {
////                break;
////            }
//        if (readid <= 0) {
//            continue;
//        }
//        //数量
//        int temp = char2number(buf[2]);
//        int count = char2number(buf[3]);
//        count = 10 * temp + count;
//        for (int i = 0; i < count; i++) {//循环读取每一个手指当前的动作
//            int position = 8 + i * 15;
//            int t = char2number(buf[position]);
//            //id
//            int id = char2number(buf[position + 1]);
//            id = id + t * 10;
//            //当前id对应的action
//            ////按下 0、移动 2、抬起 1
//            int mAction = char2number(buf[position + 3]);
//            if (mAction == 0 || mAction == 2) {
//                mAction = 0;
//            }
//            //x
//            int x = char2position(buf[position + 5], buf[position + 6], buf[position + 7],
//                                  buf[position + 8]);
//            //y
//            int y = char2position(buf[position + 10], buf[position + 11], buf[position + 12],
//                                  buf[position + 13]);
//
//            if (x == 0 || y == 0) {
//                continue;
//            }
//
//            if (clickActionArray[id].action == mAction && clickActionArray[id].x == x &&
//                clickActionArray[id].y == y) {
//            } else {
//                if (mAction == 0) {
//                    printf("\ninfo action=%d x=%d y=%d ", mAction, x, y);
//                    //屏幕处于按下或者按住状态




//                        //按下状态
//                        sendtouch(dev, slot, mAction, id, 1, x, y);
//                    } else {
//                        sendtouch(dev, slot, mAction, id, 0, x, y);
//                    }
//                }
//                clickActionArray[id].action = mAction;
//                clickActionArray[id].x = x;
//                clickActionArray[id].y = y;
//            }
//        }
//        close(fd);
//        usleep(1 * 1000 * 25);
//    }
//    if(exitflag==0){
//       exit(0);
//    }
//}

void start(char *dev, char *slot) {
    devpath = dev;
    printf("\ndevpath=%s slot=%s \n", dev, slot);
    pthread_t id;
    //读取驱动，获取当前是否正在触摸
    pthread_create(&id, NULL, readDriver, NULL);
    sendTouchCmd(dev, slot);
//    sendCmd(dev,slot);
    sleep(5);
}



