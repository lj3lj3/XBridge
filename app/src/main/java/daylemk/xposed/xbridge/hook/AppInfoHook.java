package daylemk.xposed.xbridge.hook;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import daylemk.xposed.xbridge.action.PlayAction;
import daylemk.xposed.xbridge.utils.Log;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * App info screen hook
 * Created by DayLemK on 2015/4/28.
 */
public class AppInfoHook extends Hook {
    public static final String TAG = "AppInfoHook";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws
            Throwable {
        Log.d(TAG, "enter app info hook");
        final Class<?> installedAppDetailsClass = XposedHelpers.findClass("com.android.settings" +
                ".applications" +
                ".InstalledAppDetails", loadPackageParam.classLoader);
        Log.d(TAG, "installedAppDetailsClass: " + installedAppDetailsClass);
        XposedHelpers.findAndHookMethod(installedAppDetailsClass, "onCreateOptionsMenu",
                // add parameters type here
                Menu.class, MenuInflater.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.d(TAG, "after onCreateOptionsMenu hooked");
                        super.afterHookedMethod(param);
                        final Object installedAppDetails = param.thisObject;
                        // get the package manager
                        PackageManager pm = (PackageManager) XposedHelpers.getObjectField
                                (installedAppDetails, "mPm");
                        final PackageInfo pkgInfo = (PackageInfo) XposedHelpers.getObjectField
                                (installedAppDetails,
                                        "mPackageInfo");
                        final Activity activity = (Activity) XposedHelpers.callMethod
                                (installedAppDetails,
                                        "getActivity");

                        // find the menu layout
                        Menu menu = (Menu) param.args[0];
                        Log.d(TAG, "the menu is: " + menu);
                        final PlayAction playAction = new PlayAction();
                        MenuItem playMenuItem = menu.add(playAction.getMenuTitle());
                        playAction.setAction(AppInfoHook.this, activity.getApplicationContext(),
                                pkgInfo.packageName, playMenuItem);
                    }
                });
    }
}
