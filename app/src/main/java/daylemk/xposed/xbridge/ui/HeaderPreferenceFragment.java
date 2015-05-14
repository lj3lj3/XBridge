package daylemk.xposed.xbridge.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.widget.ListView;
import android.widget.Switch;

import com.android.settings.widget.SwitchBar;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.utils.Log;

/**
 * Created by DayLemK on 2015/5/14.
 */
public abstract class HeaderPreferenceFragment extends PreferenceFragment implements SwitchBar
        .OnSwitchChangeListener {
    public static final String TAG = "HeaderPreferenceFragment";
    protected SwitchBar switchBar;
    protected ListView list;

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
    }

    @Override
    public void onStop() {
        super.onStop();
        switchBar.hide();
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        Log.d(TAG, "switch:" + switchView + ", changed: " + isChecked);
    }
}
