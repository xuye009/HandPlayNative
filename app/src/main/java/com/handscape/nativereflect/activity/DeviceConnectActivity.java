package com.handscape.nativereflect.activity;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.handscape.nativereflect.R;
import com.handscape.nativereflect.activity.adapter.DeviceConnectAdapter;
import com.handscape.nativereflect.service.HandPlayService;
import com.handscape.nativereflect.utils.Utils;
import com.handscape.sdk.inf.IHSBleScanCallBack;
import com.handscape.sdk.inf.IHSConnectCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备扫描连接界面
 */
public class DeviceConnectActivity extends Activity implements View.OnClickListener {

    private static final int LOCATION_REQUEST = 100;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, DeviceConnectActivity.class);
        context.startActivity(intent);
    }

    public static void startActivityForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, DeviceConnectActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    private RecyclerView mDeviceListView;
    private DeviceConnectAdapter mDeviceConnectAdapter;

    private TextView mLocationEnableTv;
    private TextView mStatusTv;

    private Button mRescanBt;

    private List<BluetoothDevice> deviceList = new ArrayList<>();

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof HandPlayService.HandPlayBind) {
                HandPlayService.HandPlayBind bind = (HandPlayService.HandPlayBind) service;
                handPlayService = bind.getService();
                startScanning();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            handPlayService = null;
        }
    };
    private HandPlayService handPlayService;

    private IHSBleScanCallBack bleScanCallBack = new IHSBleScanCallBack() {
        @Override
        public void scanfailed(int code) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStatusTv.setText(getString(R.string.scann_failed));
                }
            });
        }

        @Override
        public void scanfinish() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStatusTv.setText(getString(R.string.scann_finish));
                }
            });
            //如果扫描结束只扫描到一个设备，则自动连接
            if (deviceList.size() == 1) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mStatusTv.setText(getString(R.string.connecting_device));
                    }
                });
                //TODO 自动连接功能
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        handPlayService.getmSdkManager().connect(deviceList.get(0), 10 * 1000, connectCallback);
                    }
                };
                handPlayService.runWithOutUIThread(runnable);
            }
        }

        @Override
        public void onScanResult(BluetoothDevice device, int rssi) {
            if (Utils.isSupportDevice(device)) {
                if (!deviceList.contains(device)) {
                    deviceList.add(device);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDeviceConnectAdapter.notifyDataSetChanged();
                    }
                });
            }

        }

        @Override
        public void onBatchScanResults(List<BluetoothDevice> deviceList) {

        }
    };

    private IHSConnectCallback connectCallback = new IHSConnectCallback() {
        @Override
        public void actionfinish() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   if(handPlayService.getmSdkManager().isConnect()) {
                       //连接成功
                       mStatusTv.setText(getString(R.string.connect_success));
                       TouchTestActivity.startActivity(DeviceConnectActivity.this);
                       finish();
                   }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deviceconnect);
        initview();
        if (!Utils.isLocationEnable(this)) {
            Utils.setLocationService(this, LOCATION_REQUEST);
        } else {
            initdata();
        }
    }

    private void initview() {
        mDeviceListView = findViewById(R.id.device_list);
        mLocationEnableTv = findViewById(R.id.location_enable);
        mStatusTv = findViewById(R.id.status);
        mRescanBt = findViewById(R.id.rescan_button);
        mRescanBt.setOnClickListener(this);
        mLocationEnableTv.setOnClickListener(this);
    }

    private void initdata() {
        mLocationEnableTv.setVisibility(View.GONE);
        mDeviceConnectAdapter = new DeviceConnectAdapter(this, deviceList);
        mDeviceListView.setAdapter(mDeviceConnectAdapter);
        mDeviceListView.setLayoutManager(new LinearLayoutManager(this));
        //绑定服务
        if (handPlayService == null) {
            Intent intent = new Intent(this, HandPlayService.class);
            bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
        } else {
            startScanning();
        }
    }

    private void startScanning() {
        if (handPlayService == null) {
            return;
        }
        mStatusTv.setText(getString(R.string.scanning));
        deviceList.clear();

        if(handPlayService.getmSdkManager().isConnect()){
            TouchTestActivity.startActivity(DeviceConnectActivity.this);
            finish();
            return;
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handPlayService.getmSdkManager().startScanning(10 * 1000, bleScanCallBack);
            }
        };
        handPlayService.runWithOutUIThread(runnable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LOCATION_REQUEST == requestCode) {
            //申请位置信息返回
            if (Utils.isLocationEnable(this)) {
                initdata();
            } else {
                //拒绝位置申请，提示无法扫描到蓝牙设备
                Toast.makeText(this, getString(R.string.location_enable), Toast.LENGTH_SHORT).show();
                mLocationEnableTv.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location_enable:
                //打开位置
                mLocationEnableTv.setVisibility(View.GONE);
                if (!Utils.isLocationEnable(this)) {
                    Utils.setLocationService(this, LOCATION_REQUEST);
                }
                break;
            case R.id.rescan_button:
                //重新扫描
                startScanning();
                break;
        }

    }
}


