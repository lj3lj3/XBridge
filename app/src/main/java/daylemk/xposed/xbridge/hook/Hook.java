package daylemk.xposed.xbridge.hook;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.XModuleResources;

import daylemk.xposed.xbridge.XposedInit;
import daylemk.xposed.xbridge.data.StaticData;
import daylemk.xposed.xbridge.utils.Log;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
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
        if (sModuleRes == null) {
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

    /** get the XBridge package context to load resource from another process **/
    public static Context getXBridgeContext(Context context){
        Context mContext = null;
        try {
            mContext = context.createPackageContext(StaticData.THIS_PACKAGE_NAME,
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            XposedBridge.log(e);
        }
        Log.d(TAG, "the XBridge context: " + mContext);
        return mContext;
    }
}
