package daylemk.xposed.xbridge.hook;

import android.content.res.XResources;
import android.graphics.drawable.Drawable;

import de.robv.android.xposed.callbacks.XC_InitPackageResources;

/**
 * Created by DayLemK on 2015/5/11.
 * the frameworks hook to get some drawable etc from frameworks
 */
public class FrameworksHook extends Hook {
    public static final String TAG = "FrameworksHook";
    public static final String STR_ICON_COPY_NAME = "ic_menu_copy_material";
    public static final String STR_ICON_SEARCH_NAME = "ic_menu_search_material";
    public static final String STR_DRAWABLE = "drawable";

    private static Drawable iconCopy;
    private static Drawable iconSearch;

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam
                                                   initPackageResourcesParam) throws Throwable {
        super.handleInitPackageResources(initPackageResourcesParam);
        // get the icon copy icon
        int iconCopyId = initPackageResourcesParam.res.getIdentifier(STR_ICON_COPY_NAME,
                STR_DRAWABLE,
                initPackageResourcesParam.packageName);
        iconCopy = initPackageResourcesParam.res.getDrawable(iconCopyId);
        int iconSearchId = initPackageResourcesParam.res.getIdentifier(STR_ICON_SEARCH_NAME, STR_DRAWABLE, initPackageResourcesParam.packageName);
        iconSearch = initPackageResourcesParam.res.getDrawable(iconSearchId);
    }

    public static Drawable getIconCopy() {
        return iconCopy;
    }

    public static Drawable getIconSearch(){
        return iconSearch;
    }
}