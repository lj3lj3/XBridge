package daylemk.xposed.xbridge.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.data.MainPreferences;
import daylemk.xposed.xbridge.utils.Log;
import de.robv.android.xposed.XposedBridge;

/**
 * @author DayLemon
 * @version 1.0
 *          15-十月-2015 17:16:48
 */
public class SizeInputFragment extends DialogFragment {
    public static final String TAG = "SizeInputFragment";
    public static final int SIZE_DEFAULT = 75;

    // The size of icon
    public static String keySize;
    public static int size;

    private EditText editText;

    public SizeInputFragment() {
        super();
    }

    public static SizeInputFragment getDialogFragment(Bundle bundle) {
        SizeInputFragment sizeInputFragment = new SizeInputFragment();
        sizeInputFragment.setArguments(bundle);
        return sizeInputFragment;
    }

    public static void loadPreferenceKeys(Resources sModRes) {
        keySize = sModRes.getString(R.string.key_size_of_icon_in_noti);
    }

    public static void loadPreference(SharedPreferences preferences) {
        size = preferences.getInt(keySize, SIZE_DEFAULT);
    }

    public static boolean onReceiveNewValue(String key, String value) {
        boolean result = true;
        if (key.equals(keySize)) {
            size = Integer.valueOf(value);
        } else {
            // if not found it, return false
            result = false;
        }
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        // Use inflater to inflate the layout and set the content
        LayoutInflater inflater = LayoutInflater.from(this.getActivity());
        View v = inflater.inflate(R.layout.fragment_size_input, null);
        // Use string instead of int
        editText = (EditText) v.findViewById(R.id.edit_text_size);
        editText.setText(String.valueOf(size));

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        // Here use the inflated view
        return dialogBuilder.setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Editable text = editText.getText();
                        try {
                            size = Integer.parseInt(text.toString());
                        } catch (Exception e) {
                            // Input size is not number, do nothing
                            XposedBridge.log(e);
                            return;
                        }

                        if (size < 1) {
                            size = 1;
                        } else if (size > 1000) {
                            size = 1000;
                        }

                        // set the xbridgeFragment need2Load params
                        Fragment fragment = getFragmentManager().findFragmentByTag
                                (XBridgeFragment.TAG);
                        Log.d(TAG, "xBridgeFragment on switch changed: " + fragment);
                        if (fragment != null) {
                            XBridgeFragment xBridgeFragment = (XBridgeFragment) fragment;
                            xBridgeFragment.loadPreferenceValue();
                            // Save the value
                            SharedPreferences.Editor editor = MainPreferences
                                    .getEditablePreferences(xBridgeFragment
                                            .getPreferenceManager()).edit();
                            editor.putInt(keySize, size);
                            editor.commit();
                        }
                    }
                }).setTitle("Size of icon").setCancelable(true).create();
    }
}
