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
import daylemk.xposed.xbridge.action.LightningWallAction;

/**
 * Created by DayLemK on 2015/6/10.
 * The fragment of LightningWall
 */
public class LightningWallFragment extends HeaderPreferenceFragment {
    public static final String TAG = "LightningWallFragment";

    private SwitchPreference preferenceStatusBar;
    private SwitchPreference preferenceRecentTask;
    private SwitchPreference preferenceAppInfo;

    // every sub class should has this method
    public static Drawable getPkgIcon(PackageManager pm) {
        return new LightningWallAction().getIcon(pm);
    }

    public static LightningWallFragment getFragment(Bundle bundle) {
        LightningWallFragment fragment = new LightningWallFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preference_lightningwall);
        addRebootPreference(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        preferenceStatusBar = (SwitchPreference) this.findPreference(LightningWallAction
                .keyShowInStatusBar);
        preferenceRecentTask = (SwitchPreference) this.findPreference(LightningWallAction
                .keyShowInRecentTask);
        preferenceAppInfo = (SwitchPreference) this.findPreference(LightningWallAction
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
        switchBar.getSwitch().setTag(LightningWallAction.keyShow);
        switchBar.setChecked(LightningWallAction.isShow);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        super.onPreferenceChange(preference, newValue);
        return true;
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        super.onSwitchChanged(switchView, isChecked);
        LightningWallAction.isShow = isChecked;
    }
}
