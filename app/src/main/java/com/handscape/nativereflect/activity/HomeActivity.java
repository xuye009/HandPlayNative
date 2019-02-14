package com.handscape.nativereflect.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.handscape.nativereflect.R;
import com.handscape.nativereflect.activity.adapter.HomeAdapter;
import com.handscape.nativereflect.activity.manager.AppManager;
import com.handscape.nativereflect.widget.ChoiceAppDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页面
 */
public class HomeActivity  extends AppCompatActivity  implements ChoiceAppDialogFragment.ChoiceApp , View.OnClickListener {


    public static void startActivity(Context context){
        Intent intent=new Intent(context,HomeActivity.class);
        context.startActivity(intent);
    }

    private Button mBackBt,mAddAppBt;
    private RecyclerView mApplistView;

    private AppManager appManager;

    private HomeAdapter mhomeAdapter;

    private List<ApplicationInfo> applicationInfoList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        appManager=new AppManager(this);
        initview();
    }

    private void initview(){
        mBackBt=findViewById(R.id.back);
        mAddAppBt=findViewById(R.id.add_app);
        mApplistView=findViewById(R.id.app_list);
        mAddAppBt.setOnClickListener(this);
        mhomeAdapter=new HomeAdapter(this,applicationInfoList);
        mApplistView.setAdapter(mhomeAdapter);
        mApplistView.setLayoutManager(new GridLayoutManager(this,4));
    }


    @Override
    public void choice(ApplicationInfo info) {
        if(info!=null){
            if(!applicationInfoList.contains(info)){
                applicationInfoList.add(info);
                mhomeAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v==mAddAppBt){
            //添加游戏
            ChoiceAppDialogFragment.showfragment(getSupportFragmentManager(),appManager.getAllApp(),this);
        }
    }
}
