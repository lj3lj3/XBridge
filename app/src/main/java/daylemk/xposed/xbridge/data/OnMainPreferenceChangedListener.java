package daylemk.xposed.xbridge.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import daylemk.xposed.xbridge.action.SearchAction;
import daylemk.xposed.xbridge.ui.SizeInputFragment;
import daylemk.xposed.xbridge.utils.Log;
import daylemk.xposed.xbridge.action.*;

/**
 * Created by DayLemK on 2015/5/22.
 * When main preference changed on the ui, this will call, and send broadcast to the target process
 */
public class OnMainPreferenceChangedListener implements SharedPreferences
        .OnSharedPreferenceChangeListener {
    public static final String TAG = "OnMainPreferenceChangedListener";

    private Context context;

    public OnMainPreferenceChangedListener(Context context) {
        this.context = context;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Intent intent = new Intent(StaticData.ACTION_PREFERENCE_CHANGED);
        String value;
        Log.d(TAG, "preference: " + sharedPreferences);
        // if the value is String, let's get it.
        if (key.equals(SearchAction.keyUrl)) {
            value = sharedPreferences.getString(key, null);
        } else if (key.equals(SizeInputFragment.keySize)||key.equals(XHaloFloatingWindowAction.keyFloatingFlag)) {
            value = String.valueOf(sharedPreferences.getInt(key, 0));
        } else {
            value = String.valueOf(sharedPreferences.getBoolean(key, false));
        }
        intent.putExtra(StaticData.ARG_KEY, key);
        intent.putExtra(StaticData.ARG_VALUE, value);
        context.sendBroadcast(intent);
        Log.d(TAG, "changed: " + key + " -> " + value);
    }
}
