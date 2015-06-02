package daylemk.xposed.xbridge.ui;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.action.AppSettingsAction;

/**
 * Created by DayLemK on 2015/5/14.
 * Play Action settings fragment
 */
public class AppSettingsFragment extends HeaderPreferenceFragment {
    public static final String TAG = "AppSettingsFragment";

    private SwitchPreference preferenceStatusBar;
    private SwitchPreference preferenceRecentTask;
    private SwitchPreference preferenceAppInfo;

    // every sub class should has this method
    public static Drawable getPkgIcon(PackageManager pm) {
        return new AppSettingsAction().getIcon(pm);
    }

    public static AppSettingsFragment getFragment(Bundle bundle) {
        AppSettingsFragment fragment = new AppSettingsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preference_appsettings);
        addRebootPreference(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        preferenceStatusBar = (SwitchPreference) this.findPreference(AppSettingsAction
                .keyShowInStatusBar);
        preferenceRecentTask = (SwitchPreference) this.findPreference(AppSettingsAction
                .keyShowInRecentTask);
        preferenceAppInfo = (SwitchPreference) this.findPreference(AppSettingsAction
                .keyShowInAppInfo);
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
        switchBar.getSwitch().setTag(AppSettingsAction.keyShow);
        switchBar.setChecked(AppSettingsAction.isShow);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        super.onPreferenceChange(preference, newValue);
        return true;
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        super.onSwitchChanged(switchView, isChecked);
        AppSettingsAction.isShow = isChecked;
    }
}
