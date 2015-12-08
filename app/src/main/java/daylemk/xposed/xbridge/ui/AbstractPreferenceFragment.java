package daylemk.xposed.xbridge.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.action.PlayAction;
import daylemk.xposed.xbridge.data.MainPreferences;
import daylemk.xposed.xbridge.utils.BashOperation;
import daylemk.xposed.xbridge.utils.Log;

/**
 * Created by DayLemK on 2015/5/18.
 * the abstract fragment of all fragment in xbridge activity
 */
public abstract class AbstractPreferenceFragment extends PreferenceFragment implements Preference
        .OnPreferenceClickListener, BashOperation
        .OnOperationInterface {
    public static final String TAG = "AbstractPreferenceFragment";

    protected Preference preferenceRebootSysUi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // get the reboot preference and set click listener
        preferenceRebootSysUi = findPreference(getString(R.string.key_reboot_systemui));
        if (preferenceRebootSysUi != null) {
            preferenceRebootSysUi.setOnPreferenceClickListener(this);
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // set the preference of the fragment on create
        MainPreferences.setSharedPreferences(getPreferenceManager());
        // load the preference every time
        // EDIT: no need
//        MainPreferences.loadPreference();
        // first time on UI, load keys and values
        if (PlayAction.keyShow == null) {
            Log.d(TAG, "on create: reload preference keys and values");
            MainPreferences.loadPreferenceKeys(getResources());
            MainPreferences.loadPreference(getPreferenceManager());
        }
    }

    protected void addRebootPreference(PreferenceFragment preferenceFragment) {
        preferenceFragment.addPreferencesFromResource(R.xml.preference_reboot);
    }

    @Override
    public void onStart() {
        super.onStart();
        MainPreferences.setOnPreferenceChangedLis(this.getActivity().getApplicationContext(),
                getPreferenceManager());
    }

    @Override
    public void onStop() {
        super.onStop();
        MainPreferences.unsetOnPreferenceChangedLis(getPreferenceManager());
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.equals(preferenceRebootSysUi)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            AlertDialog dialog = builder.setMessage(R.string.title_reboot_system_ui)
                    .setPositiveButton
                            (android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            BashOperation.restartSystemUI
                                                    (AbstractPreferenceFragment.this);
                                        }
                                    }).start();
                                }
                            }).setNegativeButton(android.R.string.no, null).create();
            dialog.show();
        }
        return false;
    }

    @Override
    public void onOperationDone(boolean result) {
        // nothing here for now
    }
}
