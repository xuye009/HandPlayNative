package com.handscape.nativereflect.plug.drag;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.handscape.nativereflect.R;
import com.handscape.nativereflect.plug.PlugManager;

/**
 * 点击悬浮小球后的设置菜单
 */
public class PlugMainBar extends FrameLayout implements View.OnClickListener {


    protected PlugManager plugManager;
    private View mLayout, mconfigLayout;

    private ImageView mBar;

    //选择、添加、保存、清空、关闭
    private View mChoiceView, mAddView, mSaveView, mClearView, mCloseView;

    private WindowManager.LayoutParams mBarLayoutParams;

    public PlugMainBar(@NonNull Context context, PlugManager plugManager) {
        super(context);
        this.plugManager=plugManager;
        initview();
    }

    private void initview() {
        mLayout = LayoutInflater.from(getContext()).inflate(R.layout.bar_suspend, null);
        mBar = mLayout.findViewById(R.id.bar);
        mconfigLayout = mLayout.findViewById(R.id.configlayout);
        mChoiceView = mLayout.findViewById(R.id.choice);
        mAddView = mLayout.findViewById(R.id.add);
        mSaveView = mLayout.findViewById(R.id.save);
        mClearView = mLayout.findViewById(R.id.clear);
        mCloseView = mLayout.findViewById(R.id.close);

        mBar.setOnClickListener(this);
        mChoiceView.setOnClickListener(this);
        mAddView.setOnClickListener(this);
        mSaveView.setOnClickListener(this);
        mClearView.setOnClickListener(this);
        mCloseView.setOnClickListener(this);
        addView(mLayout);
    }

    @Override
    public void onClick(View v) {
        if (v == mBar) {
            //点击悬浮球

        } else if (v == mChoiceView) {
            //选择配置

        } else if (v == mAddView) {
            //添加配置

        } else if (v == mSaveView) {
            //保存配置

        } else if (v == mClearView) {
            //清空配置

        } else if (v == mCloseView) {
            //关闭配置
            plugManager.removeView(this);
            plugManager.showFloatBar();
        }
    }

    /**
     * 获取页面属性
     * @return
     */
    public WindowManager.LayoutParams getLayoutParams() {
        mBarLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
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
        return mBarLayoutParams;
    }

}
