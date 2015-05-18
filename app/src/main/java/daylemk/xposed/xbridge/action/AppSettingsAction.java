package daylemk.xposed.xbridge.action;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XModuleResources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.data.MainPreferences;
import daylemk.xposed.xbridge.hook.Hook;
import de.robv.android.xposed.XSharedPreferences;

/**
 * @author DayLemK
 * @version 1.0
 * 28-四月-2015 9:16:48
 */
public class AppSettingsAction extends Action {
    public static final String TAG = "AppSettingsAction";
    public static final String STR_DESC = "View in AppSettings";
    public static final String PKG_NAME = "de.robv.android.xposed.mods.appsettings";
    public static final String ACTIVITY_CLASS_NAME = PKG_NAME + ".settings.ApplicationSettings";
    public static final String ARG_PACKAGE_NAME = "package";

    /* the key should the sub class overwrite ------------begin */
    public static String keyShowInStatusBar;
    public static String keyShowInRecentTask;
    public static String keyShowInAppInfo;
    public static String keyShow;

    public static final boolean PREF_SHOW_IN_STATUS_BAR_DEFAULT = true;
    public static final boolean PREF_SHOW_IN_RECENT_TASK_DEFAULT = true;
    public static final boolean PREF_SHOW_IN_APP_INFO_DEFAULT = true;
    public static final boolean PREF_SHOW = true;

    public static boolean isShowInRecentTask = true;
    public static boolean isShowInStatusBar = true;
    public static boolean isShowInAppInfo = true;
    public static boolean isShow = true;
    /* the key should the sub class overwrite ------------end */

    public static Drawable sIcon = null;

    /**
     * load the key from the string resource
     *
     * @param sModRes the module resource of package
     */
    public static void loadPreferenceKeys(Resources sModRes) {
        keyShow = sModRes.getString(R.string.key_appsettings);
        keyShowInAppInfo = sModRes.getString(R.string.key_appsettings_app_info);
        keyShowInRecentTask = sModRes.getString(R.string.key_appsettings_recent_task);
        keyShowInStatusBar = sModRes.getString(R.string.key_appsettings_status_bar);
    }

    public static void loadPreference(SharedPreferences preferences) {
        isShowInRecentTask = preferences.getBoolean(keyShowInRecentTask,
                PREF_SHOW_IN_RECENT_TASK_DEFAULT);
        isShowInStatusBar = preferences.getBoolean(keyShowInStatusBar,
                PREF_SHOW_IN_STATUS_BAR_DEFAULT);
        isShowInAppInfo = preferences.getBoolean(keyShowInAppInfo,
                PREF_SHOW_IN_APP_INFO_DEFAULT);
        isShow = preferences.getBoolean(keyShow,
                PREF_SHOW);
    }

    @Override
    protected Intent getIntent(Hook hook, String pkgName) {
        Intent intent = new Intent();
        intent.setClassName(PKG_NAME, ACTIVITY_CLASS_NAME);
        intent.putExtra(ARG_PACKAGE_NAME, pkgName);
        return intent;
    }

    @Override
    protected void handleData(Context context, String pkgName) {
    }

    @Override
    public Drawable getIcon(PackageManager packageManager)
    {
        if(sIcon == null){
            sIcon = getPackageIcon(packageManager, PKG_NAME);
        }
        return sIcon;
    }

    @Override
    public String getMenuTitle() {
        return STR_DESC;
    }

}