package daylemk.xposed.xbridge.data;

import android.os.Build;

import daylemk.xposed.xbridge.XposedInit;
import de.robv.android.xposed.XposedBridge;

/**
 * @author DayLemK
 * @version 1.0
 * 28-四月-2015 9:16:48
 */
public final class StaticData {
    public static final String THIS_PACKAGE_NAME = XposedInit.class.getPackage().getName();
    public static final String PREFERENCE_MAIN_FILE = THIS_PACKAGE_NAME + "_main";

    public static final String PKG_NAME_SYSTEMUI = "com.android.systemui";

    public static final String ACTION_PREFERENCE_CHANGED = "daylemk.xposed.xbridge" +
            ".PREFERENCE_CHANGED";
    public static final String ARG_KEY = "key";
    public static final String ARG_VALUE = "value";
    /*
    public static final boolean DEFAULT_SHOW_IN_RECENT_PANEL = false;
    public static final boolean DEFAULT_SHOW_IN_APP_INFO = false;
    public static final boolean DEFAULT_SHOW_IN_NOTIFICATION = false;
    public static final boolean DEFAULT_TWO_FINGER_IN_RECENT_PANEL = false;
    */
    /*
    public static final boolean DEFAULT_DIRECTLY_SHOW_IN_PLAY = false;
    // browser-store swtich
    public static final boolean DEFAULT_USE_BROWSER = false;
    public static final boolean DEFAULT_DEBUG_LOGS = false;
    */

    public static final String LOG_TAG = "XBridge (SDK: " + Build.VERSION.SDK_INT + ") - ";
    public static final String XDA_THREAD = "http://forum.xda-developers.com/showthread.php?t=2419287";

    // can't construct this class
    private StaticData() {
    }

    public static void debugLog(String s) {
        // change to debuggable
        if (XposedInit.debuggable) {
            XposedBridge.log(s);
        }
    }
}