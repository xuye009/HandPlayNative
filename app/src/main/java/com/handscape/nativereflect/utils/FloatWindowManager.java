package com.handscape.nativereflect.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings;


import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class FloatWindowManager {
    private static volatile FloatWindowManager instance;

    public static FloatWindowManager get() {
        if (instance == null) {
            synchronized (FloatWindowManager.class) {
                if (instance == null) {
                    instance = new FloatWindowManager();
                }
            }
        }
        return instance;
    }

    public boolean checkPermission(Context context) {
        if (VERSION.SDK_INT < 23 && VERSION.SDK_INT >= 19) {
            return RomUtils.m10450a(context);
        }
        if (VERSION.SDK_INT >= 23) {
            return commonROMPermissionCheck(context);
        }
        return true;
    }

    private boolean commonROMPermissionCheck(Context context) {
        if (RomUtils.checkIsMeizuRom()) {
            return RomUtils.m10450a(context);
        }
        if (RomUtils.checkIsVivoRom()) {
            return RomUtils.m10450a(context);
        }
        Boolean result = true;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class clazz = Settings.class;
                Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                result = (Boolean) canDrawOverlays.invoke(null, context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void applyPermission(Context context) {
        try {
            if (VERSION.SDK_INT < 23) {
                if (RomUtils.checkIsMiuiRom()) {
                    miuiROMPermissionApply(context);
                } else if (RomUtils.checkIsMeizuRom()) {
                    meizuROMPermissionApply(context);
                } else if (RomUtils.checkIsHuaweiRom()) {
                    huaweiROMPermissionApply(context);
                } else if (RomUtils.checkIs360Rom()) {
                    ROM360PermissionApply(context);
                } else if (RomUtils.checkIsVivoRom()) {
                    m1859h(context);
                } else if (RomUtils.checkIsSmartisan()) {
                    m1857f(context);
                } else if (RomUtils.checkIsOppoRom()) {
                    m1856e(context);
                }
            }
            commonROMPermissionApply(context);
        } catch (Exception e) {
            RomUtils.commonROMPermissionApplyInternal(context);
        }
    }

    private void ROM360PermissionApply(Context context) {
        RomUtils.manageDrawOverlaysForQihu(context);
    }

    private void m1856e(Context context) {
        RomUtils.manageDrawOverlaysForOppo(context);
    }

    private void m1857f(Context context) {
        RomUtils.manageDrawOverlaysForSmartisan(context);
    }

    private void huaweiROMPermissionApply(Context context) {
        RomUtils.manageDrawOverlaysForEmui(context);
    }

    private void m1859h(Context context) {
        RomUtils.manageDrawOverlaysForVivo(context);
    }

    private void meizuROMPermissionApply(Context context) {
        RomUtils.manageDrawOverlaysForFlyme(context);
    }

    private void miuiROMPermissionApply(Context context) {
        RomUtils.applyMiuiPermission(context);
    }

    private void commonROMPermissionApply(Context context) {
        if (RomUtils.checkIsMeizuRom()) {
            meizuROMPermissionApply(context);
        } else if (RomUtils.checkIsVivoRom()) {
            m1859h(context);
        } else if (VERSION.SDK_INT >= 23) {
            try {
                Class clazz = Settings.class;
                Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");

                Intent intent = new Intent(field.get(null).toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
