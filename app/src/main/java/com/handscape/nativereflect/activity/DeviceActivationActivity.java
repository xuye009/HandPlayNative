package com.handscape.nativereflect.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.handscape.nativereflect.R;
import com.handscape.nativereflect.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 设备激活页面
 */
public class DeviceActivationActivity extends AppCompatActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, DeviceActivationActivity.class);
        context.startActivity(intent);
    }

    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deviceactivation);
        initview();
        init();
    }

    private void initview() {
        textView = findViewById(R.id.text);

    }

    private void init() {
        //获取cpu类型
        String cpuabi = android.os.Build.CPU_ABI;
        //将文件复制到外置缓存目录
        AssetManager assetManager = getAssets();
        try {
            String outPath = getExternalCacheDir().getAbsolutePath() + File.separator + "s";
            InputStream inputStream = assetManager.open(cpuabi + File.separator + "s");
            if (FileUtils.copy(inputStream, outPath)) {
                textView.setText(cpuabi + " " + "/sdcard/Android" + outPath.split("Android")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
