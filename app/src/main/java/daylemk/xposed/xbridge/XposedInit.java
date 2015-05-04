package daylemk.xposed.xbridge;

import daylemk.xposed.xbridge.data.StaticData;
import daylemk.xposed.xbridge.hook.AppInfoHook;
import daylemk.xposed.xbridge.hook.StatusBarHook;
import daylemk.xposed.xbridge.utils.Log;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author DayLemK
 * @version 1.0
 * 28-四月-2015 9:16:49
 */
public class XposedInit implements IXposedHookLoadPackage, IXposedHookZygoteInit,
        IXposedHookInitPackageResources {
    public static final String TAG = "XposedInit";
    public static boolean debuggable = false;

    private AppInfoHook appInfoHook;
    private StatusBarHook statusBarHook;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        Log.d(TAG, "init zygote");
        Log.d(TAG, "the package name: " + StaticData.THIS_PACKAGE_NAME);
//        startupParam.modulePath
        // init the hook object first
        statusBarHook = new StatusBarHook();
        appInfoHook = new AppInfoHook();

        // call the initZyote method
        statusBarHook.initZygote(startupParam);
        appInfoHook.initZygote(startupParam);
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam initPackageResourcesParam) throws Throwable {
//        Log.d(TAG, "handle package resource: " + initPackageResourcesParam.packageName);
//        if(initPackageResourcesParam.packageName.equals("daylemk.xposed.xbridge")){
//            initPackageResourcesParam.res.
//        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        Log.d(TAG, "handle load package:" + loadPackageParam.packageName);
        if(loadPackageParam.packageName.equals("com.android.systemui")){
            Log.d(TAG, "found systemui");
            statusBarHook.handleLoadPackage(loadPackageParam);
        } else if(loadPackageParam.packageName.equals("com.android.settings")){
            Log.d(TAG, "found settings");
            appInfoHook.handleLoadPackage(loadPackageParam);
        }

        //appInfoHook = new AppInfoHook();
        //appInfoHook.handleLoadPackage(loadPackageParam);

    }
}
