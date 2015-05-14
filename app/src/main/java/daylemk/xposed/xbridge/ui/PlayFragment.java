package daylemk.xposed.xbridge.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.android.settings.widget.SwitchBar;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.utils.Log;

/**
 * Created by DayLemK on 2015/5/14.
 */
public class PlayFragment extends HeaderPreferenceFragment  {
    public static final String TAG = "PlayFragment";


    public static PlayFragment getFragment(Bundle bundle) {
        PlayFragment fragment = new PlayFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preference_play);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.header_perference,container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
