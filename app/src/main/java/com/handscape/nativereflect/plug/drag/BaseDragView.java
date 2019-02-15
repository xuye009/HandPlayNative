package com.handscape.nativereflect.plug.drag;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.handscape.nativereflect.plug.PlugManager;

//可拖动的基础View
public abstract class BaseDragView extends FrameLayout {

    protected PlugManager plugManager;
    protected int screenWidth, screenHeight;
    private float startX, startY, nowX, nowY, spaceX, spaceY;
    private int moveSlop = 24;

    protected LayoutInflater mLayoutinflater;
    protected WindowManager.LayoutParams mBarLayoutParams;

    //是否应该删除标记
    private boolean shouldDelete = false;


    public BaseDragView(@NonNull Context context,PlugManager plugManager) {
        super(context);
        this.plugManager=plugManager;
        mLayoutinflater = LayoutInflater.from(context);
        init();
    }

    private void init() {
        moveSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
//        moveSlop = 0;
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        mBarLayoutParams=getLayoutParams();
        addView(getContentView());
    }

    public abstract WindowManager.LayoutParams getLayoutParams();
    //按下
    protected abstract void onDown(MotionEvent event);

    //代表在位置更新后，拖动结束后或者设置键值后
    protected abstract void onUp(float cx, float cy);

    //代表在拖动状态
    protected abstract void onMove(MotionEvent event);

    //是否点击
    protected abstract boolean isclick();

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        nowX = event.getRawX();
        nowY = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                startTime = SystemClock.currentThreadTimeMillis();
                startX = event.getRawX();
                startY = event.getRawY();
                spaceX = mBarLayoutParams.x - startX;
                spaceY = mBarLayoutParams.y - startY;
                isMove = false;
                return false;
            case MotionEvent.ACTION_MOVE:
                float moveX = startX - nowX;
                float moveY = startY - nowY;
                if (Math.abs(moveX) >= moveSlop || Math.abs(moveY) >= moveSlop) {
                    return true;
                }
                return false;

            case MotionEvent.ACTION_UP:

                break;
        }

        return false;
    }

    protected abstract View getContentView();

    boolean isMove = false;

    long startTime = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {//拖动事件的实现
        nowX = event.getRawX();
        nowY = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://按下
                moveSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                startTime = SystemClock.currentThreadTimeMillis();
                isMove = false;
                onDown(event);
                return true;
            case MotionEvent.ACTION_MOVE://移动
                float moveX = startX - nowX;
                float moveY = startY - nowY;
                if (SystemClock.currentThreadTimeMillis() - startTime > 25) {
                    moveSlop = 0;
                }
                if (Math.abs(moveX) >= moveSlop || Math.abs(moveY) >= moveSlop) {
                    //判断如果移动，则更新位置
                    mBarLayoutParams.x = (int) (spaceX + nowX);
                    mBarLayoutParams.y = (int) (spaceY + nowY);
                    plugManager.updateview(this, mBarLayoutParams);
                    onMove(event);
                    isMove = true;
                }
                break;

            case MotionEvent.ACTION_UP://抬起
                if (getY() <= 0) {
                    mBarLayoutParams.x=0;
                }
                if (getX() <= 0) {
                    mBarLayoutParams.y=0;
                }
                if (getX() + getWidth() > screenWidth) {
                    mBarLayoutParams.x=screenWidth - getWidth();
                }
                if (getY() + getHeight() > screenHeight) {
                    mBarLayoutParams.y=screenHeight - getHeight();
                }
                startX = 0;
                startY = 0;
                post(new Runnable() {
                    @Override
                    public void run() {
                        onUp(mBarLayoutParams.x + getWidth() / 2, mBarLayoutParams.y + getHeight());
                    }
                });
                if (isclick() && !isMove && SystemClock.currentThreadTimeMillis() - startTime < 25) {
                    callOnClick();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setShouldDelete(boolean shouldDelete) {
        this.shouldDelete = shouldDelete;
    }

    public boolean isShouldDelete() {
        return shouldDelete;
    }


}
