package com.handscape.nativereflect.service;

import android.os.Bundle;

public class ServiceStartHelp {

    public static final String PKGNAME="pkgname";
    //显示悬浮球
    public static final String SHOW_PLUG = "com.showplug";

    public static Bundle getShowPlugBundle(String pkgName) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(SHOW_PLUG, true);
        bundle.putString(PKGNAME,pkgName);
        return bundle;
    }
}
