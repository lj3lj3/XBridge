package daylemk.xposed.xbridge.utils;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by DayLemK on 2015/4/30.
 * wrapped log class
 */
public final class Log {
    public static final String TAG = "DayL";
    public static String keyDebug;
    public static boolean debug = false;
    public static boolean isDebugDefault = false;

    private Log() {
    }

    private static String getFormattedTag(String tag) {
        return TAG + "[" + tag + "]: ";
    }

    private static void xposedLog(String tag, String msg) {
        XposedBridge.log(getFormattedTag(tag) + msg);
    }

    public static void i(String tag, String msg) {
        xposedLog(tag, msg);
        android.util.Log.i(getFormattedTag(tag), msg);
    }

    public static void v(String tag, String msg) {
        xposedLog(tag, msg);
        android.util.Log.v(getFormattedTag(tag), msg);
    }

    public static void d(String tag, String msg) {
        if (debug) {
            xposedLog(tag, msg);
            android.util.Log.d(getFormattedTag(tag), msg);
        }
    }

    public static void w(String tag, String msg) {
        xposedLog(tag, msg);
        android.util.Log.w(getFormattedTag(tag), msg);
    }

    public static void e(String tag, String msg) {
        xposedLog(tag, msg);
        android.util.Log.e(getFormattedTag(tag), msg);
    }
}
