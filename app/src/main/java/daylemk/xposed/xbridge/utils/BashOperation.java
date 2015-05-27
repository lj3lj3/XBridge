package daylemk.xposed.xbridge.utils;

import android.content.Context;

import java.io.DataOutputStream;
import java.io.IOException;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.data.StaticData;
import daylemk.xposed.xbridge.hook.Hook;
import de.robv.android.xposed.XposedBridge;

/**
 * Created by DayLemK on 2015/5/25.
 * Base operations
 */
public class BashOperation {
    public static final String TAG = "BashOperation";
    public static final String COM_SU = "su";
    public static final String COM_AM_FS = "am force-stop ";
    public static final String COM_PKILL = "pkill ";
    public static final String COM_EXIT = "exit\n";

    public static void forceStopPackage(Context context, String pkgName) {
        // use loop to call the toast inside thread
        String command = COM_AM_FS + pkgName + "\n";
        runCommandAsSU(command);

        // show toast
        Context xBridgeContext = Hook.getXBridgeContext(context);
        final String forceStop = xBridgeContext.getString(R.string.force_stop) +
                pkgName;
        XBridgeToast.showToastOnHandler(context, forceStop);
    }

    public static void restartSystemUI() {
        killPackage(StaticData.PKG_NAME_SYSTEMUI);
    }

    public static void killPackage(String pkgName) {
        runCommandAsSU(COM_PKILL + pkgName);
    }

    private static void runCommandAsSU(String command) {
        Log.d(TAG, "run command as su: " + command);
        try {
            Process p = Runtime.getRuntime().exec(COM_SU);
            if (p == null) {
                Log.d(TAG, "ohh, no! not SU");
                return;
            }
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes(command);
            os.flush();
            os.writeBytes(COM_EXIT);
            os.flush();
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
            XposedBridge.log(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
