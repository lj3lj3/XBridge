package daylemk.xposed.xbridge.hook;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import daylemk.xposed.xbridge.R;
import daylemk.xposed.xbridge.action.Action;
import daylemk.xposed.xbridge.action.AppInfoAction;
import daylemk.xposed.xbridge.action.AppOpsAction;
import daylemk.xposed.xbridge.action.AppSettingsAction;
import daylemk.xposed.xbridge.action.ClipBoardAction;
import daylemk.xposed.xbridge.action.ForceStopAction;
import daylemk.xposed.xbridge.action.LightningWallAction;
import daylemk.xposed.xbridge.action.MyAndroidToolsAction;
import daylemk.xposed.xbridge.action.NotifyCleanAction;
import daylemk.xposed.xbridge.action.PlayAction;
import daylemk.xposed.xbridge.action.SearchAction;
import daylemk.xposed.xbridge.action.XHaloFloatingWindowAction;
import daylemk.xposed.xbridge.action.XPrivacyAction;
import daylemk.xposed.xbridge.data.StaticData;
import daylemk.xposed.xbridge.utils.Log;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Recent task screen hook
 * Created by DayLemK on 2015/5/4.
 */
public class RecentTaskHook extends Hook {
    public static final String TAG = "RecentTaskHook";
    // animation time
    public static final int TIME_ANIMATION = 400;
    // the guts view id
    private static final int ID_GUTS = View.generateViewId();
    // the header icon id
    private int idIcon = -1;
    // the header desc id
    private int idDesc = -1;
    // the dismiss button id
    private int idDismiss = -1;
    /**
     * the header id of this task view, we use this to create a clone one
     */
    private int viewHeaderId = -1;
    /**
     * button background id, 'cause we need set every button a drawable
     */
    private int buttonBgId = -1;
    private FrameLayout.LayoutParams headerViewLayoutParams;
    private FrameLayout.LayoutParams dismissViewLayoutParams;

    private Constructor<?> fixedSizeImageViewConstructor;

    private HeaderViewTouchListener listener;
    // animation interpolator
    private Interpolator interpolator = new AccelerateDecelerateInterpolator();

    private Interpolator fastOutSlowInInterpolator;
    private Interpolator fastOutLinearInInterpolator;
    private int taskBarExitAnimDuration = 0;
    private int taskBarEnterAnimDuration = 0;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        super.initZygote(startupParam);
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws
            Throwable {
        super.handleLoadPackage(loadPackageParam);
        Class<?> taskViewHeaderClass = XposedHelpers.findClass(StaticData.PKG_NAME_SYSTEMUI + "" +
                        ".recents" +
                        ".views" +
                        ".TaskViewHeader",
                loadPackageParam.classLoader);
        Log.d(TAG, "task view header class: " + taskViewHeaderClass);
        XposedHelpers.findAndHookMethod(taskViewHeaderClass, "onFinishInflate", new XC_MethodHook
                () {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.d(TAG, "onFinishInflate hook");
                if (XHaloFloatingWindowAction.isShow && XHaloFloatingWindowAction
                        .isShowInRecentTask) {
                    FrameLayout taskViewHeader = (FrameLayout) param.thisObject;
                    Context context = taskViewHeader.getContext();
                    Resources res = context.getResources();
                    // call this two
                    initViewIds(res);
                    getLayoutParams(taskViewHeader);
                    // the xbridge view position is 1, right to the dismiss button
                    View xBridgeView = getXBridgeView(context, res, loadPackageParam.classLoader,
                            1);
                    xBridgeView.setId(Action.getViewId(XHaloFloatingWindowAction.class));
                    // set the button to invisible
                    xBridgeView.setVisibility(View.INVISIBLE);
                    taskViewHeader.addView(xBridgeView);
                }
            }
        });

        XposedHelpers.findAndHookMethod(taskViewHeaderClass, "startLaunchTaskDismissAnimation",
                new XC_MethodHook
                        () {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.d(TAG, "startLaunchTaskDismissAnimation hook");
                        if (XHaloFloatingWindowAction.isShow && XHaloFloatingWindowAction
                                .isShowInRecentTask) {
                            FrameLayout taskViewHeader = (FrameLayout) param.thisObject;
                            View xHalo = taskViewHeader.findViewById(Action.getViewId
                                    (XHaloFloatingWindowAction.class));
                            if (xHalo != null) {
                                if (xHalo.getVisibility() == View.VISIBLE) {
                                    Object mConfig = XposedHelpers.getObjectField(taskViewHeader,
                                            "mConfig");
                                    if (mConfig != null) {
                                        if (fastOutSlowInInterpolator == null) {
                                            fastOutSlowInInterpolator = (Interpolator)
                                                    XposedHelpers.getObjectField(mConfig,
                                                            "fastOutSlowInInterpolator");
                                        }
                                        if (taskBarExitAnimDuration == 0) {
                                            taskBarExitAnimDuration = XposedHelpers.getIntField
                                                    (mConfig, "taskBarExitAnimDuration");
                                        }
                                    } else {
                                        Log.w(TAG, "the mConfig is null???");
                                    }
                                    xHalo.animate().cancel();
                                    xHalo.animate()
                                            .alpha(0f)
                                            .setStartDelay(0)
                                            .setInterpolator(fastOutSlowInInterpolator)
                                            .setDuration(taskBarExitAnimDuration)
                                            .withLayer()
                                            .start();
                                }
                            } else {
                                Log.w(TAG, "the xhalo button is null??? at " +
                                        "startLaunchTaskDismissAnimation");
                            }
                        }
                    }
                });

        XposedHelpers.findAndHookMethod(taskViewHeaderClass, "startNoUserInteractionAnimation",
                new XC_MethodHook
                        () {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.d(TAG, "startNoUserInteractionAnimation hook");
                        if (XHaloFloatingWindowAction.isShow && XHaloFloatingWindowAction
                                .isShowInRecentTask) {
                            FrameLayout taskViewHeader = (FrameLayout) param.thisObject;
                            View xHalo = taskViewHeader.findViewById(Action.getViewId
                                    (XHaloFloatingWindowAction.class));
                            if (xHalo != null) {
                                Object mConfig = XposedHelpers.getObjectField(taskViewHeader,
                                        "mConfig");
                                if (mConfig != null) {
                                    if (fastOutLinearInInterpolator == null) {
                                        fastOutLinearInInterpolator = (Interpolator)
                                                XposedHelpers.getObjectField(mConfig,
                                                        "fastOutLinearInInterpolator");
                                    }
                                    if (taskBarEnterAnimDuration == 0) {
                                        taskBarEnterAnimDuration = XposedHelpers.getIntField
                                                (mConfig, "taskBarEnterAnimDuration");
                                    }
                                } else {
                                    Log.w(TAG, "the mConfig is null???");
                                }
                                xHalo.setVisibility(View.VISIBLE);
                                xHalo.setAlpha(0f);
                                xHalo.animate()
                                        .alpha(1f)
                                        .setStartDelay(0)
                                        .setInterpolator(fastOutLinearInInterpolator)
                                        .setDuration(taskBarEnterAnimDuration)
                                        .withLayer()
                                        .start();
                            } else {
                                Log.w(TAG, "the xhalo button is null??? at " +
                                        "startNoUserInteractionAnimation");
                            }
                        }
                    }
                });

        XposedHelpers.findAndHookMethod(taskViewHeaderClass, "setNoUserInteractionState",
                new XC_MethodHook
                        () {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.d(TAG, "startNoUserInteractionAnimation hook");
                        if (XHaloFloatingWindowAction.isShow && XHaloFloatingWindowAction
                                .isShowInRecentTask) {
                            FrameLayout taskViewHeader = (FrameLayout) param.thisObject;
                            View xHalo = taskViewHeader.findViewById(Action.getViewId
                                    (XHaloFloatingWindowAction.class));
                            if (xHalo != null) {
                                if (xHalo.getVisibility() != View.VISIBLE) {
                                    xHalo.animate().cancel();
                                    xHalo.setVisibility(View.VISIBLE);
                                    xHalo.setAlpha(1f);
                                }
                            } else {
                                Log.w(TAG, "the xhalo button is null??? at " +
                                        "setNoUserInteractionState");
                            }
                        }
                    }
                });

        Class<?> taskViewClass = XposedHelpers.findClass(StaticData.PKG_NAME_SYSTEMUI + ".recents" +
                        ".views" +
                        ".TaskView",
                loadPackageParam.classLoader);
        Log.d(TAG, "task view class: " + taskViewClass);
        // hook the reset method, set the dismiss button to visible if ForceStopAction
        // .isShowDismissButtonNow is true on lollipop mr1
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            XposedHelpers.findAndHookMethod(taskViewClass, "reset", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Log.d(TAG, "reset method hook");
                    if (ForceStopAction.isShowDismissButtonNow || (XHaloFloatingWindowAction
                            .isShow && XHaloFloatingWindowAction
                            .isShowButtonNow)) {
                        FrameLayout taskViewObject = (FrameLayout) param.thisObject;
                        View mHeaderView = (View) XposedHelpers.getObjectField(taskViewObject,
                                "mHeaderView");
                        if (mHeaderView != null) {
                            if (ForceStopAction.isShowDismissButtonNow) {
                                View dismissView = mHeaderView.findViewById(idDismiss);
                                if (dismissView != null) {
                                    Log.d(TAG, "dismiss view after reset visible:" + dismissView
                                            .getVisibility());

                                    dismissView.setVisibility(View.VISIBLE);
                                } else {
                                    Log.d(TAG, "dismiss view is null at reset");
                                }
                            }
                            if (XHaloFloatingWindowAction.isShow && XHaloFloatingWindowAction
                                    .isShowButtonNow) {
                                View xhalo = mHeaderView.findViewById(Action.getViewId
                                        (XHaloFloatingWindowAction.class));
                                if (xhalo != null) {
                                    Log.d(TAG, "xhalo after reset visible:" + xhalo.getVisibility
                                            ());

                                    xhalo.setVisibility(View.VISIBLE);
                                } else {
                                    Log.d(TAG, "dismiss view is null at reset");
                                }
                            }
                        } else {
                            Log.d(TAG, "mHeaderView is null at reset");
                        }
                    }
                }
            });
        }

        // set the long press listener
        // BUT we can't set the view back, 'cause the data maybe different due to data recycle
        XposedHelpers.findAndHookMethod(taskViewClass, "onTaskDataLoaded", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                boolean isRecentShow = false;
                if (Action.isActionsShowInRecentTask()) {
                    isRecentShow = true;
                }
                // add XHaloFloatingWindowAction.isShow in this
                if (!(isRecentShow || ForceStopAction.isShow || ForceStopAction
                        .isShowDismissButtonNow || (XHaloFloatingWindowAction.isShow &&
                        XHaloFloatingWindowAction.isShowButtonNow))) {
                    // the recent and force stop action is not active
                    // call the original method
                    param.getResult();
                    return;
                }

                super.beforeHookedMethod(param);
                // get this every time???
                FrameLayout taskViewObject = (FrameLayout) param.thisObject;
                ViewGroup mHeaderView = (ViewGroup) XposedHelpers.getObjectField(taskViewObject,
                        "mHeaderView");
                if (isRecentShow && mHeaderView != null) {
                    // set on click listener, 'cause we use long click listener, so the touch
                    // event will pass to this view and not pass through it
                    mHeaderView.setOnClickListener((View.OnClickListener) taskViewObject);
                    mHeaderView.setOnLongClickListener((View.OnLongClickListener) taskViewObject);
                    if (listener == null) {
                        listener = new HeaderViewTouchListener();
                    }
                    // set on touch listener
                    mHeaderView.setOnTouchListener(listener);
                }
                View dismissView = null;
                // check if we should use force stop action
                if (ForceStopAction.isShow && mHeaderView != null) {
                    // get the view ids
                    initViewIds(taskViewObject.getResources());
                    dismissView = mHeaderView.findViewById(idDismiss);
                    dismissView.setOnLongClickListener((View.OnLongClickListener) taskViewObject);
                }
                if (ForceStopAction.isShowDismissButtonNow && mHeaderView != null) {
                    Log.d(TAG, "show dismiss button now");
                    // get the view ids
                    initViewIds(taskViewObject.getResources());
                    if (dismissView == null) {
                        dismissView = mHeaderView.findViewById(idDismiss);
                    }
                    if (dismissView != null) {
                        dismissView.setVisibility(View.VISIBLE);
                    }
                }
                if (XHaloFloatingWindowAction.isShow && XHaloFloatingWindowAction
                        .isShowInRecentTask && mHeaderView != null) {
                    Action action = new XHaloFloatingWindowAction();
                    Intent baseIntent = getTaskBaseIntent(taskViewObject);
                    resetAction(taskViewObject.getContext(), action, mHeaderView, getPackageName
                            (baseIntent), baseIntent);
                }
                if (XHaloFloatingWindowAction.isShow && XHaloFloatingWindowAction.isShowButtonNow
                        && mHeaderView != null) {
                    Log.d(TAG, "show xhalo button now");
                    View xHaloView = mHeaderView.findViewById(Action.getViewId
                            (XHaloFloatingWindowAction.class));
                    if (xHaloView != null) {
                        xHaloView.setVisibility(View.VISIBLE);
                    }
                }

                // call the original method
                param.getResult();
            }
        });
        // unset the long press listener and unset the view
        // this is a data recycler
        XposedHelpers.findAndHookMethod(taskViewClass, "onTaskDataUnloaded", new
                XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws
                            Throwable {
                        boolean isRecentShow = false;
                        if (Action.isActionsShowInRecentTask()) {
                            isRecentShow = true;
                        }
                        if (!(isRecentShow || ForceStopAction.isShow ||
                                (XHaloFloatingWindowAction.isShow && XHaloFloatingWindowAction
                                        .isShowInRecentTask))) {
                            // the recent and force stop action is not active
                            // call the original method
                            param.getResult();
                            return;
                        }

                        super.beforeHookedMethod(param);
                        FrameLayout taskViewObject = (FrameLayout) param.thisObject;
                        View mHeaderView = (View) XposedHelpers.getObjectField
                                (taskViewObject,
                                        "mHeaderView");
                        if (isRecentShow && mHeaderView != null) {
                            mHeaderView.setOnClickListener(null);
                            mHeaderView.setOnLongClickListener(null);
                            // set the on touch listener to null
                            mHeaderView.setOnTouchListener(null);

                            // EDIT: need to set the guts to invisible
                            View gutsView = ((ViewGroup) mHeaderView.getParent())
                                    .findViewById(ID_GUTS);
                            Log.d(TAG, "the guts view on unloaded is: " + gutsView);
                            if (gutsView != null) {
                                gutsView.setVisibility(View.INVISIBLE);
                            }
                        }

                        // if force stop action is show, set null
                        if (ForceStopAction.isShow && mHeaderView != null) {
                            mHeaderView.findViewById(idDismiss).setOnLongClickListener(null);
                        }

                        if (XHaloFloatingWindowAction.isShow && XHaloFloatingWindowAction
                                .isShowInRecentTask && mHeaderView != null) {
                            ImageView xHaloView = (ImageView) mHeaderView.findViewById(Action
                                    .getViewId
                                            (XHaloFloatingWindowAction
                                                    .class));
                            // reset parameters
                            xHaloView.setImageDrawable(null);
                            xHaloView.setOnClickListener(null);
                            xHaloView.setContentDescription(null);
                        }

                        // call the original method
                        param.getResult();
                    }
                });

        // hook onClick to handle the header view click event
        XposedHelpers.findAndHookMethod(taskViewClass, "onClick", View.class, new
                XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws
                            Throwable {
                        if (!Action.isActionsShowInRecentTask()) {
                            return;
                        }
                        View clickedView = (View) param.args[0];
                        Log.d(TAG, "onClick:" + clickedView);
                        final FrameLayout taskViewObject = (FrameLayout) param.thisObject;
                        final View mHeaderView = (View) XposedHelpers.getObjectField
                                (taskViewObject,
                                        "mHeaderView");
                        if (clickedView == mHeaderView) {
                            Log.d(TAG, "clicked header view, start task now");
                            Object cb = XposedHelpers.getObjectField(taskViewObject, "mCb");
                            if (cb == null) {
                                Log.e(TAG, "the mCb is null???");
                                return;
                            }
                            Object task = XposedHelpers.callMethod(taskViewObject, "getTask");
                            XposedHelpers.callMethod(cb, "onTaskViewClicked", taskViewObject,
                                    task, false);
                        }
                    }
                });

        XposedHelpers.findAndHookMethod(taskViewClass, "onLongClick", View.class, new
                XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws
                            Throwable {
                        if (!(Action.isActionsShowInRecentTask() || ForceStopAction.isShow)) {
                            // the recent and force stop action is not active
                            // EDIT:here we don't need to check the xhalo
                            return;
                        }

                        super.afterHookedMethod(param);
                        // get original result
                        boolean result = (boolean) param.getResult();
                        final View longClickedView = (View) param.args[0];
                        Log.d(TAG, "long click original result: " + result);
                        Log.d(TAG, "long clicked view: " + longClickedView);
                        if (result) {
                            Log.d(TAG, "already handled, do nothing");
                            return;
                        }
                        final FrameLayout taskViewObject = (FrameLayout) param.thisObject;
                        final View mHeaderView = (View) XposedHelpers.getObjectField
                                (taskViewObject,
                                        "mHeaderView");
                        Log.d(TAG, "hearView: " + mHeaderView);
                        initViewIds(taskViewObject.getResources());
                        final View mDissmissButton = mHeaderView.findViewById(idDismiss);
                        // don't need to check, 'cause we do in the onLoadData
                        if (longClickedView == mHeaderView) {
                            handleHeaderLongClick(loadPackageParam, param, taskViewObject,
                                    mHeaderView);
                        } else if (longClickedView == mDissmissButton) {
                            Log.d(TAG, "long clicked dismiss button");
                            handleDismissLongClick(taskViewObject);
                            param.setResult(true);
                        } else {
                            // set the original result back which is false
                            param.setResult(false);
                        }
                    }
                });
    }

    private void handleHeaderLongClick(final XC_LoadPackage.LoadPackageParam loadPackageParam,
                                       final XC_MethodHook.MethodHookParam param, final FrameLayout
                                               taskViewObject, final View mHeaderView) throws
            Exception {
        // long click on the header view
        Log.d(TAG, "begin long click handle");
        // the guts view
        FrameLayout headerGutsView;
        ViewGroup headerParent;
        final Context context = taskViewObject.getContext();
        final Resources res = context.getResources();
        // init the view ids
        initViewIds(res);
        headerGutsView = (FrameLayout) ((ViewGroup) mHeaderView.getParent
                ()).findViewById(ID_GUTS);
        Log.d(TAG, "get the headerGutsView: " + headerGutsView);
        // move header parent here
        headerParent = (ViewGroup) mHeaderView.getParent();
        Log.d(TAG, "header view parent: " + headerParent + ", " +
                "children: " +
                headerParent.getChildCount());
        // the header guts is null, we need create it
        if (headerGutsView == null) {
            headerGutsView = createHeaderGutsView(context, mHeaderView);
            // add view to the last position, and with layoutParams
            headerParent.addView(headerGutsView,
                    headerParent.getChildCount(), headerViewLayoutParams);
        } else {
            // the guts view is already added
            // check if visible, if so, just invisible it
            // EDIT: if we already show it, we just invisible it
            if (headerGutsView.getVisibility() == View.VISIBLE) {
                Log.d(TAG, "the guts is visible, dismiss it");
                startDismissAnimation(headerGutsView);

                // we handled it
                param.setResult(true);
                return;
            }
        }

        // the guts view is ok now, and is not showing
        // get the package name
        Intent intent = getTaskBaseIntent(taskViewObject);
        // this should be the tag !!!
        final String compName = getComponentName(intent).toString();
        final String pkgName = getPackageName(intent);

        int actionCount = 0;
        if (PlayAction.isShow && PlayAction.isShowInRecentTask) {
            if (Action.isNeed2Add(headerParent,
                    PlayAction.class)) {
                // set the action up
                Action xBridgeAction = new PlayAction();
                addViewAndSetAction(context, res, xBridgeAction, headerGutsView,
                        pkgName, loadPackageParam.classLoader, actionCount);
            } else if (checkIfNeed2Change(headerGutsView, compName)) {
                // this action need to be reset
                Action xBridgeAction = new PlayAction();
                resetAction(context, xBridgeAction, headerGutsView, pkgName);
            }
            actionCount++;
        }
        if (AppOpsAction.isShow && AppOpsAction.isShowInRecentTask) {
            if (Action.isNeed2Add(headerParent, AppOpsAction.class)) {
                Log.d(TAG, "add new AppOpsAction");
                // set the action up
                Action xBridgeAction = new AppOpsAction();
                addViewAndSetAction(context, res, xBridgeAction, headerGutsView,
                        pkgName, loadPackageParam.classLoader, actionCount);
            } else if (checkIfNeed2Change(headerGutsView, compName)) {
                // this action need to be reset
                Action xBridgeAction = new AppOpsAction();
                resetAction(context, xBridgeAction, headerGutsView, pkgName);
            }
            actionCount++;
        }
        if (AppSettingsAction.isShow && AppSettingsAction.isShowInRecentTask) {
            if (Action.isNeed2Add(headerParent, AppSettingsAction
                    .class)) {
                // set the action up
                Action xBridgeAction = new AppSettingsAction();
                addViewAndSetAction(context, res, xBridgeAction, headerGutsView,
                        pkgName, loadPackageParam.classLoader, actionCount);
            } else if (checkIfNeed2Change(headerGutsView, compName)) {
                // this action need to be reset
                Action xBridgeAction = new AppSettingsAction();
                resetAction(context, xBridgeAction, headerGutsView, pkgName);
            }
            actionCount++;
        }
        if (ClipBoardAction.isShow && ClipBoardAction.isShowInRecentTask) {
            if (Action.isNeed2Add(headerParent, ClipBoardAction
                    .class)) {
                // set the action up
                Action xBridgeAction = new ClipBoardAction();
                addViewAndSetAction(context, res, xBridgeAction, headerGutsView,
                        pkgName, loadPackageParam.classLoader, actionCount);
            } else if (checkIfNeed2Change(headerGutsView, compName)) {
                Action xBridgeAction = new ClipBoardAction();
                resetAction(context, xBridgeAction, headerGutsView, pkgName);
            }
            actionCount++;
        }
        if (SearchAction.isShow && SearchAction.isShowInRecentTask) {
            if (Action.isNeed2Add(headerParent, SearchAction.class)) {
                // set the action up
                Action xBridgeAction = new SearchAction();
                addViewAndSetAction(context, res, xBridgeAction, headerGutsView,
                        pkgName, loadPackageParam.classLoader, actionCount);
            } else if (checkIfNeed2Change(headerGutsView, compName)) {
                Action xBridgeAction = new SearchAction();
                resetAction(context, xBridgeAction, headerGutsView, pkgName);
            }
            actionCount++;
        }
        if (XPrivacyAction.isShow && XPrivacyAction.isShowInRecentTask) {
            if (Action.isNeed2Add(headerParent, XPrivacyAction.class)) {
                // set the action up
                Action xBridgeAction = new XPrivacyAction();
                addViewAndSetAction(context, res, xBridgeAction, headerGutsView,
                        pkgName, loadPackageParam.classLoader, actionCount);
            } else if (checkIfNeed2Change(headerGutsView, compName)) {
                Action xBridgeAction = new XPrivacyAction();
                resetAction(context, xBridgeAction, headerGutsView, pkgName);
            }
            actionCount++;
        }
        if (AppInfoAction.isShow && AppInfoAction.isShowInRecentTask) {
            if (Action.isNeed2Add(headerParent, AppInfoAction.class)) {
                // set the action up
                Action xBridgeAction = new AppInfoAction();
                addViewAndSetAction(context, res, xBridgeAction, headerGutsView,
                        pkgName, loadPackageParam.classLoader, actionCount);
            } else if (checkIfNeed2Change(headerGutsView, compName)) {
                Action xBridgeAction = new AppInfoAction();
                resetAction(context, xBridgeAction, headerGutsView, pkgName);
            }
            actionCount++;
        }
        if (NotifyCleanAction.isShow && NotifyCleanAction.isShowInRecentTask) {
            if (Action.isNeed2Add(headerParent, NotifyCleanAction.class)) {
                // set the action up
                Action xBridgeAction = new NotifyCleanAction();
                addViewAndSetAction(context, res, xBridgeAction, headerGutsView,
                        pkgName, loadPackageParam.classLoader, actionCount);
            } else if (checkIfNeed2Change(headerGutsView, compName)) {
                Action xBridgeAction = new NotifyCleanAction();
                resetAction(context, xBridgeAction, headerGutsView, pkgName);
            }
            actionCount++;
        }
        if (LightningWallAction.isShow && LightningWallAction.isShowInRecentTask) {
            if (Action.isNeed2Add(headerParent, LightningWallAction.class)) {
                // set the action up
                Action xBridgeAction = new LightningWallAction();
                addViewAndSetAction(context, res, xBridgeAction, headerGutsView,
                        pkgName, loadPackageParam.classLoader, actionCount);
            } else if (checkIfNeed2Change(headerGutsView, compName)) {
                Action xBridgeAction = new LightningWallAction();
                resetAction(context, xBridgeAction, headerGutsView, pkgName);
            }
            actionCount++;
        }
        if (MyAndroidToolsAction.isShow && MyAndroidToolsAction.isShowInRecentTask) {
            if (Action.isNeed2Add(headerParent, MyAndroidToolsAction.class)) {
                // set the action up
                Action xBridgeAction = new MyAndroidToolsAction();
                addViewAndSetAction(context, res, xBridgeAction, headerGutsView,
                        pkgName, loadPackageParam.classLoader, actionCount);
            } else if (checkIfNeed2Change(headerGutsView, compName)) {
                Action xBridgeAction = new MyAndroidToolsAction();
                resetAction(context, xBridgeAction, headerGutsView, pkgName);
            }
            actionCount++;
        }
        // moved XHalo to task view header
        Log.d(TAG, "count:" + actionCount);

        // check if the guts view is NOT the right one, some cycle stuff
        setTagAndEffect(res, mHeaderView, headerGutsView, compName);

        startShowingAnimation(headerGutsView);

        // set that this action we will handle it
        param.setResult(true);
    }

    private void handleDismissLongClick(final FrameLayout taskViewObject) {
        final String pkgName = getTaskBaseIntent(taskViewObject).getComponent().getPackageName();
        Log.d(TAG, "long dismiss pkgName: " + pkgName);
        Action action = new ForceStopAction();
        action.handleData(taskViewObject.getContext(), pkgName);
    }

    /**
     * get the base intent of this task, this intent contain info of target task, including
     * package name(intent.getComponent().getPackageName()).
     *
     * @param taskViewObject task view object
     * @return base intent of this task view
     */
    private Intent getTaskBaseIntent(final FrameLayout taskViewObject) {
        final Object task = XposedHelpers.callMethod(taskViewObject,
                "getTask");
        final Object key = XposedHelpers.getObjectField(task, "key");
        Log.d(TAG, "task: " + task + ", key: " + key);
        final Intent intent = (Intent) XposedHelpers.getObjectField(key,
                "baseIntent");
        Log.d(TAG, "base intent: " + intent);
        return intent;
    }

    private String getPackageName(Intent baseIntent) {
        return getComponentName(baseIntent).getPackageName();
    }

    private ComponentName getComponentName(Intent baseIntent) {
        // this should be the tag !!!
        return baseIntent.getComponent();
    }

    private void initViewIds(Resources res) {
        if (viewHeaderId == -1) {
            // use package name on get identifier
            viewHeaderId = res.getIdentifier("recents_task_view_header", "layout",
                    StaticData.PKG_NAME_SYSTEMUI);
            Log.d(TAG, "view header view: " + viewHeaderId);
        }
        if (idDismiss == -1) {
            idDismiss = res.getIdentifier("dismiss_task", "id",
                    StaticData.PKG_NAME_SYSTEMUI);
            Log.d(TAG, "dismiss button view: " + idDismiss);
        }
        if (idDesc == -1) {
            idDesc = res.getIdentifier("activity_description", "id",
                    StaticData.PKG_NAME_SYSTEMUI);
        }
        if (buttonBgId == -1) {
            // set the button background
            buttonBgId = res.getIdentifier("recents_button_bg", "drawable",
                    StaticData.PKG_NAME_SYSTEMUI);
            Log.d(TAG, "buttonBgId: " + buttonBgId);
        }
    }

    private void getLayoutParams(View mHeaderView) {
        if (headerViewLayoutParams == null) {
            // add the guts to the headerParent
            headerViewLayoutParams = (FrameLayout.LayoutParams)
                    mHeaderView
                            .getLayoutParams();
        }
        View dismissTaskView = mHeaderView.findViewById(idDismiss);
        Log.d(TAG, "dismiss guts view: " + dismissTaskView);
        // set the layout params
        if (dismissViewLayoutParams == null) {
            dismissViewLayoutParams = (FrameLayout.LayoutParams)
                    dismissTaskView
                            .getLayoutParams();
        }
    }

    private FrameLayout createHeaderGutsView(Context context, View mHeaderView) {
        // inflate the guts view
        FrameLayout headerGutsView = (FrameLayout) LayoutInflater.from
                (context).inflate(viewHeaderId, null);
        // set the gut id for later retrieve from layout
        headerGutsView.setId(ID_GUTS);
        // use primary dark color
        headerGutsView.setBackgroundColor(Hook.getXBridgeContext(context).getResources().getColor
                (R.color.primary_dark));

        // we don't need dismiss task view, so dismiss it
        Log.d(TAG, "headerGutsView: " + headerGutsView);
        View dismissTaskView = headerGutsView.findViewById(idDismiss);
        Log.d(TAG, "dismiss guts view: " + dismissTaskView);
        dismissTaskView.setVisibility(View.GONE);
        // we don't need this text views
        headerGutsView.findViewById(idDesc).setVisibility(View.GONE);

        getLayoutParams(mHeaderView);
        return headerGutsView;
    }

    private void addViewAndSetAction(Context context, Resources res, Action action, ViewGroup
            headerGutsView, String pkgName, ClassLoader classLoader, int actionCount, Intent
                                             originalIntent) throws
            Exception {
        Log.d(TAG, "add new Action: " + action);
        ImageView xBridgeView = getXBridgeView(context, res,
                classLoader, actionCount);
        action.setAction(RecentTaskHook.this, context,
                pkgName,
                xBridgeView, originalIntent);
        // add layoutParams
        headerGutsView.addView(xBridgeView/*,
                                        dismissViewLayoutParams*/);
    }

    private void addViewAndSetAction(Context context, Resources res, Action action, ViewGroup
            headerGutsView, String pkgName, ClassLoader classLoader, int actionCount) throws
            Exception {
        addViewAndSetAction(context, res, action, headerGutsView, pkgName, classLoader,
                actionCount, null);
    }

    private void resetAction(Context context, Action action, ViewGroup viewGroup, String
            pkgName, Intent intent) {
        Log.d(TAG, "reset action: " + action);
        // this action need to be reset
        // EDIT: use action.getClass instead
        ImageView xBridgeView = (ImageView) viewGroup.findViewById(Action.getViewId(action
                .getClass()));
        action.setAction(RecentTaskHook.this, context,
                pkgName,
                xBridgeView, intent);
    }

    private void resetAction(Context context, Action action, ViewGroup headerGutsView, String
            pkgName) {
        resetAction(context, action, headerGutsView, pkgName, null);
    }

    private boolean checkIfNeed2Change(View headerGutsView, String compName) {
        return !compName.equals(headerGutsView.getTag());
    }

    /**
     * create a new xbridge view based on FixedSizeImageView
     * <br>NOTE: before call this method, call initViewId and getLayoutParams
     *
     * @param context     Context
     * @param res         Resource
     * @param classLoader the class loader for the system ui
     * @param actionCount the position of the this view in the layout from right to left
     * @return the created xbridge view
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    private ImageView getXBridgeView(Context context, Resources res, ClassLoader
            classLoader, int
                                             actionCount) throws
            IllegalAccessException, InvocationTargetException, InstantiationException {
        if (fixedSizeImageViewConstructor == null) {
            Class<?> fixedSizeImageViewClass = XposedHelpers.findClass(StaticData
                    .PKG_NAME_SYSTEMUI +
                    ".recents.views.FixedSizeImageView", classLoader);
            Log.d(TAG, "fixedSizeImageView: " + fixedSizeImageViewClass);
            fixedSizeImageViewConstructor = XposedHelpers
                    .findConstructorBestMatch(fixedSizeImageViewClass,
                            Context.class);
            Log.d(TAG, "fixedSizeImageView constructor: " +
                    fixedSizeImageViewConstructor);
        }
        ImageView xBridgeView = (ImageView)
                fixedSizeImageViewConstructor.newInstance
                        (context);
        Log.d(TAG, "imageView: " + xBridgeView);
        xBridgeView.setLayoutParams(dismissViewLayoutParams);
        //TODO: get the layout padding
        xBridgeView.setPadding(12, 12, 12, 12);
        if (actionCount != 0) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
                    (dismissViewLayoutParams);
            params.setMarginEnd(params.getMarginEnd() +
                    actionCount * params.width);
            // set the layout params back
            xBridgeView.setLayoutParams(params);

            Log.d(TAG, "the action count is: " + actionCount + ", margin end: " + params
                    .getMarginEnd());
        }
        // the background drawable should used one time
        xBridgeView.setBackground(res.getDrawable(buttonBgId, null));
        return xBridgeView;
    }

    private void setTagAndEffect(Resources res, View mHeaderView, ViewGroup headerGutsView,
                                 String compName) {
        // check if the guts view is NOT the right one, some cycle stuff
        if (!compName.equals(headerGutsView.getTag())) {
            Log.d(TAG, "reset the guts view: " + headerGutsView);
            headerGutsView.setTag(compName);
            // get the views id so we can set the content
            if (idIcon == -1) {
                idIcon = res.getIdentifier("application_icon", "id", StaticData.PKG_NAME_SYSTEMUI);
            }

            Log.d(TAG, "icon id: " + idIcon + ", desc id: " + idDesc);
            // this is needed every time
            // call mutate method and clone a new drawable
            Drawable drawable = ((ImageView) mHeaderView.findViewById
                    (idIcon))
                    .getDrawable().getConstantState().newDrawable()
                    .mutate();
            // set the color filter to grey
            drawable.setTintMode(PorterDuff.Mode.MULTIPLY);
            drawable.setTint(Color.GRAY);
//            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            ((ImageView) headerGutsView.findViewById(idIcon))
                    .setImageDrawable
                            (drawable
                            );
        }
    }

    private void startDismissAnimation(final View headerGutsView) {
        // set the animation
        if (headerGutsView.getWindowToken() == null) return;

        // use header view width and height
        Log.d(TAG, "mHeaderView,w,h: " + headerGutsView.getWidth() +
                ", " +
                headerGutsView.getHeight());
        Log.d(TAG, "point: " + listener.getX() + ", " + listener.getY());
        final double horz = Math.max(headerGutsView.getRight() - listener
                .getX(), listener.getX() - headerGutsView.getLeft());
        final double vert = Math.max(headerGutsView.getTop() - listener
                .getY(), listener.getY() - headerGutsView.getBottom());
        final float r = (float) Math.hypot(horz, vert);
        Log.d(TAG, "ripple r: " + r);
        final Animator a
                = ViewAnimationUtils.createCircularReveal
                (headerGutsView,
                        listener.getX(),
                        listener.getY(),
                        r, 0);
        a.setDuration(TIME_ANIMATION);
        a.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                                    mHeaderView.setVisibility(View.INVISIBLE);
                headerGutsView.setVisibility(View.INVISIBLE);
                Log.d(TAG, "dismiss animation done");
            }
        });
        a.setInterpolator(interpolator);
        a.start();
    }

    private void startShowingAnimation(final View headerGutsView) {
        // set the animation
        if (headerGutsView.getWindowToken() == null) return;

        // use header view width and height
        Log.d(TAG, "mHeaderView,w,h: " + headerGutsView.getWidth() + ", " +
                headerGutsView.getHeight());
        Log.d(TAG, "point: " + listener.getX() + ", " + listener.getY());
        final double horz = Math.max(headerGutsView.getRight() - listener.getX(),
                listener.getX() - headerGutsView.getLeft());
        final double vert = Math.max(headerGutsView.getTop() - listener.getY(),
                listener.getY() - headerGutsView.getBottom());
        final float r = (float) Math.hypot(horz, vert);
        Log.d(TAG, "ripple r: " + r);
        final Animator a
                = ViewAnimationUtils.createCircularReveal(headerGutsView,
                listener.getX(), listener.getY(),
                0, r);
        a.setDuration(TIME_ANIMATION);
        a.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                                    mHeaderView.setVisibility(View.INVISIBLE);
                Log.d(TAG, "show animation done");
            }
        });
        a.setInterpolator(interpolator);
        headerGutsView.setVisibility(View.VISIBLE);
        a.start();
    }

    class HeaderViewTouchListener implements View.OnTouchListener {
        // the touched point on the header view
        private int x = 0;
        private int y = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int action = event.getAction();
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    x = (int) event.getX();
                    y = (int) event.getY();
                    break;
                }
            }
            return false;
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }
    }
}
