package daylemk.xposed.xbridge.action;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.hook.Hook;
import daylemk.xposed.xbridge.utils.BashOperation;
import daylemk.xposed.xbridge.utils.Log;

/**
 * Created by DayLemK on 2015/5/21.
 * Force stop a package, require SU permission through handleData method
 * AND include dismiss button mod
 */
public class ForceStopAction extends Action {
    public static final String TAG = "ForceStopAction";

    /* the key should the sub class overwrite ------------begin */
    public static String keyShow;
    public static String keyShowDismissButton;
    public static boolean showDismissButtonDefault = true;
    public static boolean showDefault = true;
    public static boolean isShow = true;
    public static boolean isShowDismissButton = true;
    /* the key should the sub class overwrite ------------end */

    /**
     * load the key from the string resource
     *
     * @param sModRes the module resource of package
     */
    public static void loadPreferenceKeys(Resources sModRes) {
        keyShow = sModRes.getString(R.string.key_forcestop);
        keyShowDismissButton = sModRes.getString(R.string.key_show_dismiss);
        // get the default value of this action
        showDefault = sModRes.getBoolean(R.bool.forcestop_default);
        showDismissButtonDefault = sModRes.getBoolean(R.bool.show_dismiss_default);
    }

    public static void loadPreference(SharedPreferences preferences) {
        isShow = preferences.getBoolean(keyShow,
                showDefault);
        isShowDismissButton = preferences.getBoolean(keyShowDismissButton,
                showDismissButtonDefault);
        Log.d(TAG, "load preference: " + "isShow:" + isShow + "isShowDismissButton:" +
                isShowDismissButton);
    }

    public static boolean onReceiveNewValue(String key, String value) {
        boolean result = true;
        if (key.equals(keyShow)) {
            isShow = Boolean.valueOf(value);
        } else if (key.equals(keyShowDismissButton)) {
            isShowDismissButton = Boolean.valueOf(value);
        /*} else if (key.equals(keyShowInRecentTask)) {
            isShowInRecentTask = Boolean.valueOf(value);
        } else if (key.equals(keyShowInStatusBar)) {
            isShowInStatusBar = Boolean.valueOf(value);*/
        } else {
            // if not found it, return false
            result = false;
        }
        return result;
    }

    @Override
    protected Intent getIntent(Hook hook, Context context, String pkgName) {
        return null;
    }

    @Override
    public void handleData(final Context context, final String pkgName) {
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                BashOperation.forceStopPackage(context, pkgName);
            }
        }).start();
    }

    @Override
    protected Drawable getIcon(PackageManager packageManager) {
        return null;
    }

    @Override
    public String getMenuTitle() {
        return null;
    }
}
