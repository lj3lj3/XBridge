package daylemk.xposed.xbridge;

import daylemk.xposed.xbridge.data.MainPreferences;
import daylemk.xposed.xbridge.data.StaticData;
import daylemk.xposed.xbridge.hook.AppInfoHook;
import daylemk.xposed.xbridge.hook.FrameworksHook;
import daylemk.xposed.xbridge.hook.RecentTaskHook;
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
 *          28-四月-2015 9:16:49
 */
public class XposedInit implements IXposedHookLoadPackage, IXposedHookZygoteInit,
        IXposedHookInitPackageResources {
    public static final String TAG = "XposedInit";
    public static boolean debuggable = false;

    private AppInfoHook appInfoHook;
    private StatusBarHook statusBarHook;
    private RecentTaskHook recentTaskHook;
    private FrameworksHook frameworksHook;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        Log.i(TAG, "init zygote");
        Log.i(TAG, "the package name: " + StaticData.THIS_PACKAGE_NAME);
        Log.i(TAG, "module path: " + startupParam.modulePath);

//        startupParam.modulePath
        // init the hook object first
        statusBarHook = new StatusBarHook();
        appInfoHook = new AppInfoHook();
        recentTaskHook = new RecentTaskHook();
        frameworksHook = new FrameworksHook();

        // call the initZyote method
        MainPreferences.initZygote(startupParam);
        statusBarHook.initZygote(startupParam);
        appInfoHook.initZygote(startupParam);
        recentTaskHook.initZygote(startupParam);
        frameworksHook.initZygote(startupParam);
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam
                                                   initPackageResourcesParam) throws Throwable {
//        Log.d(TAG, "handle package resource: " + initPackageResourcesParam.packageName);
        if(initPackageResourcesParam.packageName.equals(StaticData.PKG_NAME_FRAMEWORK)){
            frameworksHook.handleInitPackageResources(initPackageResourcesParam);
        }

    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws
            Throwable {
//        Log.i(TAG, "handle load package:" + loadPackageParam.packageName);
        if (loadPackageParam.packageName.equals(StaticData.PKG_NAME_SYSTEMUI)) {
            Log.i(TAG, "found systemui");
            statusBarHook.handleLoadPackage(loadPackageParam);
            recentTaskHook.handleLoadPackage(loadPackageParam);
            // add this for systemui process
            MainPreferences.loadPreference();
        } else if (loadPackageParam.packageName.equals(StaticData.PKG_NAME_SETTINGS)) {
            Log.i(TAG, "found settings");
            appInfoHook.handleLoadPackage(loadPackageParam);
            // add this for settings process
            MainPreferences.loadPreference();
        }

        //appInfoHook = new AppInfoHook();
        //appInfoHook.handleLoadPackage(loadPackageParam);

    }
}
