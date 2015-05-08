package daylemk.xposed.xbridge.action;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import daylemk.xposed.xbridge.data.MainPreferences;
import daylemk.xposed.xbridge.hook.Hook;
import daylemk.xposed.xbridge.utils.Log;
import de.robv.android.xposed.XSharedPreferences;

/**
 * App Ops xposed Action
 * Created by DayLemK on 2015/5/8.
 */
public class AppOpsAction extends Action{
    public static final String TAG = "AppOpsAction";
    public static final String STR_DESC = "View in App Ops";
    public static final String PKG_NAME = "at.jclehner.appopsxposed";
    public static final String ACTIVITY_CLASS_NAME = "at.jclehner.appopsxposed.AppOpsActivity";
    public static final String DETAILS_CLASS_NAME = "com.android.settings.applications.AppOpsDetails";
    public static final String ARG_PACKAGE_NAME = "package";

    public static final String CLASS_NAME = AppOpsAction.class.getSimpleName();
    public static final String PREF_SHOW_IN_RECENT_TASK = MainPreferences.PREF_SHOW_IN_RECENT_TASK +
            CLASS_NAME;
    public static final boolean PREF_SHOW_IN_RECENT_TASK_DEFAULT = true;
    public static final String PREF_SHOW_IN_STATUS_BAR = MainPreferences.PREF_SHOW_IN_STATUS_BAR +
            CLASS_NAME;
    public static final boolean PREF_SHOW_IN_STATUS_BAR_DEFAULT = true;
    public static final String PREF_SHOW_IN_APP_INFO = MainPreferences.PREF_SHOW_IN_APP_INFO +
            CLASS_NAME;
    // App Ops already show it itself in the app info screen
    public static final boolean PREF_SHOW_IN_APP_INFO_DEFAULT = false;
    // TODO: true for testing
    public static boolean isShowInRecentTask = true;
    public static boolean isShowInStatusBar = true;
    public static boolean isShowInAppInfo = false;

    public static Drawable sIcon = null;

    public static void loadPreference(XSharedPreferences preferences) {
        isShowInRecentTask = preferences.getBoolean(PREF_SHOW_IN_RECENT_TASK,
                PREF_SHOW_IN_RECENT_TASK_DEFAULT);
        isShowInStatusBar = preferences.getBoolean(PREF_SHOW_IN_STATUS_BAR,
                PREF_SHOW_IN_STATUS_BAR_DEFAULT);
        isShowInAppInfo = preferences.getBoolean(PREF_SHOW_IN_APP_INFO,
                PREF_SHOW_IN_APP_INFO_DEFAULT);
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
