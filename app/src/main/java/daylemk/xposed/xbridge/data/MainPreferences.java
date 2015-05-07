package daylemk.xposed.xbridge.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;

import java.util.prefs.Preferences;

import daylemk.xposed.xbridge.utils.Log;
import de.robv.android.xposed.XSharedPreferences;

/**
 * @author DayLemK
 * @version 1.0
 * 28-四月-2015 9:16:48
 */
public class MainPreferences {
    public static final String TAG = "MainPreferences";

    // these are the hub switch
    public static final String PREF_SHOW_IN_STATUS_BAR = "show_in_status_bar";
    public static final boolean PREF_SHOW_IN_STATUS_BAR_DEFAULT = true;
    public static final String PREF_SHOW_IN_RECENT_TASK = "show_in_recent_task";
    public static final boolean PREF_SHOW_IN_RECENT_TASK_DEFAULT = true;
    public static final String PREF_SHOW_IN_APP_INFO = "show_in_app_info";
    public static final boolean PREF_SHOW_IN_APP_INFO_DEFAULT = true;
    private static final String NAME_PREFERENCE = "xbridge_preference";

    public static boolean isShowInStatusBar = true;
    public static boolean isShowInRecentTask = true;
    public static boolean isShowInAppInfo = true;

    // the hook pref show be PREF_SHOW_IN_STATUS_BAR + Hook.class
    private static XSharedPreferences sharedPreferences;

    /**
     * get the xBridge shared preference
     */
    public static XSharedPreferences getSharedPreference() {
        Log.d(TAG, "Cano name: " + MainPreferences.class.getCanonicalName());
        if (sharedPreferences == null) {
            sharedPreferences = new XSharedPreferences(StaticData
                    .THIS_PACKAGE_NAME, NAME_PREFERENCE);
        }
        Log.i(TAG, "sharedPreference: " + sharedPreferences);
        return sharedPreferences;
    }

    public static void loadPreference() {
        isShowInStatusBar = sharedPreferences.getBoolean(PREF_SHOW_IN_STATUS_BAR,
                PREF_SHOW_IN_STATUS_BAR_DEFAULT);
        isShowInRecentTask = sharedPreferences.getBoolean(PREF_SHOW_IN_RECENT_TASK,
                PREF_SHOW_IN_STATUS_BAR_DEFAULT);
        isShowInAppInfo = sharedPreferences.getBoolean(PREF_SHOW_IN_APP_INFO,
                PREF_SHOW_IN_APP_INFO_DEFAULT);
        Log.d(TAG, "pref: isShowInStatusBar: " + isShowInStatusBar + ",isShowInRecentTask: " +
                isShowInRecentTask + ", isShowInAppInfo: " + isShowInAppInfo);
    }
}