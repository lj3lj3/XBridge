package daylemk.xposed.xbridge.action;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.hook.AppInfoHook;
import daylemk.xposed.xbridge.hook.Hook;
import daylemk.xposed.xbridge.hook.StatusBarHook;
import daylemk.xposed.xbridge.utils.Log;
import daylemk.xposed.xbridge.utils.XBridgeToast;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * @author DayLemK
 * @version 1.0
 *          28-四月-2015 9:16:48
 */
public abstract class Action {
    public static final String TAG = "Action";

    /* the key should the sub class overwrite ------------begin */
    public static String keyShowInStatusBar;
    public static String keyShowInRecentTask;
    public static String keyShowInAppInfo;
    public static String keyShow;

    public static boolean showInStatusBarDefault = true;
    public static boolean showInRecentTaskDefault = true;
    public static boolean showInAppInfoDefault = true;
    public static boolean showDefault = true;

    public static boolean isShowInRecentTask = true;
    public static boolean isShowInStatusBar = true;
    public static boolean isShowInAppInfo = true;
    public static boolean isShow = true;
    /* the key should the sub class overwrite ------------end */
    /**
     * The map contain the action view id *
     */
    private static Map<Class<? extends Action>, Integer> viewIdMap = new HashMap<>();
    // not use this, 'cause we need read value at the begin of injection. so, the static boolean
    // value is faster.
//    private static Map<String, Boolean> prefItem;
//    private static Map<String, Map<String, Object>> prefMap = new HashMap();

    /**
     * load the key from the string resource
     *
     * @param sModRes the module resource of package
     */
    public static void loadPreferenceKeys(Resources sModRes) {
        PlayAction.loadPreferenceKeys(sModRes);
        AppOpsAction.loadPreferenceKeys(sModRes);
        AppSettingsAction.loadPreferenceKeys(sModRes);
        SearchAction.loadPreferenceKeys(sModRes);
        ClipBoardAction.loadPreferenceKeys(sModRes);
        ForceStopAction.loadPreferenceKeys(sModRes);
        Log.d(TAG, "load preference key done");
    }

    /**
     * load all action related preference
     */
    public static void loadPreference(SharedPreferences preferences) {
        PlayAction.loadPreference(preferences);
        AppOpsAction.loadPreference(preferences);
        AppSettingsAction.loadPreference(preferences);
        SearchAction.loadPreference(preferences);
        ClipBoardAction.loadPreference(preferences);
        ForceStopAction.loadPreference(preferences);
        Log.d(TAG, "load preference done");
    }

    public static boolean onReceiveNewValue(String key, String value) {
        if (key != null && value != null) {
            if (!(PlayAction.onReceiveNewValue(key, value) ||
                    AppOpsAction.onReceiveNewValue(key, value) ||
                    AppSettingsAction.onReceiveNewValue(key, value) ||
                    ClipBoardAction.onReceiveNewValue(key, value) ||
                    ForceStopAction.onReceiveNewValue(key, value) ||
                    SearchAction.onReceiveNewValue(key, value))) {
                Log.w(TAG, "key not found???");
                return false;
            }
        }
        return true;
    }

    public static boolean isActionsShowInAppInfo() {
        return PlayAction.isShow && PlayAction.isShowInAppInfo
                || AppOpsAction.isShow && AppOpsAction.isShowInAppInfo
                || AppSettingsAction.isShow && AppSettingsAction.isShowInAppInfo
                || SearchAction.isShow && SearchAction.isShowInAppInfo
                || ClipBoardAction.isShow && ClipBoardAction.isShowInAppInfo;

    }

    public static boolean isActionsShowInRecentTask() {
        return PlayAction.isShow && PlayAction.isShowInRecentTask
                || AppOpsAction.isShow && AppOpsAction.isShowInRecentTask
                || AppSettingsAction.isShow && AppSettingsAction.isShowInRecentTask
                || SearchAction.isShow && SearchAction.isShowInRecentTask
                || ClipBoardAction.isShow && ClipBoardAction.isShowInRecentTask;

    }

    public static boolean isActionsShowInStatusBar() {
        return PlayAction.isShow && PlayAction.isShowInStatusBar
                || AppOpsAction.isShow && AppOpsAction.isShowInStatusBar
                || AppSettingsAction.isShow && AppSettingsAction.isShowInStatusBar
                || SearchAction.isShow && SearchAction.isShowInStatusBar
                || ClipBoardAction.isShow && ClipBoardAction.isShowInStatusBar;

    }

    public static boolean isNeed2Add(ViewGroup viewGroup, Class<? extends Action> actionClass) {
        Log.d(TAG, "the check map class is: " + actionClass);
        if (viewIdMap.containsKey(actionClass)) {
            // the key is here
            int viewId = viewIdMap.get(actionClass);
            if (viewGroup.findViewById(viewId) != null) {
                // the view is here too
                // no need to add
                Log.d(TAG, "this view is already added");
                return false;
            } else {
                // this layout does not contain this view, need to add it
                return true;
            }
        } else {
            // the id is not here, so we generate it and put it in the map
            viewIdMap.put(actionClass, View.generateViewId());
            // --------- debug map content begin ---------
            Log.d(TAG, "map,keys: " + viewIdMap.keySet());
            Set<Class<? extends Action>> keySet = viewIdMap.keySet();
            Iterator<Class<? extends Action>> iterator = keySet.iterator();
            Class<? extends Action> key;
            int value;
            StringBuilder sb = new StringBuilder();
            while (iterator.hasNext()) {
                key = iterator.next();
                value = viewIdMap.get(key);
                sb.append(key).append(" - ").append(value).append(",");
            }
            Log.d(TAG, "map: " + sb.toString());
            // --------- debug map content end ---------
            return true;
        }
    }

    /**
     * get the view id of this action, we need make it public, so we can get the previous view
     * from layout
     */
    public static int getViewId(Class<? extends Action> actionClass) {
        if (!viewIdMap.containsKey(actionClass)) {
            Log.w(TAG, "the map doesn't contain: " + actionClass);
        }

        return viewIdMap.get(actionClass);
    }

    protected Drawable getPackageIcon(PackageManager packageManager, String pkgName) {
        Drawable drawable;
        try {
            drawable = packageManager.getApplicationIcon(pkgName);
            Log.d(TAG, "play store icon is found: " + drawable);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "play store icon can't be found, use generic icon");
            // app is gone, just show package name and generic icon
            drawable = packageManager.getDefaultActivityIcon();
        }

        return drawable;
    }

    private Intent getFinalIntent(Hook hook, Context context, String pkgName) {
        Intent intent = getIntent(hook, context, pkgName);
        // this is just need for appInfo screen for now, not in status bar
        if (intent != null && hook instanceof AppInfoHook) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        }
        Log.d(TAG, "final intent: " + intent);
        return intent;
    }


    /**
     * subclass should overwrite this method or handleData method
     * this method will be called first, if the result is null, will call handleData method
     * EDIT: add Context parameter
     */
    protected abstract Intent getIntent(Hook hook, Context context, String pkgName);

    /**
     * subclass should overwrite this method or getIntent method
     * EDIT: this class is need to set public to call out of the action
     */
    public abstract void handleData(Context context, String pkgName);

    /**
     * get the icon of this action
     */
    protected abstract Drawable getIcon(PackageManager packageManager);

    /**
     * get the String that represent this action, overwrite this in the subclass
     */
    public abstract String getMenuTitle();

    public void setAction(final Hook hook, final Context context,
                          final String pkgName,
                          View view) {
//        super.setAction(hook, context, pkgName, imageButton);
        PackageManager packageManager = context.getPackageManager();
        if (view instanceof ImageButton) {
            ((ImageButton) view).setImageDrawable(getIcon(packageManager));
        } else if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(getIcon(packageManager));
        }
        view.setContentDescription(getMenuTitle());
        // set the button id first
        // the button id can be the same within the different notification
        view.setId(getViewId(Action.this.getClass()));
        // set the package name as tag of this view
        view.setTag(pkgName);
        Log.d(TAG, "set click action, pkg: " + pkgName);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "on click action is + " + Action.this.getClass() + ", hook: " + hook);
                Intent intent = getFinalIntent(hook, context, pkgName);
                if (intent == null) {
                    // if the intent is null, we need to call handleData method
                    handleData(context, pkgName);
                } else if (hook instanceof StatusBarHook) {
                    // dismiss the keyguard adn collapse panels
                    ((StatusBarHook) hook).dismissKeyguardAndStartIntent(Action.this, intent,
                            pkgName);
                } else {
                    // just start activity as user
                    startIntentAsUser(context, intent, pkgName);
//                    context.startActivity(intent);
                }

                Log.d(TAG, "play action done");
            }
        });
    }

    public void setAction(final Hook hook, final Context context,
                          final String pkgName,
                          MenuItem menuItem) {
        // set the show flag
        // EDIT: set to always show
        menuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        // set the icon
        menuItem.setIcon(getIcon(context.getPackageManager()));
        Log.d(TAG, "set click action, pkg: " + pkgName);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "on click action is: " + Action.this.getClass() + ", hook: " + hook);
                Intent intent = getFinalIntent(hook, context, pkgName);
                if (intent == null) {
                    // if the intent is null, we need to call handleData method
                    handleData(context, pkgName);
                } else if (hook instanceof AppInfoHook) {
                    try {
                        // no need to start as user, 'cause when the appInfo screen is called, it
                        // already signed to a user
                        context.startActivity(intent);
                    } catch (Exception e) {
                        XposedBridge.log(e);
                        Log.e(TAG, "start intent error, intent: " + intent);

                        XBridgeToast.showToast(context, Hook.getXBridgeContext(context).getString
                                (R.string.error) + intent.getComponent().getPackageName());
                    }
                }

                Log.d(TAG, "menu action done");
                return true;
            }
        });
    }

    /**
     * start intent as user
     *
     * @param context context
     * @param intent  the Action intent
     * @param pkgName clicked package name
     */
    public void startIntentAsUser(Context context, Intent intent, String pkgName) {
        startIntentAsUser(context, intent, getUid(context, pkgName));
    }

    public void startIntentAsUser(final Context context, Intent intent, int appUid) {
        Log.d(TAG, "start intent as user, appUid: " + appUid + ", " + intent);
//        List<ResolveInfo> infoList = context.getPackageManager().queryIntentActivities
//                (intent, PackageManager
//                .MATCH_DEFAULT_ONLY);
//        if(infoList.size() == 0) {
//            throw new Exception();
//        }
        TaskStackBuilder taskStackBuilder;
        try {
            // TODO: get ride of TaskStackBuilder ???
            // if the target package is no available, this will throw a exception
            taskStackBuilder = TaskStackBuilder.create
                    (context).addNextIntentWithParentStack
                    (intent);
        } catch (Exception e) {
            XposedBridge.log(e);
            Log.e(TAG, "start intent as user error, intent: " + intent);
            // show toast
            XBridgeToast.showToastOnHandler(context, Hook.getXBridgeContext(context).getString
                    (R.string.error) + intent.getComponent().getPackageName());
            return;
        }
        Log.d(TAG, "taskStackBuilder: " + taskStackBuilder);

        int userId = (int) XposedHelpers.callStaticMethod(UserHandle
                .class, "getUserId", appUid);
        Log.d(TAG, "userId: " + userId);

        Object userHandle = null;
        try {
            // here should use int.class
            Constructor<?> userHandleConstructor = UserHandle.class
                    .getDeclaredConstructor(int.class);
            userHandleConstructor.setAccessible(true);
            userHandle = userHandleConstructor.newInstance(userId);
        } catch (Exception e) {
            e.printStackTrace();
            XposedBridge.log(e);
        }
        Log.d(TAG, "userHandle: " + userHandle);

        Log.d(TAG, "start activities");
        // add try catch
        try {
            // call startActivities method
            // FIXED: should use the empty Bundle object not null
            XposedHelpers.callMethod(taskStackBuilder, "startActivities",
                    new Bundle(), userHandle);
        } catch (Exception e) {
            XposedBridge.log(e);
            Log.e(TAG, "start intent as user error, intent: " + intent);
            // show toast
            XBridgeToast.showToastOnHandler(context, Hook.getXBridgeContext(context).getString
                    (R.string.error) + intent.getData());
        }
    }

    private int getUid(Context context, String pkgName) {
        // get the appUid
        int appUid = -1;
        try {
            final ApplicationInfo info = context.getPackageManager().getApplicationInfo(pkgName,
                    PackageManager.GET_UNINSTALLED_PACKAGES
                            | PackageManager.GET_DISABLED_COMPONENTS);
            if (info != null) {
                appUid = info.uid;
                Log.d(TAG, "uid is: " + appUid + ", appInfo: " + info);
            }
        } catch (Exception e) {
            // output log
            XposedBridge.log(e);
        }
        return appUid;
    }
}