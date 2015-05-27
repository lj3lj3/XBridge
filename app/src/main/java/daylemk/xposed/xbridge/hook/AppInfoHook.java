package daylemk.xposed.xbridge.hook;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import daylemk.xposed.xbridge.action.Action;
import daylemk.xposed.xbridge.action.AppSettingsAction;
import daylemk.xposed.xbridge.action.ClipBoardAction;
import daylemk.xposed.xbridge.action.PlayAction;
import daylemk.xposed.xbridge.action.SearchAction;
import daylemk.xposed.xbridge.data.StaticData;
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
        final Class<?> installedAppDetailsClass = XposedHelpers.findClass(StaticData
                .PKG_NAME_SETTINGS +
                ".applications" +
                ".InstalledAppDetails", loadPackageParam.classLoader);
        Log.d(TAG, "installedAppDetailsClass: " + installedAppDetailsClass);
        XposedHelpers.findAndHookMethod(installedAppDetailsClass, "onCreateOptionsMenu",
                // add parameters type here
                Menu.class, MenuInflater.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (!Action.isActionsShowInAppInfo()) {
                            // do nothing
                            return;
                        }

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
                        final String pkgName = pkgInfo.packageName;
                        final Context context = activity.getApplicationContext();

                        // find the menu layout
                        Menu menu = (Menu) param.args[0];
                        Log.d(TAG, "the menu is: " + menu);

                        handleAllAction(menu, context, pkgName);
                    }
                });
    }

    private void handleAllAction(Menu menu, Context context, String pkgName) {
        if (PlayAction.isShow && PlayAction.isShowInAppInfo) {
            final Action action = new PlayAction();
            addMenuAndSetAction(menu, action, context, pkgName);
        }
        // app Ops already show in app info
                        /*if(AppOpsAction.isShowInAppInfo) {
                            final Action action = new AppOpsAction();
                            MenuItem playMenuItem = menu.add(action.getMenuTitle());
                            action.setAction(AppInfoHook.this, context,
                                    pkgName, playMenuItem);
                        }*/
        if (AppSettingsAction.isShow && AppSettingsAction.isShowInAppInfo) {
            final Action action = new AppSettingsAction();
            addMenuAndSetAction(menu, action, context, pkgName);
        }
        if (ClipBoardAction.isShow && ClipBoardAction.isShowInAppInfo) {
            final Action action = new ClipBoardAction();
            addMenuAndSetAction(menu, action, context, pkgName);
        }
        if (SearchAction.isShow && SearchAction.isShowInAppInfo) {
            final Action action = new SearchAction();
            addMenuAndSetAction(menu, action, context, pkgName);
        }
    }

    private void addMenuAndSetAction(Menu menu, Action action, Context context, String pkgName) {
        MenuItem xBridgeMenuItem = menu.add(action.getMenuTitle());
        action.setAction(AppInfoHook.this, context, pkgName, xBridgeMenuItem);
    }
}
