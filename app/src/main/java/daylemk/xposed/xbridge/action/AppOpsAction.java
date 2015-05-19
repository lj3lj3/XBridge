package daylemk.xposed.xbridge.action;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.hook.Hook;
import daylemk.xposed.xbridge.utils.Log;

/**
 * App Ops xposed Action
 * Created by DayLemK on 2015/5/8.
 */
public class AppOpsAction extends Action {
    public static final String TAG = "AppOpsAction";
    public static final String STR_DESC = "View in App Ops";
    public static final String PKG_NAME = "at.jclehner.appopsxposed";
    public static final String ACTIVITY_CLASS_NAME = PKG_NAME + ".AppOpsActivity";
    public static final String DETAILS_CLASS_NAME = "com.android.settings.applications" +
            ".AppOpsDetails";
    public static final String ARG_PACKAGE_NAME = "package";

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
        keyShow = sModRes.getString(R.string.key_appops);
//        keyShowInAppInfo = sModRes.getString(R.string.key_appops_app_info);
        keyShowInRecentTask = sModRes.getString(R.string.key_appops_recent_task);
        keyShowInStatusBar = sModRes.getString(R.string.key_appops_status_bar);
        // get the default value of this action
        showInStatusBarDefault = sModRes.getBoolean(R.bool.appops_status_bar_default);
        showInRecentTaskDefault = sModRes.getBoolean(R.bool.appops_recent_task_default);
//        showInAppInfoDefault = sModRes.getBoolean(R.bool.appops_app_info_default);
        showDefault = sModRes.getBoolean(R.bool.appops_default);
    }

    public static void loadPreference(SharedPreferences preferences) {
        isShowInRecentTask = preferences.getBoolean(keyShowInRecentTask,
                showInStatusBarDefault);
        isShowInStatusBar = preferences.getBoolean(keyShowInStatusBar,
                showInRecentTaskDefault);
//        isShowInAppInfo = preferences.getBoolean(keyShowInAppInfo,
//                PREF_SHOW_IN_APP_INFO_DEFAULT);
        isShow = preferences.getBoolean(keyShow,
                showDefault);
    }

    @Override
    protected Intent getIntent(Hook hook, String pkgName) {
        Intent intent = new Intent();
        intent.setClassName(PKG_NAME, ACTIVITY_CLASS_NAME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Bundle bundle = new Bundle();
        bundle.putString(ARG_PACKAGE_NAME, pkgName);

        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle);
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, DETAILS_CLASS_NAME);
        return intent;
    }

    @Override
    protected void handleData(Context context, String pkgName) {
    }

    @Override
    protected Drawable getIcon(PackageManager packageManager) {
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
