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
import daylemk.xposed.xbridge.action.XHaloFloatingWindowAction;
import daylemk.xposed.xbridge.data.*;

/**
 * Created by DayLemK on 2015/5/14.
 * XHaloFloatingWindow Action settings fragment
 */
public class XHaloFloatingWindowFragment extends HeaderPreferenceFragment {
    public static final String TAG = "XHaloFloatingWindowFragment";

    private SwitchPreference preferenceStatusBar;
    private SwitchPreference preferenceRecentTask;
    private SwitchPreference preferenceAppInfo;
    private SwitchPreference preferenceShowButtonNow;
	private IntListPreference preferenceFloatingFlag;

    // every sub class should has this method
    public static Drawable getPkgIcon(PackageManager pm) {
        return new XHaloFloatingWindowAction().getIcon(pm);
    }

    public static XHaloFloatingWindowFragment getFragment(Bundle bundle) {
        XHaloFloatingWindowFragment fragment = new XHaloFloatingWindowFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preference_xhalofloatingwindow);
        addRebootPreference(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        preferenceStatusBar = (SwitchPreference) this.findPreference(XHaloFloatingWindowAction
                .keyShowInStatusBar);
        preferenceRecentTask = (SwitchPreference) this.findPreference(XHaloFloatingWindowAction
                .keyShowInRecentTask);
        preferenceAppInfo = (SwitchPreference) this.findPreference(XHaloFloatingWindowAction
                .keyShowInAppInfo);
        preferenceShowButtonNow = (SwitchPreference) this.findPreference(XHaloFloatingWindowAction
                .keyShowButtonNow);
		preferenceFloatingFlag = (IntListPreference) this.findPreference(XHaloFloatingWindowAction.keyFloatingFlag);
        preferenceStatusBar.setOnPreferenceChangeListener(this);
        preferenceRecentTask.setOnPreferenceChangeListener(this);
        preferenceAppInfo.setOnPreferenceChangeListener(this);
        preferenceShowButtonNow.setOnPreferenceClickListener(this);
		preferenceFloatingFlag.setOnPreferenceChangeListener(this);
        // set values
//        preferenceStatusBar.setChecked(PlayAction.isShowInStatusBar);
//        preferenceRecentTask.setChecked(PlayAction.isShowInRecentTask);
//        preferenceAppInfo.setChecked(PlayAction.isShowInAppInfo);

        addPreferences2TheList(preferenceStatusBar, preferenceRecentTask, preferenceAppInfo,
                preferenceShowButtonNow, preferenceFloatingFlag);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // set the master switch here
        // set the tag, so wo can save the preference
        switchBar.getSwitch().setTag(XHaloFloatingWindowAction.keyShow);
        switchBar.setChecked(XHaloFloatingWindowAction.isShow);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        super.onPreferenceChange(preference, newValue);
        return true;
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        super.onSwitchChanged(switchView, isChecked);
        XHaloFloatingWindowAction.isShow = isChecked;
    }
}
