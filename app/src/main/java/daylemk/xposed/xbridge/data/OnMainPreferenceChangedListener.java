package daylemk.xposed.xbridge.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.SwitchPreference;

import daylemk.xposed.xbridge.utils.Log;

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
        String value = null;
        if (sharedPreferences instanceof SwitchPreference) {
            value = String.valueOf(sharedPreferences.getBoolean(key, false));
        } else if (sharedPreferences instanceof EditTextPreference) {
            value = sharedPreferences.getString(key, null);
        }
        intent.putExtra(StaticData.ARG_KEY, key);
        intent.putExtra(StaticData.ARG_VALUE, value);
        context.sendBroadcast(intent);
        Log.d(TAG, "changed: " + key + " -> " + value);
    }
}
