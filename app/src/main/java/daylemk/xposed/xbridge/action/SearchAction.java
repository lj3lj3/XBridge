package daylemk.xposed.xbridge.action;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XModuleResources;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.data.MainPreferences;
import daylemk.xposed.xbridge.hook.FrameworksHook;
import daylemk.xposed.xbridge.hook.Hook;
import de.robv.android.xposed.XSharedPreferences;

/**
 * Created by DayLemK on 2015/5/11.
 * search on the web action
 */
public class SearchAction extends Action{
    public static final String TAG = "SearchAction";
    public static final String STR_DESC = "Search";

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

    /**
     * load the key from the string resource
     *
     * @param sModRes the module resource of package
     */
    public static void loadPreferenceKeys(Resources sModRes) {
        keyShow = sModRes.getString(R.string.key_search);
        keyShowInAppInfo = sModRes.getString(R.string.key_search_app_info);
        keyShowInRecentTask = sModRes.getString(R.string.key_search_recent_task);
        keyShowInStatusBar = sModRes.getString(R.string.key_search_status_bar);
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
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.google.com/search?q=" + pkgName));
        return intent;
    }

    @Override
    protected void handleData(Context context, String pkgName) {

    }

    @Override
    protected Drawable getIcon(PackageManager packageManager) {
        return FrameworksHook.getIconSearch();
    }

    @Override
    public String getMenuTitle() {
        return STR_DESC;
    }
}
