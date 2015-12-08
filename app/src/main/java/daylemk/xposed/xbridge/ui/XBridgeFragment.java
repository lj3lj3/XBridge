package daylemk.xposed.xbridge.ui;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import daylemk.xposed.xbridge.action.AppInfoAction;
import daylemk.xposed.xbridge.action.AppOpsAction;
import daylemk.xposed.xbridge.action.AppSettingsAction;
import daylemk.xposed.xbridge.action.ClipBoardAction;
import daylemk.xposed.xbridge.action.LightningWallAction;
import daylemk.xposed.xbridge.action.MyAndroidToolsAction;
import daylemk.xposed.xbridge.action.NotifyCleanAction;
import daylemk.xposed.xbridge.action.PlayAction;
import daylemk.xposed.xbridge.action.SearchAction;
import daylemk.xposed.xbridge.action.XHaloFloatingWindowAction;
import daylemk.xposed.xbridge.action.XPrivacyAction;
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
    private SwitchPreference appOpsPreference;
    private SwitchPreference appSettingsPreference;
    private SwitchPreference clipBoardPreference;
    private SwitchPreference searchPreference;
    private SwitchPreference xPrivacyPreference;
    private SwitchPreference appInfoPreference;
    private SwitchPreference notifyCleanPreference;
    private SwitchPreference lightningWallPreference;
    private SwitchPreference xhaloFloatingWindowPreference;
    private SwitchPreference myAndroidToolsPreference;
    private Preference sizeOfIconInNotiPreference;

    private String keySizeOfIconInNoti;
    private String keyXda;
    private String sExperimental;

    private boolean need2Load = true;

    public static XBridgeFragment getFragment(Bundle bundle) {
        XBridgeFragment fragment = new XBridgeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preference_xbridge);
        addRebootPreference(this);
        // get xda key
        keyXda = getString(R.string.key_xda);
        sExperimental = getString(R.string.experimental);
        keySizeOfIconInNoti = getString(R.string.key_size_of_icon_in_noti);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        Log.d(TAG, "keys:" + PlayAction.keyShow + AppOpsAction.keyShow + AppSettingsAction
                .keyShow + ClipBoardAction.keyShow + SearchAction.keyShow);
        playPreference = (SwitchPreference) this.findPreference(PlayAction.keyShow);
        appOpsPreference = (SwitchPreference) this.findPreference(AppOpsAction.keyShow);
        appSettingsPreference = (SwitchPreference) this.findPreference(AppSettingsAction.keyShow);
        clipBoardPreference = (SwitchPreference) this.findPreference(ClipBoardAction.keyShow);
        searchPreference = (SwitchPreference) this.findPreference(SearchAction.keyShow);
        xPrivacyPreference = (SwitchPreference) this.findPreference(XPrivacyAction.keyShow);
        appInfoPreference = (SwitchPreference) this.findPreference(AppInfoAction.keyShow);
        notifyCleanPreference = (SwitchPreference) this.findPreference(NotifyCleanAction.keyShow);
        lightningWallPreference = (SwitchPreference) this.findPreference(LightningWallAction
                .keyShow);
        xhaloFloatingWindowPreference = (SwitchPreference) this.findPreference
                (XHaloFloatingWindowAction.keyShow);
        myAndroidToolsPreference = (SwitchPreference) this.findPreference
                (MyAndroidToolsAction.keyShow);
        sizeOfIconInNotiPreference = this.findPreference(keySizeOfIconInNoti);

        playPreference.setOnPreferenceChangeListener(this);
        appOpsPreference.setOnPreferenceChangeListener(this);
        appSettingsPreference.setOnPreferenceChangeListener(this);
        clipBoardPreference.setOnPreferenceChangeListener(this);
        searchPreference.setOnPreferenceChangeListener(this);
        xPrivacyPreference.setOnPreferenceChangeListener(this);
        appInfoPreference.setOnPreferenceChangeListener(this);
        notifyCleanPreference.setOnPreferenceChangeListener(this);
        lightningWallPreference.setOnPreferenceChangeListener(this);
        xhaloFloatingWindowPreference.setOnPreferenceChangeListener(this);
        myAndroidToolsPreference.setOnPreferenceChangeListener(this);
        CharSequence summary = xhaloFloatingWindowPreference.getSummary();
        // if the summary does not contain the experimental string, then add it
        if (!summary.toString().contains(sExperimental)) {
            xhaloFloatingWindowPreference.setSummary(summary + "(" + sExperimental + ")");
        }

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
            loadPreferenceValue();
            need2Load = false;
        }
        new IconLoader().execute();
    }

    public void loadPreferenceValue() {
        // here should set the preference value???
        playPreference.setChecked(PlayAction.isShow);
        appOpsPreference.setChecked(AppOpsAction.isShow);
        appSettingsPreference.setChecked(AppSettingsAction.isShow);
        clipBoardPreference.setChecked(ClipBoardAction.isShow);
        searchPreference.setChecked(SearchAction.isShow);
        xPrivacyPreference.setChecked(XPrivacyAction.isShow);
        appInfoPreference.setChecked(AppInfoAction.isShow);
        notifyCleanPreference.setChecked(NotifyCleanAction.isShow);
        lightningWallPreference.setChecked(LightningWallAction.isShow);
        xhaloFloatingWindowPreference.setChecked(XHaloFloatingWindowAction.isShow);
        myAndroidToolsPreference.setChecked(MyAndroidToolsAction.isShow);
        sizeOfIconInNotiPreference.setSummary(SizeInputFragment.size + "%");
        Log.d(TAG, "values:" +
                "PlayAction:" + PlayAction.isShow +
                ",AppOpsAction:" + AppOpsAction.isShow +
                ",AppSettingsAction:" + AppSettingsAction.isShow +
                ",ClipBoardAction:" + ClipBoardAction.isShow +
                ",SearchAction:" + SearchAction.isShow +
                ",XPrivacyAction:" + XPrivacyAction.isShow +
                ",AppInfoAction:" + AppInfoAction.isShow +
                ",XHaloFloatingWindowAction:" + XHaloFloatingWindowAction.isShow +
                ",NotifyCleanAction:" + NotifyCleanAction.isShow);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference
            preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);
        String prefKey = preference.getKey();

        Log.d(TAG, "clicked preference: " + prefKey);
        PreferenceFragment fragment = null;
        DialogFragment dialogFragment = null;
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
        } else if (XPrivacyAction.keyShow.equals(prefKey)) {
            bundle.putInt(HeaderPreferenceFragment.ARGS_TITLE, R.string.title_xprivacy);
            fragment = XPrivacyFragment.getFragment(bundle);
            tag = XPrivacyFragment.TAG;
        } else if (AppInfoAction.keyShow.equals(prefKey)) {
            bundle.putInt(HeaderPreferenceFragment.ARGS_TITLE, R.string.title_appinfo);
            fragment = AppInfoFragment.getFragment(bundle);
            tag = AppInfoFragment.TAG;
        } else if (NotifyCleanAction.keyShow.equals(prefKey)) {
            bundle.putInt(HeaderPreferenceFragment.ARGS_TITLE, R.string.title_notifyclean);
            fragment = NotifyCleanFragment.getFragment(bundle);
            tag = NotifyCleanFragment.TAG;
        } else if (LightningWallAction.keyShow.equals(prefKey)) {
            bundle.putInt(HeaderPreferenceFragment.ARGS_TITLE, R.string.title_lightningwall);
            fragment = LightningWallFragment.getFragment(bundle);
            tag = LightningWallFragment.TAG;
        } else if (XHaloFloatingWindowAction.keyShow.equals(prefKey)) {
            bundle.putInt(HeaderPreferenceFragment.ARGS_TITLE, R.string.title_xhalofloatingwindow);
            fragment = XHaloFloatingWindowFragment.getFragment(bundle);
            tag = XHaloFloatingWindowFragment.TAG;
        } else if (MyAndroidToolsAction.keyShow.equals(prefKey)) {
            bundle.putInt(HeaderPreferenceFragment.ARGS_TITLE, R.string.title_myandroidtools);
            fragment = MyAndroidToolsFragment.getFragment(bundle);
            tag = MyAndroidToolsFragment.TAG;
        } else if (keySizeOfIconInNoti.equals(prefKey)) {
            dialogFragment = SizeInputFragment.getDialogFragment(bundle);
            tag = SizeInputFragment.TAG;
        } else if (keyXda.equals(prefKey)) {
            Action.viewInXda(this.getActivity().getApplicationContext());
            return true;
        }

        if (dialogFragment != null) {
            Log.d(TAG, "dialog fragment is ok: " + dialogFragment);
            dialogFragment.show(this.getFragmentManager(), tag);
//            this.getFragmentManager().beginTransaction().add(dialogFragment, tag).addToBackStack
//                    (tag).commit();
//            // start transactions now
//            this.getFragmentManager().executePendingTransactions();
        } else if (fragment != null) {
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
        // return false to on touch the switch result
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = this.getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    /**
     * need2Load is false on default, but if we changed the value from the another fragment, we
     * need to load the status from the parameters.
     */
    public void setNeed2Load(boolean need2Load) {
        this.need2Load = need2Load;
    }

    class IconLoader extends AsyncTask<Object, Object, Object> {
        Drawable iconInfo;
        Drawable iconAppOps;
        Drawable iconAppSettings;
        //        Drawable iconClipBoard;
        Drawable iconPlay;
        //        Drawable iconSearch;
        Drawable iconXPrivacy;
        Drawable iconNotifyClean;
        Drawable iconLightningWall;
        Drawable iconXHaloFloatingWindow;
        Drawable iconMyAndroidTools;

        @Override
        protected Object doInBackground(Object[] params) {
            PackageManager packageManager = getActivity().getPackageManager();
            iconInfo = AppInfoFragment.getPkgIcon(packageManager);
            iconAppOps = AppOpsFragment.getPkgIcon(packageManager);
            iconAppSettings = AppSettingsFragment.getPkgIcon(packageManager);
//            iconClipBoard = ClipBoardFragment.getPkgIcon(getResources(),packageManager);
            iconPlay = PlayFragment.getPkgIcon(packageManager);
//            iconSearch = SearchFragment.getPkgIcon(getResources(),packageManager);
            iconXPrivacy = XPrivacyFragment.getPkgIcon(packageManager);
            iconNotifyClean = NotifyCleanFragment.getPkgIcon(packageManager);
            iconLightningWall = LightningWallFragment.getPkgIcon(packageManager);
            iconXHaloFloatingWindow = XHaloFloatingWindowFragment.getPkgIcon(packageManager);
            iconMyAndroidTools = MyAndroidToolsFragment.getPkgIcon(packageManager);
            Log.d(TAG, "load icons done:" + "iconInfo:" + iconInfo + ",iconAppOps:" + iconAppOps
                    + ",iconAppSettings:" + iconAppSettings +
//                    ",iconClipBoard:" + iconClipBoard +
                    ",iconPlay:" + iconPlay +
                    ",iconNotifyClean:" + iconNotifyClean +
                    ",iconLightningWall:" + iconLightningWall +
                    ",iconXHaloFloatingWindow:" + iconXHaloFloatingWindow +
                    ",iconMyAndroidTools:" + iconMyAndroidTools +
//                    ",iconSearch:" + iconSearch +
                    ",iconXPrivacy:" + iconXPrivacy);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            playPreference.setIcon(iconPlay);
            appOpsPreference.setIcon(iconAppOps);
            appSettingsPreference.setIcon(iconAppSettings);
//            clipBoardPreference.setIcon(iconClipBoard);
//            searchPreference.setIcon(iconSearch);
            xPrivacyPreference.setIcon(iconXPrivacy);
            appInfoPreference.setIcon(iconInfo);
            notifyCleanPreference.setIcon(iconNotifyClean);
            lightningWallPreference.setIcon(iconLightningWall);
            xhaloFloatingWindowPreference.setIcon(iconXHaloFloatingWindow);
            myAndroidToolsPreference.setIcon(iconMyAndroidTools);
            super.onPostExecute(o);
        }
    }
}