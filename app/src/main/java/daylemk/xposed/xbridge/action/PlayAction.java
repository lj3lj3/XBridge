package daylemk.xposed.xbridge.action;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import daylemk.xposed.xbridge.data.MainPreferences;
import daylemk.xposed.xbridge.hook.Hook;
import daylemk.xposed.xbridge.utils.Log;
import de.robv.android.xposed.XSharedPreferences;

/**
 * @author DayLemK
 * @version 1.0
 *          28-四月-2015 9:16:48
 */
public class PlayAction extends Action {
    public static final String TAG = "PlayAction";
    public static final String STR_DESC = "View in Play Store";
    public static final String PKG_NAME = "com.android.vending";

    public static final String CLASS_NAME = PlayAction.class.getSimpleName();
    public static final String PREF_SHOW_IN_RECENT_TASK = MainPreferences.PREF_SHOW_IN_RECENT_TASK +
            CLASS_NAME;
    public static final boolean PREF_SHOW_IN_RECENT_TASK_DEFAULT = true;
    public static final String PREF_SHOW_IN_STATUS_BAR = MainPreferences.PREF_SHOW_IN_STATUS_BAR +
            CLASS_NAME;
    public static final boolean PREF_SHOW_IN_STATUS_BAR_DEFAULT = true;
    public static final String PREF_SHOW_IN_APP_INFO = MainPreferences.PREF_SHOW_IN_APP_INFO +
            CLASS_NAME;
    public static final boolean PREF_SHOW_IN_APP_INFO_DEFAULT = true;
    public static boolean isShowInRecentTask = true;
    public static boolean isShowInStatusBar = true;
    public static boolean isShowInAppInfo = true;
    // just need to init the icon and listener once
    // EDIT: maybe the icon is already one instance in the system
    public static Drawable sIcon = null;
    //public static View.OnClickListener sOnClickListener = null;
    // EDIT: the on click listener should be different

    public static void loadPreference(XSharedPreferences preferences) {
        isShowInRecentTask = preferences.getBoolean(PREF_SHOW_IN_RECENT_TASK,
                PREF_SHOW_IN_RECENT_TASK_DEFAULT);
        isShowInStatusBar = preferences.getBoolean(PREF_SHOW_IN_STATUS_BAR,
                PREF_SHOW_IN_STATUS_BAR_DEFAULT);
        isShowInAppInfo = preferences.getBoolean(PREF_SHOW_IN_APP_INFO,
                PREF_SHOW_IN_APP_INFO_DEFAULT);
    }

    @Override
    protected Drawable getIcon(PackageManager packageManager) {
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
    protected Intent getIntent(Hook hook, String pkgName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://play.google.com/store/apps/details?id="
                + pkgName));
        return intent;
    }

    @Override
    protected void handleData(Context context, String pkgName) {
    }
}