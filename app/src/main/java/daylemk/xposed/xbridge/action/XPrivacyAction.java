package daylemk.xposed.xbridge.action;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.hook.Hook;
import daylemk.xposed.xbridge.utils.Log;

/**
 * Created by DayLemK on 2015/5/29.
 * The XPrivacy Action
 */
public class XPrivacyAction extends Action {
    public static final String TAG = "XPrivacyAction";
    public static final String STR_DESC = "View in XPrivacy";
    public static final String PKG_NAME = "biz.bokhorst.xprivacy";
    public static final String ACTIVITY_CLASS_NAME = PKG_NAME + ".ActivityApp";
    public static final String ARG_UID = "Uid";
//    public static final String ARG_RESTRICTION = "RestrictionName";
//    public static final String VALUE_VIEW = "view";

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

    public static Drawable sIcon = null;

    /**
     * load the key from the string resource
     *
     * @param sModRes the module resource of package
     */
    public static void loadPreferenceKeys(Resources sModRes) {
        keyShow = sModRes.getString(R.string.key_xprivacy);
        keyShowInAppInfo = sModRes.getString(R.string.key_xprivacy_app_info);
        keyShowInRecentTask = sModRes.getString(R.string.key_xprivacy_recent_task);
        keyShowInStatusBar = sModRes.getString(R.string.key_xprivacy_status_bar);
        // get the default value of this action
        showInStatusBarDefault = sModRes.getBoolean(R.bool.xprivacy_status_bar_default);
        showInRecentTaskDefault = sModRes.getBoolean(R.bool.xprivacy_recent_task_default);
        showInAppInfoDefault = sModRes.getBoolean(R.bool.xprivacy_app_info_default);
        showDefault = sModRes.getBoolean(R.bool.xprivacy_default);
    }

    public static void loadPreference(SharedPreferences preferences) {
        isShowInRecentTask = preferences.getBoolean(keyShowInRecentTask,
                showInStatusBarDefault);
        isShowInStatusBar = preferences.getBoolean(keyShowInStatusBar,
                showInRecentTaskDefault);
        isShowInAppInfo = preferences.getBoolean(keyShowInAppInfo,
                showInAppInfoDefault);
        isShow = preferences.getBoolean(keyShow,
                showDefault);
        Log.d(TAG, "load preference: " + "isShowInStatusBar:" + isShowInStatusBar +
                "isShowInRecentTask:" + isShowInRecentTask
                + "isShow:" + isShow);
    }

    public static boolean onReceiveNewValue(String key, String value) {
        boolean result = true;
        if (key.equals(keyShow)) {
            isShow = Boolean.valueOf(value);
        } else if (key.equals(keyShowInAppInfo)) {
            isShowInAppInfo = Boolean.valueOf(value);
        } else if (key.equals(keyShowInRecentTask)) {
            isShowInRecentTask = Boolean.valueOf(value);
        } else if (key.equals(keyShowInStatusBar)) {
            isShowInStatusBar = Boolean.valueOf(value);
        } else {
            // if not found it, return false
            result = false;
        }
        return result;
    }


    @Override
    protected Intent getIntent(Hook hook, Context context, String pkgName) {
        Intent intent = new Intent();
        intent.setClassName(PKG_NAME, ACTIVITY_CLASS_NAME);
        // set no animation to start activity quickly
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(ARG_UID, getUid(context, pkgName));
//        intent.putExtra(ARG_RESTRICTION, VALUE_VIEW);
        return intent;
    }

    @Override
    protected Intent getIntent(Hook hook, Context context, String pkgName, Intent originalIntent) {
        return null;
    }

    @Override
    public void handleData(Context context, String pkgName) {

    }

    @Override
    public Drawable getIcon(PackageManager packageManager) {
        if (sIcon == null) {
            sIcon = getPackageIcon(packageManager, PKG_NAME);
        }
        return sIcon;
    }

    @Override
    public String getMenuTitle() {
        return STR_DESC;
    }
}
