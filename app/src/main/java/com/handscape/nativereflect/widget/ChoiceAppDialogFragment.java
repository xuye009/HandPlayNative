package com.handscape.nativereflect.widget;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.handscape.nativereflect.R;
import com.handscape.nativereflect.widget.adapter.ChoiceAppDialogAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加APP的弹框
 */
public class ChoiceAppDialogFragment extends DialogFragment {


    public interface ChoiceApp {
        void choice(ApplicationInfo info);
    }

    public final static String TAG = ChoiceAppDialogFragment.class.getName();

    public static void showfragment(FragmentManager manager, ArrayList<ApplicationInfo> applicationInfo, ChoiceApp choiceApp) {
        ChoiceAppDialogFragment choiceAppDialogFragment = new ChoiceAppDialogFragment();
        choiceAppDialogFragment.setChoiceApp(choiceApp);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", applicationInfo);
        choiceAppDialogFragment.setArguments(bundle);
        choiceAppDialogFragment.show(manager, TAG);
    }

    private View mLayout;
    private RecyclerView mApplistView;
    private ArrayList<ApplicationInfo> applicationInfo;
    private ChoiceAppDialogAdapter choiceAppDialogAdapter;

    private ChoiceApp choiceApp;

    public void setChoiceApp(ChoiceApp choiceApp) {
        this.choiceApp = choiceApp;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            applicationInfo = getArguments().getParcelableArrayList("data");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.9), (int) (dm.heightPixels*0.7));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.dialogfragment_choiceapp, null);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return mLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initview();
    }

    private void initview() {
        mApplistView = mLayout.findViewById(R.id.appList);
        choiceAppDialogAdapter = new ChoiceAppDialogAdapter(getActivity(), applicationInfo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() instanceof ApplicationInfo) {
                    ApplicationInfo info = (ApplicationInfo) v.getTag();
                    if (choiceApp != null) {
                        choiceApp.choice(info);
                    }
                    dismiss();
                }
            }
        });
        mApplistView.setAdapter(choiceAppDialogAdapter);
        mApplistView.setLayoutManager(new GridLayoutManager(getActivity(),3));
    }

}
