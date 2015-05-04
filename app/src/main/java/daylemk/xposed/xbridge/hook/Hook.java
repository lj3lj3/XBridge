package daylemk.xposed.xbridge.hook;

import android.content.res.XModuleResources;

import daylemk.xposed.xbridge.utils.Log;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by DayLemK on 5/1/2015.
 */
public abstract class Hook implements IXposedHookZygoteInit, IXposedHookLoadPackage,
        IXposedHookInitPackageResources {
    public static final String TAG = "Hook";

    // module resources, need to be static
    /**
     * this module resource
     */
    protected static XModuleResources sModuleRes = null;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        if(sModuleRes == null) {
            Log.i(TAG, "init the module resource");
            sModuleRes = XModuleResources.createInstance("daylemk.xposed.xbridge", null);
        }
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam
                                                   initPackageResourcesParam) throws Throwable {

    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws
            Throwable {

    }
}
