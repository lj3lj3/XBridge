package daylemk.xposed.xbridge.action;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.hook.Hook;
import daylemk.xposed.xbridge.utils.Log;
import de.robv.android.xposed.XposedBridge;

/**
 * Created by DayLemK on 2015/6/10.
 * the action of package: de.defim.apk.myandroidtools
 */
public class MyAndroidToolsAction extends Action {
    public static final String TAG = "MyAndroidToolsAction";
    public static final String STR_DESC = "View in MyAndroidTools";
    public static final String PKG_NAME = "cn.wq.myandroidtools";
    public static final String PKG_PRO_NAME = "cn.wq.myandroidtoolspro";
    public static final String ACTION_NAME = "cn.wq.myandroidtools.SHOW_APP_INFO";
    public static final String ARG_PKG_NAME = "packageName";

    /* the key should the sub class overwrite ------------begin */
    public static String keyShowInStatusBar;
    public static String keyShowInRecentTask;
    public static String keyShowInAppInfo;
    public static String keyShow;

    public static boolean showInStatusBarDefault = true;
    public static boolean showInRecentTaskDefault = true;
    public static boolean showInAppInfoDefault = true;
    public static boolean showDefault = true;

    public static boolean isShowInRecentTask = true;
    public static boolean isShowInStatusBar = true;
    public static boolean isShowInAppInfo = true;
    public static boolean isShow = true;
    /* the key should the sub class overwrite ------------end */

    /**
     * load the key from the string resource
     *
     * @param sModRes the module resource of package
     */
    public static void loadPreferenceKeys(Resources sModRes) {
        keyShow = sModRes.getString(R.string.key_myandroidtools);
        keyShowInAppInfo = sModRes.getString(R.string.key_myandroidtools_app_info);
        keyShowInRecentTask = sModRes.getString(R.string.key_myandroidtools_recent_task);
        keyShowInStatusBar = sModRes.getString(R.string.key_myandroidtools_status_bar);
        // get the default value of this action
        showInStatusBarDefault = sModRes.getBoolean(R.bool.myandroidtools_status_bar_default);
        showInRecentTaskDefault = sModRes.getBoolean(R.bool.myandroidtools_recent_task_default);
        showInAppInfoDefault = sModRes.getBoolean(R.bool.myandroidtools_app_info_default);
        showDefault = sModRes.getBoolean(R.bool.myandroidtools_default);
    }

    public static void loadPreference(SharedPreferences preferences) {
        isShowInStatusBar = preferences.getBoolean(keyShowInStatusBar,
                showInStatusBarDefault);
        isShowInRecentTask = preferences.getBoolean(keyShowInRecentTask,
                showInRecentTaskDefault);
        isShowInAppInfo = preferences.getBoolean(keyShowInAppInfo,
                showInAppInfoDefault);
        isShow = preferences.getBoolean(keyShow,
                showDefault);
        Log.d(TAG, "load preference: " + "isShowInStatusBar:" + isShowInStatusBar +
                "isShowInRecentTask:" + isShowInRecentTask + "isShowInAppInfo:" + isShowInAppInfo
                + "isShow:" + isShow);
    }

    public static boolean onReceiveNewValue(String key, String value) {
        boolean result = true;
        if (key.equals(keyShow)) {
            isShow = Boolean.valueOf(value);
        } else if (key.equals(keyShowInAppInfo)) {
            isShowInAppInfo = Boolean.valueOf(value);
        } else if (key.equals(keyShowInRecentTask)) {
            isShowInRecentTask = Boolean.valueOf(value);
        } else if (key.equals(keyShowInStatusBar)) {
            isShowInStatusBar = Boolean.valueOf(value);
        } else {
            // if not found it, return false
            result = false;
        }
        return result;
    }

    @Override
    protected Intent getIntent(Hook hook, Context context, String pkgName) {
        return null;
    }

    @Override
    protected Intent getIntent(Hook hook, Context context, String pkgName, Intent originalIntent) {
        return null;
    }

    @Override
    public void handleData(Context context, String pkgName) {
        Intent intent = new Intent(ACTION_NAME);
        //intent.setPackage(PKG_NAME);
        intent.putExtra(ARG_PKG_NAME, pkgName);
        Log.d(TAG, "send broadcast:" + intent + ", pkg: " + pkgName);
        context.sendBroadcast(intent);
    }

    @Override
    public Drawable getIcon(PackageManager packageManager) {
        String pkgName = PKG_NAME;
        try {
            packageManager.getPackageInfo(PKG_PRO_NAME, 0);
            pkgName = PKG_PRO_NAME;
        } catch (PackageManager.NameNotFoundException e) {
            XposedBridge.log(e);
            Log.d(TAG, "Did not found the pro package");
        }
        return getPackageIcon(packageManager, pkgName);
    }

    @Override
    public String getMenuTitle() {
        return STR_DESC;
    }
}
