package com.handscape.nativereflect.widget.adapter;

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

import java.util.ArrayList;

public class ChoiceAppDialogAdapter extends RecyclerView.Adapter {

    private Context context;
    private LayoutInflater inflater;
    private View.OnClickListener onClickListener;
    private ArrayList<ApplicationInfo> applicationInfo;

    public ChoiceAppDialogAdapter(Context context, ArrayList<ApplicationInfo> applicationInfos, View.OnClickListener onClickListener) {
        this.context = context;
        this.onClickListener=onClickListener;
        this.applicationInfo = applicationInfos;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ChoiceAppDialogHolder(inflater.inflate(R.layout.app_iten, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ChoiceAppDialogHolder) {
            ChoiceAppDialogHolder choiceAppDialogHolder = (ChoiceAppDialogHolder) viewHolder;
            choiceAppDialogHolder.initdata(applicationInfo.get(i));
        }
    }

    @Override
    public int getItemCount() {
        if (applicationInfo != null) {
            return applicationInfo.size();
        }
        return 0;
    }

    class ChoiceAppDialogHolder extends RecyclerView.ViewHolder {

        ImageView appIcon;
        TextView appName;

        public ChoiceAppDialogHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            itemView.setOnClickListener(onClickListener);
        }

        public void initdata(ApplicationInfo info) {
            Drawable setima = info.loadIcon(context.getPackageManager());
            String name=info.loadLabel(context.getPackageManager()).toString();
            appName.setText(name);
            appIcon.setImageDrawable(setima);
            itemView.setTag(info);
        }


    }
}
