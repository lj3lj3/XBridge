package daylemk.xposed.xbridge.ui;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.action.AppOpsAction;
import daylemk.xposed.xbridge.action.AppSettingsAction;
import daylemk.xposed.xbridge.action.ClipBoardAction;
import daylemk.xposed.xbridge.action.PlayAction;
import daylemk.xposed.xbridge.action.SearchAction;
import daylemk.xposed.xbridge.utils.Log;

/**
 * @author DayLemK
 * @version 1.0
 *          28-四月-2015 9:16:48
 */
public class XBridgeFragment extends AbstractPreferenceFragment implements Preference
        .OnPreferenceChangeListener {
    public static final String TAG = "XBridgeFragment";

    private SwitchPreference playPreference;
    private SwitchPreference AppOpsPreference;
    private SwitchPreference AppSettingsPreference;
    private SwitchPreference ClipBoardPreference;
    private SwitchPreference SearchPreference;

    private boolean need2Load = false;

    public static XBridgeFragment getFragment(Bundle bundle) {
        XBridgeFragment fragment = new XBridgeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preference_xbridge);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        Log.d(TAG, "keys:" + PlayAction.keyShow + AppOpsAction.keyShow + AppSettingsAction
                .keyShow + ClipBoardAction.keyShow + SearchAction.keyShow);
        playPreference = (SwitchPreference) this.findPreference(PlayAction.keyShow);
        AppOpsPreference = (SwitchPreference) this.findPreference(AppOpsAction.keyShow);
        AppSettingsPreference = (SwitchPreference) this.findPreference(AppSettingsAction.keyShow);
        ClipBoardPreference = (SwitchPreference) this.findPreference(ClipBoardAction.keyShow);
        SearchPreference = (SwitchPreference) this.findPreference(SearchAction.keyShow);

        playPreference.setOnPreferenceChangeListener(this);
        AppOpsPreference.setOnPreferenceChangeListener(this);
        AppSettingsPreference.setOnPreferenceChangeListener(this);
        ClipBoardPreference.setOnPreferenceChangeListener(this);
        SearchPreference.setOnPreferenceChangeListener(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (need2Load) {
            // here should set the preference value???
            playPreference.setChecked(PlayAction.isShow);
            AppOpsPreference.setChecked(AppOpsAction.isShow);
            AppSettingsPreference.setChecked(AppSettingsAction.isShow);
            ClipBoardPreference.setChecked(ClipBoardAction.isShow);
            SearchPreference.setChecked(SearchAction.isShow);
            Log.d(TAG, "values:" + PlayAction.isShow + AppOpsAction.isShow + AppSettingsAction
                    .isShow
                    + ClipBoardAction.isShow + SearchAction.isShow);
            need2Load = false;
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference
            preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);
        String prefKey = preference.getKey();

        Log.d(TAG, "clicked preference: " + prefKey);
        PreferenceFragment fragment = null;
        String tag = null;
        if (PlayAction.keyShow.equals(prefKey)) {
            fragment = PlayFragment.getFragment(null);
            tag = PlayFragment.TAG;
        } else if (AppOpsAction.keyShow.equals(prefKey)) {
            fragment = AppOpsFragment.getFragment(null);
            tag = AppOpsFragment.TAG;
        } else if (AppSettingsAction.keyShow.equals(prefKey)) {
            fragment = AppSettingsFragment.getFragment(null);
            tag = AppSettingsFragment.TAG;
        } else if (ClipBoardAction.keyShow.equals(prefKey)) {
            fragment = ClipBoardFragment.getFragment(null);
            tag = ClipBoardFragment.TAG;
        } else if (SearchAction.keyShow.equals(prefKey)) {
            fragment = SearchFragment.getFragment(null);
            tag = SearchFragment.TAG;
        }

        if (fragment != null) {
            Log.d(TAG, "fragment is ok: " + fragment);
            this.getFragmentManager().beginTransaction().replace(
                    R.id.container, fragment, tag).setTransition(FragmentTransaction
                    .TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(tag).commit();
            // start transactions now
            this.getFragmentManager().executePendingTransactions();
        } else {
            Log.w(TAG, "on click fragment is null, key: " + prefKey);
            return false;
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, "changed preference: " + preference + ", newValue: " + newValue);
        return false;
    }

    /**
     * need2Load is false on default, but if we changed the value from the another fragment, we
     * need to load the status from the parameters.
     */
    public void setNeed2Load(boolean need2Load) {
        this.need2Load = need2Load;
    }
}