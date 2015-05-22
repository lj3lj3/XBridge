package daylemk.xposed.xbridge.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import daylemk.xposed.xbridge.action.Action;
import daylemk.xposed.xbridge.utils.Log;

/**
 * Created by DayLemK on 2015/5/22.
 * Receiver should stain in the systemui process
 */
public class OnPreferenceChangedReceiver extends BroadcastReceiver {
    public static final String TAG = "OnPreferenceChangedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive: " + action + "," + intent);
        if (action != null && action.equals(StaticData.ACTION_PREFERENCE_CHANGED)) {
            String key = intent.getStringExtra(StaticData.ARG_KEY);
            String value = intent.getStringExtra(StaticData.ARG_VALUE);
            Log.d(TAG, "key:" + key + ",value:" + value);
            Action.onReceiveNewValue(key, value);
        }
    }
}
