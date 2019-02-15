package com.handscape.nativereflect.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.handscape.nativereflect.R;
import com.handscape.nativereflect.activity.adapter.HomeAdapter;
import com.handscape.nativereflect.activity.manager.AppManager;
import com.handscape.nativereflect.service.HandPlayService;
import com.handscape.nativereflect.service.ServiceStartHelp;
import com.handscape.nativereflect.utils.FloatWindowManager;
import com.handscape.nativereflect.widget.ChoiceAppDialogFragment;
import com.handscape.nativereflect.widget.SimpleDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页面
 */
public class HomeActivity extends AppCompatActivity implements ChoiceAppDialogFragment.ChoiceApp, View.OnClickListener {


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }

    private Button mBackBt, mAddAppBt;
    private RecyclerView mApplistView;

    private AppManager appManager;

    private HomeAdapter mhomeAdapter;

    private List<ApplicationInfo> applicationInfoList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        appManager = new AppManager(this);
        initview();

        //判断悬浮框
        if (FloatWindowManager.get().checkPermission(this)) {
        } else {
            final SimpleDialogFragment dlgFragment = SimpleDialogFragment.get(73, getString(R.string.skill_show_tip_title), getString(R.string.skill_show_tip_msg), true, true);
            dlgFragment.setCallback(new SimpleDialogFragment.Callback() {
                public void onClickOK(int reqCode) {
                    FloatWindowManager.get().applyPermission(HomeActivity.this);
                    dlgFragment.dismiss();
                }
                public void onClickCancel(int reqCode) {
                    dlgFragment.dismiss();
                }

                public void onCheckedChanged(boolean isChecked) {
                }
            });
            dlgFragment.show(getSupportFragmentManager(), "show_skill_tips");
        }

    }

    private void initview() {
        mBackBt = findViewById(R.id.back);
        mAddAppBt = findViewById(R.id.add_app);
        mApplistView = findViewById(R.id.app_list);
        mAddAppBt.setOnClickListener(this);
        mhomeAdapter = new HomeAdapter(this, applicationInfoList, onAppClickListener);
        mApplistView.setAdapter(mhomeAdapter);
        mApplistView.setLayoutManager(new GridLayoutManager(this, 4));
    }

    private View.OnClickListener onAppClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v != null && v.getTag() instanceof ApplicationInfo) {
                ApplicationInfo info = (ApplicationInfo) v.getTag();
                //启动APP，显示悬浮球
                Intent intent = getAppOpenIntentByPackageName(HomeActivity.this, info.packageName);
                startActivity(intent);
                //显示悬浮球
                HandPlayService.startService(HomeActivity.this, ServiceStartHelp.getShowPlugBundle(info.packageName));
            }
        }
    };

    public Intent getAppOpenIntentByPackageName(Context context, String packageName) {
        String mainAct = null;
        PackageManager pkgMag = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);

        @SuppressLint("WrongConstant") List<ResolveInfo> list = pkgMag.queryIntentActivities(intent,
                PackageManager.GET_ACTIVITIES);
        for (int i = 0; i < list.size(); i++) {
            ResolveInfo info = list.get(i);
            if (info.activityInfo.packageName.equals(packageName)) {
                mainAct = info.activityInfo.name;
                break;
            }
        }
        if (TextUtils.isEmpty(mainAct)) {
            return null;
        }
        intent.setComponent(new ComponentName(packageName, mainAct));
        return intent;
    }


    @Override
    public void choice(ApplicationInfo info) {
        if (info != null) {
            if (!applicationInfoList.contains(info)) {
                applicationInfoList.add(info);
                mhomeAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mAddAppBt) {
            //添加游戏
            ChoiceAppDialogFragment.showfragment(getSupportFragmentManager(), appManager.getAllApp(), this);
        }
    }
}
