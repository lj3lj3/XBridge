package daylemk.xposed.xbridge.action;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.hook.FrameworksHook;
import daylemk.xposed.xbridge.hook.Hook;
import daylemk.xposed.xbridge.utils.Log;

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
        keyShow = sModRes.getString(R.string.key_search);
        keyShowInAppInfo = sModRes.getString(R.string.key_search_app_info);
        keyShowInRecentTask = sModRes.getString(R.string.key_search_recent_task);
        keyShowInStatusBar = sModRes.getString(R.string.key_search_status_bar);
        // get the default value of this action
        showInStatusBarDefault = sModRes.getBoolean(R.bool.search_status_bar_default);
        showInRecentTaskDefault = sModRes.getBoolean(R.bool.search_recent_task_default);
        showInAppInfoDefault = sModRes.getBoolean(R.bool.search_app_info_default);
        showDefault = sModRes.getBoolean(R.bool.search_default);
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

    @Override
    protected Intent getIntent(Hook hook, Context context, String pkgName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.google.com/search?q=" + pkgName));
        return intent;
    }

    @Override
    public void handleData(Context context, String pkgName) {

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
