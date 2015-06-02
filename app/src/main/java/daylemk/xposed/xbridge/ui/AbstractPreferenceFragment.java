package daylemk.xposed.xbridge.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import daylemk.xposed.xbridge.action.PlayAction;
import daylemk.xposed.xbridge.data.MainPreferences;
import daylemk.xposed.xbridge.utils.Log;

/**
 * Created by DayLemK on 2015/5/18.
 * the abstract fragment of all fragment in xbridge activity
 */
public abstract class AbstractPreferenceFragment extends PreferenceFragment {
    public static final String TAG = "AbstractPreferenceFragment";

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

    @Override
    public void onStart() {
        super.onStart();
        MainPreferences.setOnPreferenceChanged(this.getActivity().getApplicationContext(),
                getPreferenceManager());
    }

    @Override
    public void onStop() {
        super.onStop();
        MainPreferences.unSetOnPreferenceChanged(getPreferenceManager());
    }
}
