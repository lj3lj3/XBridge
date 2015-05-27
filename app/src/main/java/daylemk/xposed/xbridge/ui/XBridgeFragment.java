package daylemk.xposed.xbridge.ui;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.action.Action;
import daylemk.xposed.xbridge.action.AppOpsAction;
import daylemk.xposed.xbridge.action.AppSettingsAction;
import daylemk.xposed.xbridge.action.ClipBoardAction;
import daylemk.xposed.xbridge.action.PlayAction;
import daylemk.xposed.xbridge.action.SearchAction;
import daylemk.xposed.xbridge.data.MainPreferences;
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

    private String keyXda;

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
        setHasOptionsMenu(true);
        // get xda key
        keyXda = getString(R.string.key_xda);
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
        // set actionbar name
        ActionBar actionBar = this.getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
        }
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
        Bundle bundle = new Bundle();
        if (PlayAction.keyShow.equals(prefKey)) {
            bundle.putInt(HeaderPreferenceFragment.ARGS_TITLE, R.string.title_play);
            fragment = PlayFragment.getFragment(bundle);
            tag = PlayFragment.TAG;
        } else if (AppOpsAction.keyShow.equals(prefKey)) {
            bundle.putInt(HeaderPreferenceFragment.ARGS_TITLE, R.string.title_appops);
            fragment = AppOpsFragment.getFragment(bundle);
            tag = AppOpsFragment.TAG;
        } else if (AppSettingsAction.keyShow.equals(prefKey)) {
            bundle.putInt(HeaderPreferenceFragment.ARGS_TITLE, R.string.title_appsettings);
            fragment = AppSettingsFragment.getFragment(bundle);
            tag = AppSettingsFragment.TAG;
        } else if (ClipBoardAction.keyShow.equals(prefKey)) {
            bundle.putInt(HeaderPreferenceFragment.ARGS_TITLE, R.string.title_clipboard);
            fragment = ClipBoardFragment.getFragment(bundle);
            tag = ClipBoardFragment.TAG;
        } else if (SearchAction.keyShow.equals(prefKey)) {
            bundle.putInt(HeaderPreferenceFragment.ARGS_TITLE, R.string.title_search);
            fragment = SearchFragment.getFragment(bundle);
            tag = SearchFragment.TAG;
        } else if (keyXda.equals(prefKey)) {
            Action.viewInXda(this.getActivity().getApplicationContext());
            return true;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // does not need menu right now
        inflater.inflate(R.menu.menu_xbridge, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem debugItem = menu.findItem(R.id.debug);
        debugItem.setChecked(Log.debug);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.debug) {
            boolean isChecked = !item.isChecked();
            item.setChecked(isChecked);
            Log.debug = isChecked;
            Log.i(TAG, "debug is checked: " + isChecked);
            // put the new value to preference
            MainPreferences.getEditablePreferences(getPreferenceManager()).edit().putBoolean(Log
                    .keyDebug, isChecked).commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * need2Load is false on default, but if we changed the value from the another fragment, we
     * need to load the status from the parameters.
     */
    public void setNeed2Load(boolean need2Load) {
        this.need2Load = need2Load;
    }
}