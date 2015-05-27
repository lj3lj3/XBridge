package daylemk.xposed.xbridge.hook;

import android.graphics.drawable.Drawable;

import daylemk.xposed.xbridge.utils.Log;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;

/**
 * Created by DayLemK on 2015/5/11.
 * the frameworks hook to get some drawable etc from frameworks
 */
public class FrameworksHook extends Hook {
    public static final String TAG = "FrameworksHook";

    private static Drawable iconCopy;
    private static Drawable iconSearch;

    public static Drawable getIconCopy() {
        Log.d(TAG, "get icon copy: " + iconCopy);
        return iconCopy;
    }

    public static Drawable getIconSearch() {
        Log.d(TAG, "get icon search: " + iconSearch);
        return iconSearch;
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam
                                                   initPackageResourcesParam) throws Throwable {
        super.handleInitPackageResources(initPackageResourcesParam);
        Log.d(TAG, "load framework resource");
        // get the icon copy icon
        int iconCopyId = initPackageResourcesParam.res.getIdentifier("ic_menu_copy_material",
                "drawable",
                initPackageResourcesParam.packageName);
        // use null theme
        iconCopy = initPackageResourcesParam.res.getDrawable(iconCopyId, null);
        int iconSearchId = initPackageResourcesParam.res.getIdentifier("ic_menu_search_material",
                "drawable", initPackageResourcesParam.packageName);
        iconSearch = initPackageResourcesParam.res.getDrawable(iconSearchId, null);
        Log.d(TAG, "iocnCopy: " + iconCopy + ",iconSearch: " + iconSearch);
    }
}