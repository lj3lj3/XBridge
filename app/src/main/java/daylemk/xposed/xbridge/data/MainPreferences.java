package daylemk.xposed.xbridge.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.XModuleResources;
import android.preference.PreferenceManager;

import daylemk.xposed.xbridge.action.Action;
import daylemk.xposed.xbridge.utils.Log;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;

/**
 * @author DayLemK
 * @version 1.0
 *          28-四月-2015 9:16:48
 */
public class MainPreferences {
    public static final String TAG = "MainPreferences";

    public static String NAME_PREFERENCE = "xbridge_preference";

    private static XModuleResources sModRes;
    // the hook pref show be PREF_SHOW_IN_STATUS_BAR + Hook.class
    private static XSharedPreferences sharedPreferences;
    private static SharedPreferences editablePreferences;
    private static OnMainPreferenceChangedListener listener;

    public static void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
        sModRes = XModuleResources.createInstance(startupParam.modulePath, null);
        loadPreferenceKeys(sModRes);
        // call this method to init shared preference
        getSharedPreference();
        // call this to load hub switch
//        loadPreference();
    }

    /**
     * get the xBridge shared preference
     * NOTE: this only used to get the preference from teh xposed init
     */
    public static XSharedPreferences getSharedPreference() {
        if (sharedPreferences == null) {
            Log.w(TAG, "sharedPreference is null, init it");
            sharedPreferences = new XSharedPreferences(StaticData
                    .THIS_PACKAGE_NAME, NAME_PREFERENCE);
        }
        // call reload when the data has changed
        sharedPreferences.reload();
        return sharedPreferences;
    }

    /**
     * every preference should call this method to set the name of the preference file
     *
     * @param preferenceManager preference manager of this fragment
     */
    public static void setSharedPreferences(PreferenceManager preferenceManager) {
        preferenceManager.setSharedPreferencesName(NAME_PREFERENCE);
        preferenceManager.setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
    }

    public static void setOnPreferenceChanged(Context context, PreferenceManager
            preferenceManager) {
        if (listener == null) {
            listener = new OnMainPreferenceChangedListener(context);
        }
        preferenceManager.getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
        Log.d(TAG, "register on shared preference changed listener");
    }

    public static void unSetOnPreferenceChanged(PreferenceManager preferenceManager) {
        if (listener != null) {
            preferenceManager.getSharedPreferences().unregisterOnSharedPreferenceChangeListener
                    (listener);
//            listener = null;
            Log.d(TAG, "unregister on shared preference changed listener");
        }
    }

    public static SharedPreferences getEditablePreferences(PreferenceManager preferenceManager) {
        if (editablePreferences == null) {
            Log.w(TAG, "editable sharedPreference is null, init it");
            editablePreferences = preferenceManager.getSharedPreferences();
        }

        return editablePreferences;
    }

    public static void loadPreferenceKeys(Resources resources) {
        Log.d(TAG, "load preference keys");
        Action.loadPreferenceKeys(resources);
    }

    /**
     * this CAN'T be called within the activity cycle, use loadPreference(PreferenceManager) instead
     */
    public static void loadPreference() {
        Log.d(TAG, "load preference");
        if (sharedPreferences == null) {
            getSharedPreference();
        }
        // load action preference
        Action.loadPreference(sharedPreferences);
    }

    /**
     * use this method on the activity side
     */
    public static void loadPreference(PreferenceManager preferenceManager) {
        if (editablePreferences == null) {
            getEditablePreferences(preferenceManager);
        }
        Action.loadPreference(editablePreferences);
    }
}