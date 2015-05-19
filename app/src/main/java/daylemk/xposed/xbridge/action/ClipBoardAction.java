package daylemk.xposed.xbridge.action;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.hook.FrameworksHook;
import daylemk.xposed.xbridge.hook.Hook;
import daylemk.xposed.xbridge.utils.Log;

/**
 * @author DayLemK
 * @version 1.0
 *          28-四月-2015 9:16:48
 */
public class ClipBoardAction extends Action {
    public static final String TAG = "ClipBoardAction";
    public static final String STR_DESC = "Copy to clipboard";

    public static final String ARG_PACKAGE_NAME = "package";

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
        keyShow = sModRes.getString(R.string.key_clipboard);
        keyShowInAppInfo = sModRes.getString(R.string.key_clipboard_app_info);
        keyShowInRecentTask = sModRes.getString(R.string.key_clipboard_recent_task);
        keyShowInStatusBar = sModRes.getString(R.string.key_clipboard_status_bar);
        // get the default value of this action
        showInStatusBarDefault = sModRes.getBoolean(R.bool.clipboard_status_bar_default);
        showInRecentTaskDefault = sModRes.getBoolean(R.bool.clipboard_recent_task_default);
        showInAppInfoDefault = sModRes.getBoolean(R.bool.clipboard_app_info_default);
        showDefault = sModRes.getBoolean(R.bool.clipboard_default);
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
    }

    @Override
    protected Intent getIntent(Hook hook, String pkgName) {
        return null;
    }

    @Override
    public Drawable getIcon(PackageManager packageManager) {
        return FrameworksHook.getIconCopy();
    }

    @Override
    public String getMenuTitle() {
        return STR_DESC;
    }

    @Override
    public void handleData(Context context, String pkgName) {
        Log.d(TAG, "handle in the ClipBoard");
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context
                .CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(ARG_PACKAGE_NAME, pkgName);
        clipboardManager.setPrimaryClip(clipData);
        Context xBridgeContext = Hook.getXBridgeContext(context);

        String strPkgName = xBridgeContext.getString(R.string.package_name);
        String strCopied = xBridgeContext.getString(R.string.copied);

        Toast.makeText(context, strPkgName + strCopied + "\n" + pkgName, Toast.LENGTH_LONG).show();
    }

}