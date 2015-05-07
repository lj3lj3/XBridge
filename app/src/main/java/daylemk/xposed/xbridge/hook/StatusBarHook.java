package daylemk.xposed.xbridge.hook;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.XposedInit;
import daylemk.xposed.xbridge.action.Action;
import daylemk.xposed.xbridge.action.PlayAction;
import daylemk.xposed.xbridge.data.StaticData;
import daylemk.xposed.xbridge.utils.Log;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Status bar hook
 * Created by DayLemK on 2015/4/30.
 */
public class StatusBarHook extends Hook {
    public static final String TAG = "StatusBarHook";
    // TODO: need get this value from original class
    public static final int FLAG_EXCLUDE_NONE = 0;

    // set the field and we can use it later to collapse notification or unlock screen
    private Object statusBarObject = null;
    private Context context = null;
    private PackageManager packageManager = null;
    // the OnDismissActionInterface
    private Class<?> onDismissActionInterface = null;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        super.initZygote(startupParam);

//        inspectItemButton = sModuleRes.fwd(R.layout.notification_inspect_item);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws
            Throwable {
        Log.v(TAG, "enter the status bar hook");
        final Class<?> baseStatusBarClass = XposedHelpers.findClass("com.android.systemui" +
                ".statusbar.BaseStatusBar", loadPackageParam.classLoader);
        Log.d(TAG, "BaseStatusBar: " + baseStatusBarClass);
        XposedBridge.hookAllMethods(baseStatusBarClass, "inflateGuts", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.v(TAG, "after inflateGuts method hooked");
                super.afterHookedMethod(param);
                // set the statusBar everytime
                // TODO: needed? when system ui crash, this code will be cleaned too
                if (!param.thisObject.equals(statusBarObject)) {
                    Log.w(TAG, "statusBar is different: " + statusBarObject);
                    statusBarObject = param.thisObject;
                    context = (Context) XposedHelpers.getObjectField
                            (statusBarObject, "mContext");
                    Log.d(TAG, "context: " + context);
                }

                // 1) get the notification guts id
                // ignore: and inspect item id
                // EDIT: get guts view here
                final Resources res = context.getResources();
                final int idGuts = res.getIdentifier("notification_guts", "id",
                        StaticData.PKG_NAME_SYSTEMUI);
                Log.d(TAG, "guts id: " + idGuts);
                // move get guts view here, so we can check if the action is already injected or not
                final Object expandNotiRowObject = param.args[0];
                Log.d(TAG, "expandNotiRowObject: " + expandNotiRowObject);
                final FrameLayout layoutGuts = (FrameLayout) XposedHelpers.callMethod
                        (expandNotiRowObject, "findViewById", idGuts);
                Log.d(TAG, "get the guts view: " + layoutGuts);
                Log.d(TAG, "guts children number: " + layoutGuts.getChildCount());
                // check if the view already exists
                // EDIT: we should use the dynamic generated id, 'cause that will not be equal
                // with another package static id

                // check if need to add action
                // TODO: add the preference check
                if (!PlayAction.isNeed2Add(layoutGuts, PlayAction.class)) {
                    Log.d(TAG, "play action alread added");
                    return;
                }


                final int idInspectItemId = res.getIdentifier("notification_inspect_item", "id",
                        StaticData.PKG_NAME_SYSTEMUI);
                Log.d(TAG, "inspect item id: " + idInspectItemId);

                // 2) get the guts view and inspect item button view
                final ImageButton imageButtonInspect = (ImageButton) layoutGuts.findViewById
                        (idInspectItemId);
                Log.d(TAG, "the inspect image button: " + imageButtonInspect);

                // 3) get the package name
                Object statusBarNotificationObject = XposedHelpers.callMethod(expandNotiRowObject,
                        "getStatusBarNotification");
                Log.d(TAG, "statusBarNotificationObject: " + statusBarNotificationObject);
                final String pkgName = (String) XposedHelpers.callMethod
                        (statusBarNotificationObject, "getPackageName");
                Log.i(TAG, "package name: " + pkgName);

                // 4) init the XBridge button
                // the first child is a linearLayout in the aosp
                final LinearLayout linearLayout = (LinearLayout) layoutGuts.getChildAt(0);
                Log.d(TAG, "linear layout: " + linearLayout);
                // inflate layout from xBridge package name
                // EDIT: finally, this can be done
                Context xBridgeContext = Hook.getXBridgeContext(context);
                ImageButton xBridgeButton = (ImageButton) LayoutInflater.from(xBridgeContext)
                        .inflate(R.layout
                        .notification_inspect_item, null);

                Log.d(TAG, "inflate image button: " + xBridgeButton);

                // set the style finally!!!
                // EDIT: set the style on the fly acts wired

                // copy the params from the inspect item button
                ViewGroup.LayoutParams layoutParams = imageButtonInspect.getLayoutParams();
                Log.d(TAG, "image button inspect: " + layoutParams.toString());
                xBridgeButton.setLayoutParams(layoutParams);

                // get package manager
                final int userId = (int) XposedHelpers.callMethod(XposedHelpers.callMethod
                        (statusBarNotificationObject, "getUser"), "getIdentifier");
                Log.d(TAG, "user id: " + userId);
                packageManager = (PackageManager) XposedHelpers.callMethod
                        (statusBarObject,
                                "getPackageManagerForUser",
                                userId);
                Log.d(TAG, "packageManager: " + packageManager);

                // init play action for demo
                Action action = new PlayAction();
                // set the action event
                action.setAction(StatusBarHook.this, context, pkgName, xBridgeButton);

                // 5) add the button to the guts layout
                linearLayout.addView(xBridgeButton);
            }
        });

        onDismissActionInterface = XposedHelpers.findClass("com.android.keyguard" +
                ".KeyguardHostView.OnDismissAction", loadPackageParam.classLoader);
        Log.d(TAG, "onDismissActionInterface: " + onDismissActionInterface);

    }

    public void dismissKeyguardAndStartIntent(final Action action, final Intent intent, final String pkgName) {
        if (statusBarObject == null) {
            Log.e(TAG, "baseStatusBar is null, give up");
            return;
        }
        Log.i(TAG, "dismiss keyguard");

        // dismiss and start intent
        // EDIT: super abstract private method can't be called?
//        Method method;
//        try {
//            method = statusBarObject.getClass().getSuperclass().getDeclaredMethod(
//                    ("startNotificationGutsIntent", Intent.class, Integer.class);
//            Log.d(TAG, "the method: " + method);
//            method.setAccessible(true);
//            method.invoke(statusBarObject, intent, appUid);
//        } catch (Exception e) {
//            XposedBridge.log(e);
//        }

        Object mStatusBarKeyguardViewManager = XposedHelpers.
                getObjectField(statusBarObject, "mStatusBarKeyguardViewManager");
        Log.d(TAG, "get the keyguardViewManager: " + mStatusBarKeyguardViewManager);
        final boolean keyguardShowing = (boolean) XposedHelpers.callMethod
                (mStatusBarKeyguardViewManager, "isShowing");
        Log.d(TAG, "keyguardShowing: " + keyguardShowing);

        Object onDismissAction = Proxy.newProxyInstance(onDismissActionInterface.getClassLoader()
                , new Class<?>[]{onDismissActionInterface}, new InvocationHandler() {

            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                Log.d(TAG, "invoke: " + method.getName());
                if (method.getName().equals("onDismiss")) {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "keyguardShowing: " + keyguardShowing);
                            if (keyguardShowing) {
                                Class<?> activityManagerNativeClass = XposedHelpers.findClass
                                        ("android.app.ActivityManagerNative",
                                                statusBarObject.getClass().getClassLoader());
                                Log.d(TAG, "activityManagerNativeClass: " +
                                        activityManagerNativeClass);
                                if (activityManagerNativeClass != null) {
                                    Object defaultNative = XposedHelpers.callStaticMethod
                                            (activityManagerNativeClass, "getDefault");
                                    Log.d(TAG, "defaultNative: " + defaultNative);
                                    // call keyguardWaitingForActivityDrawn method
                                    XposedHelpers.callMethod(defaultNative,
                                            "keyguardWaitingForActivityDrawn");
                                }
                            }
                            // end of keyguard showing

                            action.startIntentAsUser(context, intent, pkgName);

                        }
                    });
                    Log.d(TAG, "collapse panels");
                    XposedHelpers.callMethod(statusBarObject, "animateCollapsePanels",
                            FLAG_EXCLUDE_NONE,
                            true /* force */);
                    return true;
                } else if (method.getName().equals("toString")) {
                    //TODO: invoke original method
                    return o.getClass().toString();
                }
                // else invoke the original method
                return method.invoke(o, objects);
            }
        });
        Log.d(TAG, "onDismissAction: " + onDismissAction);

        XposedHelpers.callMethod(statusBarObject, "dismissKeyguardThenExecute", onDismissAction,
                false/* afterKeyguardGone */);
    }

}
