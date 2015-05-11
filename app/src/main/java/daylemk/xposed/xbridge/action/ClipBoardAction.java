package daylemk.xposed.xbridge.action;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.data.MainPreferences;
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
    public static final String CLASS_NAME = ClipBoardAction.class.getSimpleName();
    public static final String PREF_SHOW_IN_RECENT_TASK = MainPreferences.PREF_SHOW_IN_RECENT_TASK +
            CLASS_NAME;
    public static final boolean PREF_SHOW_IN_RECENT_TASK_DEFAULT = true;
    public static final String PREF_SHOW_IN_STATUS_BAR = MainPreferences.PREF_SHOW_IN_STATUS_BAR +
            CLASS_NAME;
    public static final boolean PREF_SHOW_IN_STATUS_BAR_DEFAULT = true;
    public static final String PREF_SHOW_IN_APP_INFO = MainPreferences.PREF_SHOW_IN_APP_INFO +
            CLASS_NAME;
    // App Ops already show it itself in the app info screen
    public static final boolean PREF_SHOW_IN_APP_INFO_DEFAULT = true;
    // TODO: true for testing
    public static boolean isShowInRecentTask = true;
    public static boolean isShowInStatusBar = true;
    public static boolean isShowInAppInfo = true;
    /* the key should the sub class overwrite ------------end */

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