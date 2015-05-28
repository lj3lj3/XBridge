package daylemk.xposed.xbridge.hook;

import android.content.res.Resources;
import android.content.res.XModuleResources;
import android.graphics.drawable.Drawable;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.data.StaticData;
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

    private static Drawable iconCopyDefault;
    private static Drawable iconSearchDefault;

    public static void getDefaultIcons(XModuleResources sModRes) {
        if (iconCopy == null) {
            iconCopy = sModRes.getDrawable(getCopyId(sModRes), null);
            Log.d(TAG, "get the icon copy: " + iconCopy);
        }
        if (iconSearch == null) {
            iconSearch = sModRes.getDrawable(getSearchId(sModRes), null);
            Log.d(TAG, "get the icon search: " + iconSearch);
        }
        iconCopyDefault = sModRes.getDrawable(R.drawable.ic_menu_copy_material);
        iconSearchDefault = sModRes.getDrawable(R.drawable.ic_menu_search_material);
        Log.d(TAG, "default icons: " + iconCopyDefault + ", " + iconSearchDefault);
    }

    private static int getCopyId(Resources sModRes) {
        return sModRes.getIdentifier("ic_menu_copy_material",
                "drawable", StaticData.PKG_NAME_FRAMEWORK);
    }

    private static int getSearchId(Resources sModRes) {
        return sModRes.getIdentifier("ic_menu_search_material",
                "drawable", StaticData.PKG_NAME_FRAMEWORK);
    }

    public static Drawable getIconCopy() {
        Log.d(TAG, "get icon copy: " + iconCopy);
        if (iconCopy == null) {
            Log.d(TAG, "return icon copy default: " + iconCopyDefault);
            return iconCopyDefault;
        }
        return iconCopy;
    }

    public static Drawable getIconSearch() {
        Log.d(TAG, "get icon search: " + iconSearch);
        if (iconSearch == null) {
            Log.d(TAG, "return icon search default: " + iconSearchDefault);
            return iconSearchDefault;
        }
        return iconSearch;
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        super.initZygote(startupParam);
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam
                                                   initPackageResourcesParam) throws Throwable {
        super.handleInitPackageResources(initPackageResourcesParam);
        Log.d(TAG, "load framework resource");
        if (iconCopy == null) {
            Log.d(TAG, "iconCopy is null");
            // get the icon copy icon
            // use null theme
            iconCopy = initPackageResourcesParam.res.getDrawable(getCopyId
                    (initPackageResourcesParam.res), null);
        }
        if (iconSearch == null) {
            Log.d(TAG, "iconSearch is null");
            iconSearch = initPackageResourcesParam.res.getDrawable(getSearchId
                    (initPackageResourcesParam.res), null);
        }
        Log.d(TAG, "iconCopy: " + iconCopy + ",iconSearch: " + iconSearch);
    }
}