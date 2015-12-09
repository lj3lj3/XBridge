package daylemk.xposed.xbridge.action;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.lang.reflect.Field;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.hook.Hook;
import daylemk.xposed.xbridge.hook.RecentTaskHook;
import daylemk.xposed.xbridge.utils.Log;
import daylemk.xposed.xbridge.utils.XBridgeToast;

/**
 * @author DayLemK
 * @version 1.0
 *          23-06-2015
 */
public class XHaloFloatingWindowAction extends Action {
    public static final String TAG = "XHaloFloatingWindowAction";
    public static final String STR_DESC = "View in XHalo floating window";
    public static final String PKG_NAME = "com.zst.xposed.halo.floatingwindow";
    public static final String STR_FLAG_FLOATING_WINDOW = "FLAG_FLOATING_WINDOW";
    public static final int FLAG_FLOATING_WINDOW = 0x00002000;

    /* the key should the sub class overwrite ------------begin */
    public static String keyShowInStatusBar;
    public static String keyShowInRecentTask;
    public static String keyShowInAppInfo;
    public static String keyShow;
    public static String keyShowButtonNow;

    public static boolean showInStatusBarDefault = true;
    public static boolean showInRecentTaskDefault = true;
    public static boolean showInAppInfoDefault = true;
    public static boolean showDefault = true;
    public static boolean showButtonNowDefault;

    public static boolean isShowInRecentTask = true;
    public static boolean isShowInStatusBar = true;
    public static boolean isShowInAppInfo = true;
    public static boolean isShow = true;
    public static boolean isShowButtonNow = true;
    /* the key should the sub class overwrite ------------end */
    // just need to init the icon and listener once
    // EDIT: maybe the icon is already one instance in the system
    public static Drawable sIcon = null;
    //public static View.OnClickListener sOnClickListener = null;
    // EDIT: the on click listener should be different

    private int flagFloatingWindow = 0;

    /**
     * load the key from the string resource
     *
     * @param sModRes the module resource of package
     */
    public static void loadPreferenceKeys(Resources sModRes) {
        keyShow = sModRes.getString(R.string.key_xhalofloatingwindow);
        keyShowInAppInfo = sModRes.getString(R.string.key_xhalofloatingwindow_app_info);
        keyShowInRecentTask = sModRes.getString(R.string.key_xhalofloatingwindow_recent_task);
        keyShowInStatusBar = sModRes.getString(R.string.key_xhalofloatingwindow_status_bar);
        keyShowButtonNow = sModRes.getString(R.string.key_xhalofloatingwindow_show_button_now);
        // get the default value of this action
        showInStatusBarDefault = sModRes.getBoolean(R.bool.xhalofloatingwindow_status_bar_default);
        showInRecentTaskDefault = sModRes.getBoolean(R.bool
                .xhalofloatingwindow_recent_task_default);
        showInAppInfoDefault = sModRes.getBoolean(R.bool.xhalofloatingwindow_app_info_default);
        showDefault = sModRes.getBoolean(R.bool.xhalofloatingwindow_default);
        showButtonNowDefault = sModRes.getBoolean(R.bool
                .xhalofloatingwindow_show_button_now_default);
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
        isShowButtonNow = preferences.getBoolean(keyShowButtonNow,
                showButtonNowDefault);
        Log.d(TAG, "load preference: " + "isShowInStatusBar:" + isShowInStatusBar +
                "isShowInRecentTask:" + isShowInRecentTask + "isShowInAppInfo:" + isShowInAppInfo
                + "isShow:" + isShow + "isShowButtonNow:" + isShowButtonNow);
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
        } else if (key.equals(keyShowButtonNow)) {
            isShowButtonNow = Boolean.valueOf(value);
        } else {
            // if not found it, return false
            result = false;
        }
        return result;
    }

    @Override
    public Drawable getIcon(PackageManager packageManager) {
        // check the icon. if good, just return.
        // TODO: check if the app is just install or upgrade and the icon should be changed
        if (sIcon == null) {
//        Drawable pkgIcon = null;
            sIcon = getPackageIcon(packageManager, PKG_NAME);
        } else {
            Log.d(TAG, "icon is ok, no need to create again");
        }
        return sIcon;
    }

    @Override
    public String getMenuTitle() {
        return STR_DESC;
    }

    @Override
    protected Intent getIntent(Hook hook, Context context, String pkgName) {
        return null;
    }

    @Override
    protected Intent getIntent(Hook hook, Context context, String pkgName, Intent originalIntent) {
        Intent intent;
        // if the hook is recent task hook, handle it with method which with a intent parameter
        if (hook instanceof RecentTaskHook) {
            intent = new Intent(originalIntent);
        } else {
            // we don't know that launch intent, so use package manager to get it for us
            intent = context.getPackageManager().getLaunchIntentForPackage
                    (pkgName);
        }
        if (intent != null) {
            getFloatingWindowFlag();
            // the floating window flag
            intent.addFlags(flagFloatingWindow | Intent.FLAG_ACTIVITY_MULTIPLE_TASK |
                    Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            Log.w(TAG, "the launcher for: " + pkgName + " is not available");
        }

        return intent;
    }

    // Get the floating window flag from the system
    private void getFloatingWindowFlag() {
        // Get the flag
        if (flagFloatingWindow == 0) {
            try {
                Field f = Intent.class.getField(STR_FLAG_FLOATING_WINDOW);
                flagFloatingWindow = f.getInt(null);
            } catch (Exception e) {
                Log.w(TAG, "Did not found the floating window flag");
                // Did not found the floating flag, use default one
                flagFloatingWindow = FLAG_FLOATING_WINDOW;
            }
        }
        Log.d(TAG, "Floating window flag:" + Integer.toOctalString(flagFloatingWindow));
    }

    /**
     * when the launch intent for this package can't be found, then this method will be called,
     * so we just show a toast to notify user
     *
     * @param context Context
     * @param pkgName The package name
     */
    @Override
    public void handleData(Context context, String pkgName) {
        XBridgeToast.showToastOnHandler(context, Hook.getXBridgeContext(context).getString(R.string
                .cant_launch_in_xhalo) + pkgName);
    }
}