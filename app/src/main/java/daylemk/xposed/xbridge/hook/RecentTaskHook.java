package daylemk.xposed.xbridge.hook;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import daylemk.xposed.xbridge.action.Action;
import daylemk.xposed.xbridge.action.AppOpsAction;
import daylemk.xposed.xbridge.action.AppSettingsAction;
import daylemk.xposed.xbridge.action.ClipBoardAction;
import daylemk.xposed.xbridge.action.PlayAction;
import daylemk.xposed.xbridge.action.SearchAction;
import daylemk.xposed.xbridge.data.MainPreferences;
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
    private int idIcon = 0;
    // the header desc id
    private int idDesc = 0;
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

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        super.initZygote(startupParam);
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws
            Throwable {
        super.handleLoadPackage(loadPackageParam);
        Class<?> taskViewClass = XposedHelpers.findClass("com.android.systemui.recents.views" +
                        ".TaskView",
                loadPackageParam.classLoader);
        Log.d(TAG, "task view class: " + taskViewClass);
        // set the long press listener
        // BUT we can't set the view back, 'cause the data maybe different due to data recycle
        XposedHelpers.findAndHookMethod(taskViewClass, "onTaskDataLoaded", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (!Action.isActionsShowInRecentTask()) {
                    // call the original method
                    param.getResult();
                    return;
                }

                super.beforeHookedMethod(param);
                // get this every time???
                FrameLayout taskViewObject = (FrameLayout) param.thisObject;
                View mHeaderView = (View) XposedHelpers.getObjectField(taskViewObject,
                        "mHeaderView");
                mHeaderView.setOnLongClickListener((View.OnLongClickListener) taskViewObject);
                if (listener == null) {
                    listener = new HeaderViewTouchListener();
                }
                // set on touch listener
                mHeaderView.setOnTouchListener(listener);

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
                        if (!Action.isActionsShowInRecentTask()) {
                            // call the original method
                            param.getResult();
                            return;
                        }

                        super.beforeHookedMethod(param);
                        FrameLayout taskViewObject = (FrameLayout) param.thisObject;
                        View mHeaderView = (View) XposedHelpers.getObjectField
                                (taskViewObject,
                                        "mHeaderView");
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

                        // call the original method
                        param.getResult();
                    }
                });

        XposedHelpers.findAndHookMethod(taskViewClass, "onLongClick", View.class, new
                XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws
                            Throwable {
                        if (!Action.isActionsShowInRecentTask()) {
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
                        if (longClickedView != mHeaderView) {
                            // set the original result back which is false
                            param.setResult(false);
                            return;
                        }

                        // long click on the header view
                        Log.d(TAG, "begin long click handle");
                        // the guts view
                        FrameLayout headerGutsView;
                        ViewGroup headerParent;
                        final Context context = taskViewObject.getContext();
                        final Resources res = context.getResources();
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
                            headerGutsView = createHeaderGutsView(context, res, mHeaderView);
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
                        final Object task = XposedHelpers.callMethod(taskViewObject,
                                "getTask");
                        final Object key = XposedHelpers.getObjectField(task, "key");
                        Log.d(TAG, "task: " + task + ", key: " + key);
                        final Intent intent = (Intent) XposedHelpers.getObjectField(key,
                                "baseIntent");
                        Log.d(TAG, "base intent: " + intent);
                        // this should be the tag !!!
                        final String compName = intent.getComponent().toString();
                        final String pkgName = intent.getComponent().getPackageName();

                        int actionCount = 0;
                        if (PlayAction.isShow && PlayAction.isShowInRecentTask) {
                            if (PlayAction.isNeed2Add(headerParent,
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
                            if (AppOpsAction.isNeed2Add(headerParent, AppOpsAction.class)) {
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
                            if (AppSettingsAction.isNeed2Add(headerParent, AppSettingsAction
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
                            if (ClipBoardAction.isNeed2Add(headerParent, ClipBoardAction
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
                            if (SearchAction.isNeed2Add(headerParent, SearchAction.class)) {
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
                        Log.d(TAG, "count:" + actionCount);

                        // check if the guts view is NOT the right one, some cycle stuff
                        setTagAndEffect(res, mHeaderView, headerGutsView, compName);

                        startShowingAnimation(headerGutsView);

                        // set that this action we will handle it
                        param.setResult(true);
                    }
                });
    }

    private FrameLayout createHeaderGutsView(Context context, Resources res, View mHeaderView) {
        if (viewHeaderId == -1) {
            // use package name on get identifier
            viewHeaderId = context.getResources().getIdentifier
                    ("recents_task_view_header", "layout", "com" +
                            ".android" +
                            ".systemui");
            Log.d(TAG, "view header view: " + viewHeaderId);
        }
        // inflate the guts view
        FrameLayout headerGutsView = (FrameLayout) LayoutInflater.from
                (context).inflate(viewHeaderId, null);
        // set the gut id for later retrieve from layout
        headerGutsView.setId(ID_GUTS);
        // TODO: set the color on the fly
        headerGutsView.setBackgroundColor(Color.BLUE);

        // we don't need dismiss task view, so dismiss it
        // TODO: get the dismiss task view id
        Log.d(TAG, "headerGutsView: " + headerGutsView);
        Log.d(TAG, "headerGutsView counts: " + headerGutsView
                .getChildCount());
        View dismissTaskView = headerGutsView.getChildAt(headerGutsView
                .getChildCount
                        () - 1);
        Log.d(TAG, "dismiss guts view: " + dismissTaskView);
        // set the layout params
        if (dismissViewLayoutParams == null) {
            dismissViewLayoutParams = (FrameLayout.LayoutParams)
                    dismissTaskView
                            .getLayoutParams();
        }
        dismissTaskView.setVisibility(View.GONE);
        if (idDesc == 0) {
            idDesc = res.getIdentifier("activity_description", "id",
                    "com" +
                            ".android.systemui");
        }
        // we don't need this text views
        headerGutsView.findViewById(idDesc).setVisibility(View.GONE);

        if (headerViewLayoutParams == null) {
            // add the guts to the headerParent
            headerViewLayoutParams = (FrameLayout.LayoutParams)
                    mHeaderView
                            .getLayoutParams();
        }
        return headerGutsView;
    }

    private void addViewAndSetAction(Context context, Resources res, Action action, ViewGroup
            headerGutsView, String pkgName, ClassLoader classLoader, int actionCount) throws
            IllegalAccessException, InstantiationException, InvocationTargetException {
        Log.d(TAG, "add new Action: " + action);
        ImageView xBridgeView = getXBridgeView(context, res,
                classLoader, actionCount);
        action.setAction(RecentTaskHook.this, context,
                pkgName,
                xBridgeView);
        // add layoutParams
        headerGutsView.addView(xBridgeView/*,
                                        dismissViewLayoutParams*/);
    }

    private void resetAction(Context context, Action action, ViewGroup headerGutsView, String
            pkgName) {
        Log.d(TAG, "reset action: " + action);
        // this action need to be reset
        ImageView xBridgeView = (ImageView) headerGutsView
                .findViewById
                        (Action
                                .getViewId(PlayAction.class));
        action.setAction(RecentTaskHook.this, context,
                pkgName,
                xBridgeView);
    }

    private boolean checkIfNeed2Change(View headerGutsView, String compName) {
        return !compName.equals(headerGutsView.getTag());
    }

    private ImageView getXBridgeView(Context context, Resources res, ClassLoader
            classLoader, int
                                             actionCount) throws
            IllegalAccessException, InvocationTargetException, InstantiationException {
        if (fixedSizeImageViewConstructor == null) {
            Class<?> fixedSizeImageViewClass = XposedHelpers.findClass("com" +
                    ".android.systemui" +
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
        if (buttonBgId == -1) {
            // set the button background
            buttonBgId = res.getIdentifier("recents_button_bg", "drawable",
                    "com.android.systemui");
            Log.d(TAG, "buttonBgId: " + buttonBgId);
        }
        // the background drawable should used one time
        xBridgeView.setBackground(res.getDrawable(buttonBgId));
        return xBridgeView;
    }

    private void setTagAndEffect (Resources res, View mHeaderView, ViewGroup headerGutsView, String compName){
        // check if the guts view is NOT the right one, some cycle stuff
        if (!compName.equals(headerGutsView.getTag())) {
            Log.d(TAG, "reset the guts view: " + headerGutsView);
            headerGutsView.setTag(compName);
            // get the views id so we can set the content
            if (idIcon == 0) {
                idIcon = res.getIdentifier("application_icon", "id", "com" +
                        ".android" +
                        ".systemui");
            }

            Log.d(TAG, "icon id: " + idIcon + ", desc id: " + idDesc);
            // this is needed every time
            // call mutate method and clone a new drawable
            Drawable drawable = ((ImageView) mHeaderView.findViewById
                    (idIcon))
                    .getDrawable().getConstantState().newDrawable()
                    .mutate();
            // set the color filter to grey
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
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

    private void startShowingAnimation (final View headerGutsView){
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
