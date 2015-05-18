package daylemk.xposed.xbridge.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.widget.ListView;
import android.widget.Switch;

import com.android.settings.widget.SwitchBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.data.MainPreferences;
import daylemk.xposed.xbridge.utils.Log;

/**
 * Created by DayLemK on 2015/5/14.
 * HeaderPreference which has a master switch
 */
public abstract class HeaderPreferenceFragment extends AbstractPreferenceFragment implements
        SwitchBar
                .OnSwitchChangeListener, Preference
        .OnPreferenceChangeListener {
    public static final String TAG = "HeaderPreferenceFragment";
    /**
     * only can be used after onActivityCreated
     */
    protected SwitchBar switchBar;
    protected ListView list;
    protected List<Preference> preferenceList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        switchBar = (SwitchBar) getView().findViewById(R.id.switch_bar);
        switchBar.addOnSwitchChangeListener(this);
        list = (ListView) getView().findViewById(android.R.id.list);
    }

    @Override
    public void onStart() {
        super.onStart();
        switchBar.show();
        // enable or disable the list of preference based on the switch bar
        enablePreferencesList(switchBar.isChecked());
    }

    @Override
    public void onStop() {
        super.onStop();
        switchBar.hide();
    }

    /**
     * save the perference which use tag of switchView, and enable disable the sub preference.
     *
     * @param switchView The Switch view whose state has changed.
     * @param isChecked  The new checked state of switchView.
     */
    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        Log.d(TAG, "switch:" + switchView + ", changed: " + isChecked);
        enablePreferencesList(isChecked);

        MainPreferences.getEditablePreferences(getPreferenceManager()).edit().putBoolean((String)
                        switchView.getTag(),
                isChecked)
                .commit();
        Log.d(TAG, "switch: " + switchView.getTag() + "," + isChecked);

        // set the xbridgeFragment need2Load params
        Fragment fragment = getFragmentManager().findFragmentByTag(XBridgeFragment.TAG);
        Log.d(TAG, "xBridgeFragment on switch changed: " + fragment);
        if (fragment != null) {
            XBridgeFragment xBridgeFragment = (XBridgeFragment)fragment;
            xBridgeFragment.setNeed2Load(true);
        }
    }

    private void enablePreferencesList(boolean enable) {
        for (Preference aPreferenceList : preferenceList) {
            aPreferenceList.setEnabled(enable);
        }
    }

    protected void addPreferences2TheList(Preference... preference) {
        Collections.addAll(preferenceList, preference);
    }

    /**
     * will store preference key and value to the disk
     *
     * @param preference the changed preference
     * @param newValue   the new value
     * @return if we handle it
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, "preference key: " + preference.getKey() + ", " + newValue);
        if (preference instanceof SwitchPreference) {
            MainPreferences.getEditablePreferences(getPreferenceManager()).edit().putBoolean(
                    preference.getKey(),
                    (boolean) newValue)
                    .commit();
        }
        return true;
    }
}
