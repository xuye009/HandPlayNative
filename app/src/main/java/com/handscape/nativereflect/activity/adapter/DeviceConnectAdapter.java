package com.handscape.nativereflect.activity.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handscape.nativereflect.R;

import java.util.List;

/**
 * 设备连接服务
 */
public class DeviceConnectAdapter extends RecyclerView.Adapter {


    private Context context;
    private LayoutInflater inflater;
    private List<BluetoothDevice> deviceList;

    public DeviceConnectAdapter(Context context, List<BluetoothDevice> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DeviceHolder(inflater.inflate(R.layout.device_item, viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof DeviceHolder) {
            DeviceHolder deviceHolder = (DeviceHolder) viewHolder;
            deviceHolder.init(deviceList.get(i));
        }
    }

    @Override
    public int getItemCount() {
        if (deviceList != null) {
            return deviceList.size();
        }
        return 0;
    }


    class DeviceHolder extends RecyclerView.ViewHolder {

        private TextView mNameTv, mAddressTv;

        public DeviceHolder(@NonNull View itemView) {
            super(itemView);
            mNameTv = itemView.findViewById(R.id.device_name);
            mAddressTv = itemView.findViewById(R.id.device_address);
        }

        public void init(BluetoothDevice device) {
            if (device == null) {
                return;
            }
            mNameTv.setText(device.getName());
            mAddressTv.setText(device.getAddress());
        }
    }
}
