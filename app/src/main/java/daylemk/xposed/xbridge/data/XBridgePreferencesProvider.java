package daylemk.xposed.xbridge.data;

import com.crossbowffs.remotepreferences.RemotePreferenceProvider;

/**
 * Created by DayLemon Liu on 2016/8/12.
 */
public class XBridgePreferencesProvider extends RemotePreferenceProvider {
    public static final String NAME_AUTHORITY = "daylemk.xposed.xbridge.preferences";

    public XBridgePreferencesProvider() {
        super(NAME_AUTHORITY, new String[]{MainPreferences.NAME_PREFERENCE});
    }
}
