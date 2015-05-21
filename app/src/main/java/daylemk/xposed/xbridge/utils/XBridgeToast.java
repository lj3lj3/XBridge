package daylemk.xposed.xbridge.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;


/**
 * Created by DayLemK on 2015/5/21.
 * wrap up some toast operation
 */
public class XBridgeToast {
    public static final String TAG = "XBridgeToast";

    public static void showToast(Context context, String msg) {
        Log.d(TAG, "show toast: " + msg);
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showToastOnHandler(final Context context, final String msg) {
        Handler handler = new Handler(context.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "show toast: " + msg);
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
