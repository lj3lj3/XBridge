package daylemk.xposed.xbridge.utils;

import android.content.Context;

import java.io.DataOutputStream;

import daylemk.xposed.xbridge.data.StaticData;
import de.robv.android.xposed.XposedBridge;

/**
 * Created by DayLemK on 2015/5/25.
 * Base operations
 */
public class BashOperation {
    public static final String TAG = "BashOperation";

    public static void forceStopPackage(Context context, String pkgName, OnOperationInterface
            operationInterface) {
        // use loop to call the toast inside thread
        String command = "am force-stop " + pkgName + "\n";
        boolean result = runCommandAsSU(command);

        if (operationInterface != null) {
            operationInterface.onOperationDone(result);
        }
    }

    // use -c flag to terminate system ui process
    public static void restartSystemUI(OnOperationInterface operationInterface) {
        Log.d(TAG, "new restart system ui method");
        boolean result = true;
        try {
            Runtime.getRuntime().exec("su -c pkill " + StaticData.PKG_NAME_SYSTEMUI).waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            XposedBridge.log(e);
            result = false;
        }
        if (operationInterface != null) {
            operationInterface.onOperationDone(result);
        }
    }

    private static boolean runCommandAsSU(String command) {
        Log.d(TAG, "run command as su: " + command);
        try {
            Process p = Runtime.getRuntime().exec("su");
            if (p == null) {
                Log.d(TAG, "ohh, no! not SU");
                return false;
            }
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes(command);
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            XposedBridge.log(e);
            return false;
        }
        return true;
    }

    public interface OnOperationInterface {
        void onOperationDone(boolean result);
    }
}
