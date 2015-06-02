package daylemk.xposed.xbridge.action;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.data.StaticData;
import daylemk.xposed.xbridge.hook.Hook;
import daylemk.xposed.xbridge.utils.Log;

/**
 * Created by DayLemK on 2015/6/2.
 * The app info screen of app action
 */
public class AppInfoAction extends Action {
    public static final String TAG = "AppInfoAction";
    //TODO: get menu description from xml
    public static final String STR_DESC = "Show info screen of this app";
    public static final String PKG_NAME = StaticData.PKG_NAME_SETTINGS;

    /* the key should the sub class overwrite ------------begin */
    public static String keyShowInStatusBar;
    public static String keyShowInRecentTask;
    //    public static String keyShowInAppInfo;
    public static String keyShow;

    public static boolean showInStatusBarDefault = true;
    public static boolean showInRecentTaskDefault = true;
    //    public static boolean showInAppInfoDefault = true;
    public static boolean showDefault = true;

    public static boolean isShowInRecentTask = true;
    public static boolean isShowInStatusBar = true;
    //    public static boolean isShowInAppInfo = true;
    public static boolean isShow = true;
    /* the key should the sub class overwrite ------------end */

    public static Drawable sIcon = null;

    /**
     * load the key from the string resource
     *
     * @param sModRes the module resource of package
     */
    public static void loadPreferenceKeys(Resources sModRes) {
        keyShow = sModRes.getString(R.string.key_appinfo);
//        keyShowInAppInfo = sModRes.getString(R.string.key_appinfo_app_info);
        keyShowInRecentTask = sModRes.getString(R.string.key_appinfo_recent_task);
        keyShowInStatusBar = sModRes.getString(R.string.key_appinfo_status_bar);
        // get the default value of this action
        showInStatusBarDefault = sModRes.getBoolean(R.bool.appinfo_status_bar_default);
        showInRecentTaskDefault = sModRes.getBoolean(R.bool.appinfo_recent_task_default);
//        showInAppInfoDefault = sModRes.getBoolean(R.bool.appinfo_app_info_default);
        showDefault = sModRes.getBoolean(R.bool.appinfo_default);
    }

    public static void loadPreference(SharedPreferences preferences) {
        isShowInStatusBar = preferences.getBoolean(keyShowInStatusBar,
                showInStatusBarDefault);
        isShowInRecentTask = preferences.getBoolean(keyShowInRecentTask,
                showInRecentTaskDefault);
//        isShowInAppInfo = preferences.getBoolean(keyShowInAppInfo,
//                showInAppInfoDefault);
        isShow = preferences.getBoolean(keyShow,
                showDefault);
        Log.d(TAG, "load preference: " + "isShowInStatusBar:" + isShowInStatusBar +
                "isShowInRecentTask:" + isShowInRecentTask
                + "isShow:" + isShow);
    }

    public static boolean onReceiveNewValue(String key, String value) {
        boolean result = true;
        if (key.equals(keyShow)) {
            isShow = Boolean.valueOf(value);
//        } else if (key.equals(keyShowInAppInfo)) {
//            isShowInAppInfo = Boolean.valueOf(value);
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
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", pkgName, null));
        intent.setComponent(intent.resolveActivity(context.getPackageManager()));
        return intent;
    }

    @Override
    public void handleData(Context context, String pkgName) {

    }

    @Override
    public Drawable getIcon(PackageManager packageManager) {
        // check the icon. if good, just return.
        if (sIcon == null) {
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
}
