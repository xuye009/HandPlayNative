
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

void sendtouch(char *devpath,char *slot,int action, int id, int isSingle, int x, int y);

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

void checkProgress() {
    int pidFd = open(pidfile, O_RDONLY, O_CREAT);
    if (pidFd > 0) {
        char rpiddata[10] = {-1};
        int rpid = read(pidFd, rpiddata, sizeof(rpiddata));
        if (rpid > 0) {
            char mypiddata[10] = {-1};
            pid_t mypid = getpid();
            sprintf(mypiddata, "%d", mypid);
            if (strcmp(rpiddata, mypiddata) == 0) {
                //是本进程
            } else {
                printf("\nprogress exit\n");
                exit(0);
            }
        }
    } else {
        printf("\nread pid error\n");
//        exit(0);
    }
    close(pidFd);
}

char *devpath;
/**
 * 读取触摸驱动
 * @return
 */
void *readDriver(void *arg) {
    printf("readDriver\n");
    ufds = calloc(1, sizeof(ufds[0]));
    ufds[0].fd = open(devpath, O_RDONLY | O_NONBLOCK);
    ufds[0].events = POLLIN;
    inotify_add_watch(ufds[0].fd, devpath, IN_DELETE | IN_CREATE);
    nfds = 1;
    printf("readDriver while\n");
    while (1) {
//        checkProgress();
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
        } else {
            //触摸屏没有触摸事件
//            if(actionNumber>0){
//                actionNumber=0;
//            }
        }
        usleep(1 * 1000);
    }
    close(ufds[0].fd);
    return NULL;
}

static int lastAction = 4;

/**
 * 读取指令文件并发送给驱动
 */
void sendTouchCmd(char *dev,char *slot) {
    char buf[100] = {-1};
    int fd = open("/sdcard/Android/data/com.handscape.nativereflect/cache/touch.txt", O_RDONLY, 00700);
    if (fd > 0) {
        printf("\nfd>0\n");
        char *p = mmap(NULL, sizeof(buf), PROT_READ, MAP_SHARED, fd, 0);
        printf("\nmmap end=%s",p);
        close(fd);
        if (p == NULL || p == (void *) -1) {
            printf("\nmmap failed");
            sleep(10);
            sendTouchCmd(dev,slot);
            return;
        }
        printf("\nmmap success");
        close(fd);
        while (1) {
            checkProgress();
            //同步文件内容
            msync(p, 100, MS_SYNC);
//            int mainAction = char2number(p[0]);
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
                //x
                int x = char2position(p[position + 5], p[position + 6], p[position + 7],
                                      p[position + 8]);
                //y
                int y = char2position(p[position + 10], p[position + 11], p[position + 12],
                                      p[position + 13]);

                if (mAction == 0 || mAction == 2) {
                    printf("\ninfo action=%d x=%d y=%d ", mAction, x, y);
                    //处于按下或者按住状态
                    if (downNumber - upNumber > 0) {
                        //按下状态
                        sendtouch(dev,slot,mAction, id, 1, x, y);
                    } else {
                        sendtouch(dev,slot,mAction, id, 0, x, y);
                    }
                }
            }
            usleep(1 * 1000 * 25);
        }
    } else {
        close(fd);
        printf("\nopen failed");
        //获取不到文件,等待10秒，重新读取
        sleep(10);
        sendTouchCmd(dev,slot);
    }
}

void start(char *dev,char *slot) {
    devpath=dev;
    printf("\ndevpath=%s slot=%s \n",dev,slot);
    pthread_t id,id2;
    int ret=pthread_create(&id, NULL, readDriver, NULL);
    if(ret){
        printf("create pthread fail\n");
    }
    printf("create pthread success\n");
    sendTouchCmd(dev,slot);
    sleep(2);
}



