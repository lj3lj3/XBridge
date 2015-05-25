package daylemk.xposed.xbridge.ui;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.action.SearchAction;
import daylemk.xposed.xbridge.utils.Log;

/**
 * Created by DayLemK on 2015/5/14.
 * Play Action settings fragment
 */
public class SearchFragment extends HeaderPreferenceFragment {
    public static final String TAG = "SearchFragment";

    private SwitchPreference preferenceStatusBar;
    private SwitchPreference preferenceRecentTask;
    private SwitchPreference preferenceAppInfo;
    private SwitchPreference preferenceCustomizeUrl;
    private EditTextPreference preferenceUrl;

    public static SearchFragment getFragment(Bundle bundle) {
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preference_search);
        addRebootPreference(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        preferenceStatusBar = (SwitchPreference) this.findPreference(SearchAction
                .keyShowInStatusBar);
        preferenceRecentTask = (SwitchPreference) this.findPreference(SearchAction
                .keyShowInRecentTask);
        preferenceAppInfo = (SwitchPreference) this.findPreference(SearchAction.keyShowInAppInfo);
        preferenceCustomizeUrl = (SwitchPreference) this.findPreference(SearchAction.keyCustomize);
        preferenceUrl = (EditTextPreference) this.findPreference(SearchAction.keyUrl);

        preferenceStatusBar.setOnPreferenceChangeListener(this);
        preferenceRecentTask.setOnPreferenceChangeListener(this);
        preferenceAppInfo.setOnPreferenceChangeListener(this);
        preferenceCustomizeUrl.setOnPreferenceChangeListener(this);
        preferenceUrl.setOnPreferenceChangeListener(this);
        // set summary on preference created
        preferenceUrl.setSummary(SearchAction.getUrl());
        // set values
//        preferenceStatusBar.setChecked(PlayAction.isShowInStatusBar);
//        preferenceRecentTask.setChecked(PlayAction.isShowInRecentTask);
//        preferenceAppInfo.setChecked(PlayAction.isShowInAppInfo);

        // preferenceUrl is no needed
        addPreferences2TheList(preferenceStatusBar, preferenceRecentTask, preferenceAppInfo,
                preferenceCustomizeUrl);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // set the master switch here
        // set the tag, so wo can save the preference
        switchBar.getSwitch().setTag(SearchAction.keyShow);
        switchBar.setChecked(SearchAction.isShow);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        super.onPreferenceChange(preference, newValue);
        if (preference.equals(preferenceUrl)) {
            Log.d(TAG, "url changed: " + newValue);
            // set the new value
            SearchAction.url = (String) newValue;
            // set summary on preference changed
            preferenceUrl.setSummary(SearchAction.getUrl());
        }
        return true;
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        super.onSwitchChanged(switchView, isChecked);
        SearchAction.isShow = isChecked;
    }
}
