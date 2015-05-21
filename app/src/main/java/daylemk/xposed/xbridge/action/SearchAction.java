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
public class SearchAction extends Action {
    public static final String TAG = "SearchAction";
    public static final String STR_DESC = "Search";
    // the default of http uri prefix
    public static final String URI_HTTP_PREFIX = "http://";

    /* the key should the sub class overwrite ------------begin */
    public static String keyShowInStatusBar;
    public static String keyShowInRecentTask;
    public static String keyShowInAppInfo;
    public static String keyShow;
    // key for customize switch
    public static String keyCustomize;
    // key for customize url
    public static String keyUrl;

    public static boolean showInStatusBarDefault = true;
    public static boolean showInRecentTaskDefault = true;
    public static boolean showInAppInfoDefault = true;
    public static boolean showDefault = true;
    // key for customize switch default
    public static boolean customizeDefault = false;
    // key for url default
    public static String urlDefault = "";

    public static boolean isShowInRecentTask = true;
    public static boolean isShowInStatusBar = true;
    public static boolean isShowInAppInfo = true;
    public static boolean isShow = true;
    public static boolean isCustomize = false;
    public static String url = "";
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
        // key of customize and url
        keyCustomize = sModRes.getString(R.string.key_search_customize_url);
        keyUrl = sModRes.getString(R.string.key_search_url);
        // get the default value of this action
        showInStatusBarDefault = sModRes.getBoolean(R.bool.search_status_bar_default);
        showInRecentTaskDefault = sModRes.getBoolean(R.bool.search_recent_task_default);
        showInAppInfoDefault = sModRes.getBoolean(R.bool.search_app_info_default);
        showDefault = sModRes.getBoolean(R.bool.search_default);
        // default url and customize
        urlDefault = sModRes.getString(R.string.search_url_default);
        customizeDefault = sModRes.getBoolean(R.bool.search_customize_default);
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
        // should use customize url or not
        isCustomize = preferences.getBoolean(keyCustomize, customizeDefault);
        // the url of customize
        url = preferences.getString(keyUrl, urlDefault);

        Log.d(TAG, "load preference: " + "isShowInStatusBar:" + isShowInStatusBar +
                "isShowInRecentTask:" + isShowInRecentTask + "isShowInAppInfo:" + isShowInAppInfo
                + "isShow:" + isShow + "isCustomize:" + isCustomize + "url:" + url);
    }

    @Override
    protected Intent getIntent(Hook hook, Context context, String pkgName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // check if we should use default url or default url
        String str = urlDefault;
        if (isCustomize) {
            Log.d(TAG, "search url customized: " + url);
            if (Uri.parse(url).getScheme() == null) {
                // if the scheme is null, set it to http
                str = SearchAction.URI_HTTP_PREFIX + url;
                Log.d(TAG, "new url didn't contain prefix, new:" + str);
            }
        }
        intent.setData(Uri.parse(str + pkgName));
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
