package daylemk.xposed.xbridge.ui;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.action.PlayAction;
import daylemk.xposed.xbridge.utils.Log;

/**
 * @author DayLemK
 * @version 1.0
 *          28-四月-2015 9:16:48
 */
public class XBridgeFragment extends PreferenceFragment implements Preference
        .OnPreferenceChangeListener {
    public static final String TAG = "XBridgeFragment";

    public static String keyPlayAction;
    public static String keyClipboardAction;
    public static String keySearchAction;
    public static String keyAppOpsAction;
    public static String keyAppSettingsAction;

    public static XBridgeFragment getFragment(Bundle bundle) {
        XBridgeFragment fragment = new XBridgeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preference_xbridge);

        keyPlayAction = this.getString(R.string.key_play);
        keyClipboardAction = this.getString(R.string.key_clipboard);
        keySearchAction = this.getString(R.string.key_search);
        keyAppOpsAction = this.getString(R.string.key_appops);
        keyAppSettingsAction = this.getString(R.string.key_appsettings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        this.findPreference(keyPlayAction).setOnPreferenceChangeListener(this);
        this.findPreference(keyClipboardAction).setOnPreferenceChangeListener(this);
        this.findPreference(keySearchAction).setOnPreferenceChangeListener(this);
        this.findPreference(keyAppOpsAction).setOnPreferenceChangeListener(this);
        this.findPreference(keyAppSettingsAction).setOnPreferenceChangeListener(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);
        String prefKey = preference.getKey();

        Log.d(TAG, "here");
        Log.d(TAG, "clicked preference: " + prefKey);
        PreferenceFragment fragment = null;
        String tag = null;
        if (keyPlayAction.equals(prefKey)) {
            fragment = PlayFragment.getFragment(null);
            tag = PlayFragment.TAG;
        } else if (keyClipboardAction.equals(prefKey)) {

        } else if (keySearchAction.equals(prefKey)) {

        } else if (keyAppOpsAction.equals(prefKey)) {

        } else if (keyAppSettingsAction.equals(prefKey)) {

        }

        if (fragment != null) {
            Log.d(TAG, "fragment is ok: " + fragment);
            this.getFragmentManager().beginTransaction().replace(
                    R.id.container, fragment, tag).addToBackStack(tag).commit();
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, "changed preference: " + preference + ", newValue: " + newValue);
        return false;
    }
}