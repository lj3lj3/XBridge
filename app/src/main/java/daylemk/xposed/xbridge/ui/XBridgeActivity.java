package daylemk.xposed.xbridge.ui;

import android.app.Activity;
import android.os.Bundle;

import daylemk.xposed.xbridge.R;


/**
 * @author DayLemK
 * @version 1.0
 *          28-四月-2015 9:16:48
 */
public class XBridgeActivity extends Activity {
    public static final String TAG = "XBridgeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // use the transitions animation
        // do no need???
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        // start transition as soon as possible
//        getWindow().setAllowEnterTransitionOverlap(true);
//        getWindow().setAllowReturnTransitionOverlap(true);
        setContentView(R.layout.activity_xbridge);
        if (savedInstanceState == null) {
            // add tag, so we can get this fragment
            getFragmentManager().beginTransaction()
                    .add(R.id.container, XBridgeFragment.getFragment(null), XBridgeFragment.TAG)
                    .commit();
        }
    }
}
