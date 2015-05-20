package daylemk.xposed.xbridge.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_xbridge, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
