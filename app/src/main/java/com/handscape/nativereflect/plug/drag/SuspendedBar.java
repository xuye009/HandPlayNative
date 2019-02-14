package com.handscape.nativereflect.plug.drag;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.handscape.nativereflect.R;

/**
 * 显示在应用界面的悬浮球
 */
public class SuspendedBar extends BaseDragView implements View.OnClickListener {


    private View mLayout, mconfigLayout;
    //是否处于打开配置状态
    private boolean isOpen = false;

    private ImageView mBar;

    //选择、添加、保存、清空、关闭
    private View mChoiceView, mAddView, mSaveView, mClearView, mCloseView;

    private WindowManager.LayoutParams mBarLayoutParams;

    public SuspendedBar(@NonNull Context context) {
        super(context);
    }

    @Override
    protected View getContentView() {
        mLayout = mLayoutinflater.inflate(R.layout.bar_suspend, null);
        initview();
        return mLayout;
    }

    private void initview() {
        mBar = mLayout.findViewById(R.id.bar);
        mconfigLayout = mLayout.findViewById(R.id.configlayout);
        mChoiceView = mLayout.findViewById(R.id.choice);
        mAddView = mLayout.findViewById(R.id.add);
        mSaveView = mLayout.findViewById(R.id.save);
        mClearView = mLayout.findViewById(R.id.clear);
        mCloseView = mLayout.findViewById(R.id.close);
        mconfigLayout.setVisibility(View.GONE);

        mBar.setOnClickListener(this);
        mChoiceView.setOnClickListener(this);
        mAddView.setOnClickListener(this);
        mSaveView.setOnClickListener(this);
        mClearView.setOnClickListener(this);
        mCloseView.setOnClickListener(this);
    }

    public boolean isOpen() {
        return isOpen;
    }

    @Override
    protected void onDown(MotionEvent event) {

    }

    @Override
    protected void onUp(float cx, float cy) {

    }

    @Override
    protected void onMove(MotionEvent event) {

    }

    @Override
    protected boolean isclick() {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == mBar) {
            //点击悬浮球
            if (!isOpen) {
                mconfigLayout.setVisibility(View.VISIBLE);
            }
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

        }
    }

    /**
     * 获取页面属性
     * @return
     */
    public WindowManager.LayoutParams getLayoutPArams() {
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
