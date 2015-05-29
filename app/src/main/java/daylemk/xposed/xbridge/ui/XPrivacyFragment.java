package daylemk.xposed.xbridge.ui;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.action.XPrivacyAction;

/**
 * Created by DayLemK on 2015/5/14.
 * XPrivacy Action settings fragment
 */
public class XPrivacyFragment extends HeaderPreferenceFragment {
    public static final String TAG = "XPrivacyFragment";

    private SwitchPreference preferenceStatusBar;
    private SwitchPreference preferenceRecentTask;
    private SwitchPreference preferenceAppInfo;

    public static XPrivacyFragment getFragment(Bundle bundle) {
        XPrivacyFragment fragment = new XPrivacyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preference_xprivacy);
        addRebootPreference(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        preferenceStatusBar = (SwitchPreference) this.findPreference(XPrivacyAction
                .keyShowInStatusBar);
        preferenceRecentTask = (SwitchPreference) this.findPreference(XPrivacyAction
                .keyShowInRecentTask);
        preferenceAppInfo = (SwitchPreference) this.findPreference(XPrivacyAction.keyShowInAppInfo);
        preferenceStatusBar.setOnPreferenceChangeListener(this);
        preferenceRecentTask.setOnPreferenceChangeListener(this);
        preferenceAppInfo.setOnPreferenceChangeListener(this);
        // set values
//        preferenceStatusBar.setChecked(PlayAction.isShowInStatusBar);
//        preferenceRecentTask.setChecked(PlayAction.isShowInRecentTask);
//        preferenceAppInfo.setChecked(PlayAction.isShowInAppInfo);

        addPreferences2TheList(preferenceStatusBar, preferenceRecentTask, preferenceAppInfo);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // set the master switch here
        // set the tag, so wo can save the preference
        switchBar.getSwitch().setTag(XPrivacyAction.keyShow);
        switchBar.setChecked(XPrivacyAction.isShow);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        super.onPreferenceChange(preference, newValue);
        return true;
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        super.onSwitchChanged(switchView, isChecked);
        XPrivacyAction.isShow = isChecked;
    }
}
