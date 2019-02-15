package com.handscape.nativereflect.plug.drag;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.handscape.nativereflect.KeyBean;
import com.handscape.nativereflect.R;
import com.handscape.nativereflect.plug.PlugManager;
import com.handscape.nativereflect.utils.Utils;


//设置的映射按钮
public class Keyview extends FrameLayout implements View.OnTouchListener {

    public static final String TAG = Keyview.class.getName();

    private PlugManager plugManager;
    private ImageView imageView = null;
    private WindowManager.LayoutParams mBarLayoutParams;

    private KeyBean mKeydata;


    public Keyview(@NonNull Context context, PlugManager plugManager,int keycode) {
        super(context);
        this.plugManager = plugManager;
        setTag(TAG);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView = new ImageView(getContext());
        imageView.setLayoutParams(new LinearLayout.LayoutParams(Utils.dp2px(40), Utils.dp2px(40)));
        imageView.setAlpha(0.8f);
        imageView.setImageResource(R.drawable.key_position_bk);
        initLayoutParams(0,0);
        moveSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mKeydata=new KeyBean();
        mKeydata.getHsKeyData().setKeyCode(keycode);
        setOnTouchListener(this);
        addView(imageView);
    }

    public void setPosition(int x,int y){
        mBarLayoutParams.x=x;
        mBarLayoutParams.y=y;
    }

    public void show(){
        plugManager.addView(this,mBarLayoutParams);
    }

    public KeyBean getmKeydata() {
        mKeydata.getHsKeyData().setKeyIndex(0);
        mKeydata.getHsKeyData().setKeyPointId(0);
        PointF pointF=new PointF();
        pointF.x=mBarLayoutParams.x;
        pointF.y=mBarLayoutParams.y;
        mKeydata.getHsKeyData().setPoint(pointF);
        mKeydata.getHsKeyData().setKeyDelayTime(0);
        return mKeydata;
    }

    public WindowManager.LayoutParams getmBarLayoutParams() {
        return mBarLayoutParams;
    }

    public void initLayoutParams(int x,int y) {
        mBarLayoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBarLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mBarLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mBarLayoutParams.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        mBarLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mBarLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mBarLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mBarLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mBarLayoutParams.format = PixelFormat.RGBA_8888;
    }


    private float startX, startY, nowX, nowY, spaceX, spaceY;
    private int moveSlop = 24;
    private long startTime = 0;
    private boolean isclick = true;


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        nowX = event.getRawX();
        nowY = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isclick = true;
                startTime = SystemClock.currentThreadTimeMillis();
                startX = event.getRawX();
                startY = event.getRawY();
                spaceX = mBarLayoutParams.x - startX;
                spaceY = mBarLayoutParams.y - startY;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = startX - nowX;
                float moveY = startY - nowY;
                if (Math.abs(moveX) >= moveSlop || Math.abs(moveY) >= moveSlop) {
                    //判断如果移动，则更新位置
                    mBarLayoutParams.x = (int) (spaceX + nowX);
                    mBarLayoutParams.y = (int) (spaceY + nowY);
                    plugManager.updateview(this, mBarLayoutParams);
                    isclick = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isclick && SystemClock.currentThreadTimeMillis() - startTime < 25) {
                    //执行点击事件
                    plugManager.showPlugMain();
                }
                break;
        }
        return false;
    }

}
