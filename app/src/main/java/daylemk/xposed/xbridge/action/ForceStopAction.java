package daylemk.xposed.xbridge.action;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.hook.Hook;
import daylemk.xposed.xbridge.utils.BashOperation;
import daylemk.xposed.xbridge.utils.Log;
import daylemk.xposed.xbridge.utils.XBridgeToast;

/**
 * Created by DayLemK on 2015/5/21.
 * Force stop a package, require SU permission through handleData method
 * AND include dismiss button mod
 */
public class ForceStopAction extends Action implements BashOperation.OnOperationInterface {
    public static final String TAG = "ForceStopAction";
    public static final long PERIOD_VIBRATION = 35L;

    /* the key should the sub class overwrite ------------begin */
    public static String keyShow;
    public static String keyShowDismissButtonNow;
    public static boolean showDismissButtonNowDefault = true;
    public static boolean showDefault = true;
    public static boolean isShow = true;
    public static boolean isShowDismissButtonNow = true;
    /* the key should the sub class overwrite ------------end */

    /**
     * load the key from the string resource
     *
     * @param sModRes the module resource of package
     */
    public static void loadPreferenceKeys(Resources sModRes) {
        keyShow = sModRes.getString(R.string.key_forcestop);
        keyShowDismissButtonNow = sModRes.getString(R.string.key_show_dismiss_now);
        // get the default value of this action
        showDefault = sModRes.getBoolean(R.bool.forcestop_default);
        showDismissButtonNowDefault = sModRes.getBoolean(R.bool.show_dismiss_now_default);
    }

    public static void loadPreference(SharedPreferences preferences) {
        isShow = preferences.getBoolean(keyShow,
                showDefault);
        isShowDismissButtonNow = preferences.getBoolean(keyShowDismissButtonNow,
                showDismissButtonNowDefault);
        Log.d(TAG, "load preference: " + "isShow:" + isShow + "isShowDismissButtonNow:" +
                isShowDismissButtonNow);
    }

    public static boolean onReceiveNewValue(String key, String value) {
        boolean result = true;
        if (key.equals(keyShow)) {
            isShow = Boolean.valueOf(value);
        } else if (key.equals(keyShowDismissButtonNow)) {
            isShowDismissButtonNow = Boolean.valueOf(value);
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
    protected Intent getIntent(Hook hook, Context context, String pkgName, Intent originalIntent) {
        return null;
    }

    @Override
    public void handleData(final Context context, final String pkgName) {
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        mContext = context;
        mPkgName = pkgName;
        new Thread(new Runnable() {
            @Override
            public void run() {
                BashOperation.forceStopPackage(context, pkgName, ForceStopAction.this);
            }
        }).start();
    }

    @Override
    public Drawable getIcon(PackageManager packageManager) {
        return null;
    }

    @Override
    public String getMenuTitle() {
        return null;
    }

    @Override
    public void onOperationDone(boolean result) {
        // show toast
        Context xBridgeContext = Hook.getXBridgeContext(mContext);
        final String forceStop = (result ? "" : xBridgeContext.getString(R.string.error)) +
                xBridgeContext.getString(R.string.force_stop) +
                mPkgName;
        XBridgeToast.showToastOnHandler(mContext, forceStop);
        // add vibration
        if (result) {
            Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(PERIOD_VIBRATION);
        }
        // maybe release context here?
    }
}
