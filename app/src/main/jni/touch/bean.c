
#include <stdlib.h>
#include "consts.h"

/**
 * 触摸指令
 */
struct touch_action {
    //主操作
    int action;
    //坐标X
    int x;
    //坐标y
    int y;
};


struct touch_action init(const char *buf) {
    struct touch_action action;
    action.action = char2number(buf[0]);
    int a = char2number(buf[1]);
    int b = char2number(buf[2]);
    int c = char2number(buf[3]);
    int d = char2number(buf[4]);
    action.x = a * 1000 + b * 100 + c * 10 + d;
    a = char2number(buf[5]);
    b = char2number(buf[6]);
    c = char2number(buf[7]);
    d = char2number(buf[8]);
    action.y = a * 1000 + b * 100 + c * 10 + d;
    return action;
};

int char2position(char aa, char bb, char cc, char dd) {
    int a = char2number(aa);
    int b = char2number(bb);
    int c = char2number(cc);
    int d = char2number(dd);
    return a * 1000 + b * 100 + c * 10 + d;
}

int equal(struct touch_action a, struct touch_action b) {
    if (a.action != b.action || a.x != b.x || a.y != b.y) {
        return 0;
    }
    return 1;
}

void copy(struct touch_action a, struct touch_action b) {
    b.action = a.action;
    b.x = a.x;
    b.y = a.y;
}

