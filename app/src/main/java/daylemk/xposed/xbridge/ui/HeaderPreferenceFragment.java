package daylemk.xposed.xbridge.ui;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Switch;

import com.android.settings.widget.SwitchBar;

import java.util.ArrayList;
import java.util.Collections;
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
    public static final String ARGS_TITLE = "title";

    // the inflated view
    protected View view;
    /**
     * only can be used after onActivityCreated
     */
    protected SwitchBar switchBar;
    protected ListView list;
    protected List<Preference> preferenceList = new ArrayList<>();

    // every sub class should has this method
//    public static Drawable getPkgIcon(PackageManager pm) {
//        return pm.getDefaultActivityIcon();
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.header_perference, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(ARGS_TITLE)) {
            ActionBar actionBar = this.getActivity().getActionBar();
            if (actionBar != null) {
                actionBar.setTitle(bundle.getInt(ARGS_TITLE));
                // set icon of action bar
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
        switchBar = (SwitchBar) view.findViewById(R.id.switch_bar);
        switchBar.addOnSwitchChangeListener(this);
        list = (ListView) view.findViewById(android.R.id.list);
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
            XBridgeFragment xBridgeFragment = (XBridgeFragment) fragment;
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

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, "changed preference: " + preference + ", newValue: " + newValue);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "pressed:" + item.getItemId());
        if (item.getItemId() == android.R.id.home) {
            Log.d(TAG, "home pressed");
            this.getFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }
}
