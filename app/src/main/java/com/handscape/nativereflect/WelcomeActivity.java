package com.handscape.nativereflect;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.handscape.nativereflect.activity.DeviceActivationActivity;

/**
 * 欢迎界面
 */
public class WelcomeActivity extends AppCompatActivity {

    String[] permissions = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        requestAllPermission();
    }


    //申请动态权限
    private void requestAllPermission() {
        boolean flag = false;
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                flag = true;
                break;
            }
        }
        if (flag) {
            ActivityCompat.requestPermissions(this,
                    permissions, 1);
        } else {
            DeviceActivationActivity.startActivity(this);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            requestAllPermission();
        }
    }

}
