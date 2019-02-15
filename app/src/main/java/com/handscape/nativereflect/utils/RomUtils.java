package com.handscape.nativereflect.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RomUtils {    //class pe
    private static final String TAG = RomUtils.class.getSimpleName();

    public static boolean checkIsVivoX21Rom() {
        return Build.MODEL.contains("vivo X21");
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStream = null;
        try {
            inputStream = new InputStreamReader(Runtime.getRuntime().exec("getprop " + propName).getInputStream());
            bufferedReader = new BufferedReader(inputStream, 1024);
            line = bufferedReader.readLine();
            bufferedReader.close();
        } catch (IOException e) {
            Log.e(TAG, "Unable to read sysprop " + propName, e);
            return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Throwable e4) {
                    Log.e(TAG, "Exception while closing InputStream", e4);
                }
            }
        }
        return line;

    }

    public static boolean checkIsHuaweiRom() {
        return Build.MANUFACTURER.contains("HUAWEI");
    }

    public static boolean checkIsMiuiRom() {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
    }

    public static boolean checkIsMeizuRom() {
        //return Build.MANUFACTURER.contains("Meizu");
        String meizuFlymeOSFlag  = getSystemProperty("ro.build.display.id");
        if (TextUtils.isEmpty(meizuFlymeOSFlag)){
            return false;
        }else if (meizuFlymeOSFlag.contains("flyme") || meizuFlymeOSFlag.toLowerCase().contains("flyme")){
            return  true;
        }else {
            return false;
        }
    }

    public static boolean checkIs360Rom() {
        return Build.MANUFACTURER.contains("QiKU")
                || Build.MANUFACTURER.contains("360");
    }

    public static boolean checkIsVivoRom() {
        String a = getSystemProperty("ro.vivo.os.name");
        return !TextUtils.isEmpty(a) && a.toLowerCase().contains("funtouch");
    }

    public static boolean checkIsSmartisan() {
        String a = getSystemProperty("ro.smartisan.version");
        return !TextUtils.isEmpty(a) && a.toLowerCase().contains("SMARTISAN");
    }

    public static boolean checkIsOppoRom() {
        return Build.MANUFACTURER.contains("OPPO");
    }

    public static boolean m10450a(Context context) {
        if (checkIsVivoRom() || checkIsOppoRom()) {
            return false;
        }
        if (VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return checkOp(context, 24/*AppOpsManager.OP_SYSTEM_ALERT_WINDOW*/);
        }
        return true;
    }

    @TargetApi(19)
    private static boolean checkOp(Context context, int op) {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                int result = ((Integer) AppOpsManager.class.getDeclaredMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class)
                        .invoke(context.getSystemService(Context.APP_OPS_SERVICE),
                                op, Binder.getCallingUid(), context.getPackageName()));
                return (result == 0);
            } catch (Throwable e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        } else {
            Log.e(TAG, "Below API 19 cannot invoke!");
        }
        return false;
    }

    private static boolean startSafely(Context context, Intent intent) {
        if (context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } else {
            Log.e(TAG, "Intent is not available! " + intent);
            return false;
        }
    }

    private final static String HUAWEI_PACKAGE = "com.huawei.systemmanager";

    // 华为
    public static boolean manageDrawOverlaysForEmui(Context context) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.setClassName(HUAWEI_PACKAGE, "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");
            if (startSafely(context, intent)) {
                return true;
            }
        }
        // Huawei Honor P6|4.4.4|3.0
        intent.setClassName(HUAWEI_PACKAGE, "com.huawei.notificationmanager.ui.NotificationManagmentActivity");
        intent.putExtra("showTabsNumber", 1);
        if (startSafely(context, intent)) {
            return true;
        }
        intent.setClassName(HUAWEI_PACKAGE, "com.huawei.permissionmanager.ui.MainActivity");
        if (startSafely(context, intent)) {
            return true;
        }
        return false;
    }

    // 魅族
    public static void manageDrawOverlaysForFlyme(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");
        intent.putExtra("packageName", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void commonROMPermissionApplyInternal(Context context) {
        try {
            Intent intent = new Intent(Settings.class.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION").get(null).toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (Throwable e) {
            Log.e(TAG, Log.getStackTraceString(e));
            context.startActivity(new Intent("android.settings.SETTINGS"));
        }
    }

    public static int getMiuiVersion() {
        String a = getSystemProperty("ro.miui.ui.version.name");
        if (a != null) {
            try {
                return Integer.parseInt(a.substring(1));
            } catch (Throwable e) {
                Log.e(TAG, "get miui version code error, version : " + a);
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return -1;
    }

    public static void applyMiuiPermission(Context context) {
        int versionCode = getMiuiVersion();
        if (versionCode == 5) {
            goToMiuiPermissionActivity_V5(context);
        } else if (versionCode == 6) {
            goToMiuiPermissionActivity_V6(context);
        } else if (versionCode == 7) {
            goToMiuiPermissionActivity_V7(context);
        } else if (versionCode == 8) {
            goToMiuiPermissionActivity_V8(context);
        } else {
            Log.e(TAG, "this is a special MIUI rom version, its version code " + versionCode);
        }
    }

    private static boolean isIntentAvailable(Intent intent, Context context) {
        if (intent == null) {
            return false;
        }
        return context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    public static void goToMiuiPermissionActivity_V5(Context context) {
        String packageName = context.getPackageName();
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", packageName, null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Log.e(TAG, "intent is not available!");
        }
    }

    public static void goToMiuiPermissionActivity_V6(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Log.e(TAG, "Intent is not available!");
        }
    }

    public static void goToMiuiPermissionActivity_V7(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Log.e(TAG, "Intent is not available!");
        }
    }

    public static void goToMiuiPermissionActivity_V8(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
            return;
        }
        intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setPackage("com.miui.securitycenter");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Log.e(TAG, "Intent is not available!");
        }
    }

    // VIVO
    public static void manageDrawOverlaysForVivo(Context context) {
        Intent intent;
        if (VERSION.SDK_INT >= 23) {
            intent = new Intent();
            intent.putExtra("packagename", context.getPackageName());
            intent.putExtra("title", "触控精灵");
            intent.setComponent(ComponentName.unflattenFromString("com.iqoo.secure/.safeguard.SoftPermissionDetailActivity"));
            if (!startSafely(context, intent)) {
                intent.setComponent(ComponentName.unflattenFromString("com.vivo.permissionmanager/.activity.SoftPermissionDetailActivity"));
                startSafely(context, intent);
            }
        } else {
            intent = new Intent("com.iqoo.secure");
            intent.setClassName("com.iqoo.secure", "com.iqoo.secure.MainActivity");
            startSafely(context, intent);
        }
    }

    // 360
    public static void manageDrawOverlaysForQihu(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings$OverlaySettingsActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            intent.setClassName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
            if (isIntentAvailable(intent, context)) {
                context.startActivity(intent);
            } else {
                Log.e(TAG, "can't open permission page with particular name, please use \"adb shell dumpsys activity\" command and tell me the name of the float window permission page");
            }
        }
    }

    // 锤子
    public static boolean manageDrawOverlaysForSmartisan(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 锤子 坚果|5.1.1|2.5.3
            Intent intent = new Intent("com.smartisanos.security.action.SWITCHED_PERMISSIONS_NEW");
            intent.setClassName("com.smartisanos.security", "com.smartisanos.security.SwitchedPermissions");
            intent.putExtra("index", 17); // 不同版本会不一样
            return startSafely(context, intent);
        } else {
            // 锤子 坚果|4.4.4|2.1.2
            Intent intent = new Intent("com.smartisanos.security.action.SWITCHED_PERMISSIONS");
            intent.setClassName("com.smartisanos.security", "com.smartisanos.security.SwitchedPermissions");
            intent.putExtra("permission", new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW});

            //        Intent intent = new Intent("com.smartisanos.security.action.MAIN");
            //        intent.setClassName("com.smartisanos.security", "com.smartisanos.security.MainActivity");
            return startSafely(context, intent);
        }
    }

    // OPPO
    public static boolean manageDrawOverlaysForOppo(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packageName", context.getPackageName());
        intent.setAction("com.oppo.safe");
        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.floatwindow.FloatWindowListActivity");
        if (startSafely(context, intent)) {
            return true;
        }
        intent.setAction("com.color.safecenter");
        intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.floatwindow.FloatWindowListActivity");
        if (startSafely(context, intent)) {
            return true;
        }

        intent.setAction("com.coloros.safecenter");
        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");
        return startSafely(context, intent);
    }
}
