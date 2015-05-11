package daylemk.xposed.xbridge.action;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import daylemk.xposed.xbridge.data.MainPreferences;
import daylemk.xposed.xbridge.hook.FrameworksHook;
import daylemk.xposed.xbridge.hook.Hook;

/**
 * Created by DayLemK on 2015/5/11.
 * search on the web action
 */
public class SearchAction extends Action{
    public static final String TAG = "SearchAction";
    public static final String STR_DESC = "Search";

    /* the key should the sub class overwrite ------------begin */
    public static final String CLASS_NAME = SearchAction.class.getSimpleName();
    public static final String PREF_SHOW_IN_RECENT_TASK = MainPreferences.PREF_SHOW_IN_RECENT_TASK +
            CLASS_NAME;
    public static final boolean PREF_SHOW_IN_RECENT_TASK_DEFAULT = true;
    public static final String PREF_SHOW_IN_STATUS_BAR = MainPreferences.PREF_SHOW_IN_STATUS_BAR +
            CLASS_NAME;
    public static final boolean PREF_SHOW_IN_STATUS_BAR_DEFAULT = true;
    public static final String PREF_SHOW_IN_APP_INFO = MainPreferences.PREF_SHOW_IN_APP_INFO +
            CLASS_NAME;
    public static final boolean PREF_SHOW_IN_APP_INFO_DEFAULT = true;
    // TODO: true for testing
    public static boolean isShowInRecentTask = true;
    public static boolean isShowInStatusBar = true;
    public static boolean isShowInAppInfo = true;
    /* the key should the sub class overwrite ------------end */

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
