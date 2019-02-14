package com.handscape.nativereflect.activity.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.handscape.nativereflect.R;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter {


    private Context context;
    private LayoutInflater inflater;
    private List<ApplicationInfo> applicationInfos = null;

    public HomeAdapter(Context context, List<ApplicationInfo> applicationInfos) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.applicationInfos = applicationInfos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HomeHolder(inflater.inflate(R.layout.app_iten, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof HomeHolder) {
            HomeHolder choiceAppDialogHolder = (HomeHolder) viewHolder;
            choiceAppDialogHolder.initdata(applicationInfos.get(i));
        }
    }

    @Override
    public int getItemCount() {
        if (applicationInfos != null) {
            return applicationInfos.size();
        }
        return 0;
    }

    class HomeHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;

        public HomeHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
        }

        public void initdata(ApplicationInfo info) {
            Drawable setima = info.loadIcon(context.getPackageManager());
            String name = info.loadLabel(context.getPackageManager()).toString();
            appName.setText(name);
            appIcon.setImageDrawable(setima);
            itemView.setTag(info);
        }
    }


}
