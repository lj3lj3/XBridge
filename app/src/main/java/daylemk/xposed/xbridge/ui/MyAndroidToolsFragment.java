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
import daylemk.xposed.xbridge.action.MyAndroidToolsAction;

/**
 * Created by DayLemK on 2015/6/10.
 * The fragment of MyAndroidTools
 */
public class MyAndroidToolsFragment extends HeaderPreferenceFragment {
    public static final String TAG = "MyAndroidToolsFragment";

    private SwitchPreference preferenceStatusBar;
    private SwitchPreference preferenceRecentTask;
    private SwitchPreference preferenceAppInfo;

    // every sub class should has this method
    public static Drawable getPkgIcon(PackageManager pm) {
        return new MyAndroidToolsAction().getIcon(pm);
    }

    public static MyAndroidToolsFragment getFragment(Bundle bundle) {
        MyAndroidToolsFragment fragment = new MyAndroidToolsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preference_myandroidtools);
        addRebootPreference(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        preferenceStatusBar = (SwitchPreference) this.findPreference(MyAndroidToolsAction
                .keyShowInStatusBar);
        preferenceRecentTask = (SwitchPreference) this.findPreference(MyAndroidToolsAction
                .keyShowInRecentTask);
        preferenceAppInfo = (SwitchPreference) this.findPreference(MyAndroidToolsAction
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
        switchBar.getSwitch().setTag(MyAndroidToolsAction.keyShow);
        switchBar.setChecked(MyAndroidToolsAction.isShow);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        super.onPreferenceChange(preference, newValue);
        return true;
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        super.onSwitchChanged(switchView, isChecked);
        MyAndroidToolsAction.isShow = isChecked;
    }
}
