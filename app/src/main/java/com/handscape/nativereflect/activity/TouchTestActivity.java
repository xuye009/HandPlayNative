package com.handscape.nativereflect.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.handscape.nativereflect.R;

/**
 * 设备触摸测试界面
 */
public class TouchTestActivity extends Activity implements View.OnClickListener {


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, TouchTestActivity.class);
        context.startActivity(intent);
    }

    private Button mNextBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchtest);
        mNextBt = findViewById(R.id.next);
        mNextBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
                HomeActivity.startActivity(this);
                finish();
                break;
        }
    }
}
