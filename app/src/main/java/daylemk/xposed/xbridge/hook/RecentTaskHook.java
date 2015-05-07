package daylemk.xposed.xbridge.hook;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Constructor;

import daylemk.xposed.xbridge.action.Action;
import daylemk.xposed.xbridge.action.PlayAction;
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
    // the guts view id
    private static final int ID_GUTS = View.generateViewId();
    // the header icon id
    private int idIcon = 0;
    // the header desc id
    private int idDesc = 0;


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
                if (!MainPreferences.isShowInRecentTask) {
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

                // call the original method
                param.getResult();
            }
        });
        // unset the long press listener and unset the view
        // this is a data recycler
        XposedHelpers.findAndHookMethod(taskViewClass, "onTaskDataUnloaded", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if(!MainPreferences.isShowInRecentTask){
                    // call the original method
                    param.getResult();
                    return;
                }

                super.beforeHookedMethod(param);
                FrameLayout taskViewObject = (FrameLayout) param.thisObject;
                View mHeaderView = (View) XposedHelpers.getObjectField(taskViewObject,
                        "mHeaderView");
                mHeaderView.setOnLongClickListener(null);

                // EDIT: need to set the guts to invisible
                View gutsView = ((ViewGroup) mHeaderView.getParent()).findViewById(ID_GUTS);
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
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if(!MainPreferences.isShowInRecentTask){
                            // need?
//                            param.setResult(param.getResult());
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
                        final View mHeaderView = (View) XposedHelpers.getObjectField(taskViewObject,
                                "mHeaderView");
                        Log.d(TAG, "hearView: " + mHeaderView);
                        if (longClickedView == mHeaderView) {
                            Log.d(TAG, "begin long click handle");
                            final Context context = taskViewObject.getContext();
                            final Resources res = context.getResources();
                            // the guts view
                            FrameLayout headerGutsView;
                            // this for now is jus play image view
                            ImageView fixedSizeImageView;
                            if (PlayAction.isNeed2Add((ViewGroup) mHeaderView.getParent(),
                                    PlayAction.class)) {
                                // use package name on get identifier
                                int viewHeaderId = context.getResources().getIdentifier
                                        ("recents_task_view_header", "layout", "com.android" +
                                                ".systemui");
                                Log.d(TAG, "view header view: " + viewHeaderId);
                                // inflate the guts view
                                headerGutsView = (FrameLayout) LayoutInflater.from
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
                                ViewGroup.LayoutParams dismissViewLayoutParams = dismissTaskView
                                        .getLayoutParams();
//                                if (dismissTaskView != null) {
                                dismissTaskView.setVisibility(View.GONE);
//                                }
                                // add ImageView here
                                Class<?> fixedSizeImageViewClass = XposedHelpers.findClass("com" +
                                        ".android.systemui" +
                                        ".recents.views.FixedSizeImageView", loadPackageParam
                                        .classLoader);
                                Log.d(TAG, "fixedSizeImageView: " + fixedSizeImageViewClass);
                                Constructor<?> fixedSizeImageViewConstructor = XposedHelpers
                                        .findConstructorBestMatch(fixedSizeImageViewClass,
                                                Context.class);
                                Log.d(TAG, "fixedSizeImageView constructor: " +
                                        fixedSizeImageViewConstructor);
                                fixedSizeImageView = (ImageView)
                                        fixedSizeImageViewConstructor.newInstance
                                                (context);
                                Log.d(TAG, "imageView: " + fixedSizeImageView);
                                fixedSizeImageView.setLayoutParams(dismissViewLayoutParams);
                                // set the button background

                                int buttonBgId = res.getIdentifier("recents_button_bg", "drawable",
                                        "com.android.systemui");
                                Log.d(TAG, "buttonBgId: " + buttonBgId);
                                Drawable drawable = res.getDrawable(buttonBgId);
                                Log.d(TAG, "buttonBg drawable: " + drawable);
                                fixedSizeImageView.setBackground(drawable);

                                // add layoutParams
                                headerGutsView.addView(fixedSizeImageView, dismissViewLayoutParams);

                                // add the guts to the headerParent
                                ViewGroup.LayoutParams layoutParams = mHeaderView.getLayoutParams();
                                // add view to the last position, and with layoutParams
                                final ViewGroup headerParent = (ViewGroup) mHeaderView.getParent();
                                Log.d(TAG, "header view parent: " + headerParent + ", children: " +
                                        headerParent.getChildCount());
                                headerParent.addView(headerGutsView,
                                        headerParent.getChildCount(), layoutParams);
                            } else {
                                // if we already add this, we don't need to create it, just find
                                // it and set everything

                                // found headerGutsView and fixedSizeImageView
                                headerGutsView = (FrameLayout) ((ViewGroup) mHeaderView.getParent
                                        ()).findViewById(ID_GUTS);
                                Log.d(TAG, "get the headerGutsView: " + headerGutsView);

                                // EDIT: if we already show it, we just invisible it
                                if (headerGutsView.getVisibility() == View.VISIBLE) {
                                    Log.d(TAG, "the guts is visible, dismiss it");
                                    // copy guts view to a final one
                                    final View gutsViewFinal = headerGutsView;
                                    // set the animation
                                    // TODO: what is this for?
                                    if (headerGutsView.getWindowToken() == null) return;

                                    // use header view width and height
                                    Log.d(TAG, "mHeaderView,w,h: " + headerGutsView.getWidth() +
                                            ", " +
                                            headerGutsView.getHeight());
                                    final double horz = Math.max(headerGutsView.getWidth() / 2, 2);
                                    final double vert = Math.max(headerGutsView.getHeight() / 2, 2);
                                    final float r = (float) Math.hypot(horz, vert);
                                    Log.d(TAG, "ripple r: " + r);
                                    final Animator a
                                            = ViewAnimationUtils.createCircularReveal
                                            (headerGutsView,
                                                    headerGutsView.getWidth() / 2, headerGutsView
                                                            .getHeight() / 2,
                                                    r, 0);
                                    a.setDuration(400);
                                    a.addListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
//                                    mHeaderView.setVisibility(View.INVISIBLE);
                                            gutsViewFinal.setVisibility(View.INVISIBLE);
                                            Log.d(TAG, "dismiss animation done");
                                        }
                                    });
                                    a.setInterpolator(new LinearInterpolator());
                                    a.start();

                                    // we handled it
                                    param.setResult(true);
                                    return;
                                }

                                // else we need to show it
                                fixedSizeImageView = (ImageView) headerGutsView.findViewById
                                        (Action.getViewId(PlayAction.class));
                                Log.d(TAG, "get the fixedSizeImageView: " + fixedSizeImageView);
                                // set here or at end???
                                headerGutsView.setVisibility(View.VISIBLE);
                            }

                            // get the views id so we can set the content
                            if (idIcon == 0) {
                                idIcon = res.getIdentifier("application_icon", "id", "com.android" +
                                        ".systemui");
                            }
                            if (idDesc == 0) {
                                idDesc = res.getIdentifier("activity_description", "id", "com" +
                                        ".android.systemui");
                            }
                            Log.d(TAG, "icon id: " + idIcon + ", desc id: " + idDesc);
                            // this is needed every time
                            // TODO: get better performance
                            ((ImageView) headerGutsView.findViewById(idIcon)).setImageDrawable((
                                    (ImageView) mHeaderView.findViewById(idIcon)).getDrawable());
                            ((TextView) headerGutsView.findViewById(idDesc)).setText(((TextView)
                                    mHeaderView.findViewById(idDesc)).getText());
//                            headerGutsView.setVisibility(View.INVISIBLE);

                            // set the action up
                            PlayAction playAction = new PlayAction();
                            // get the package name
                            Object task = XposedHelpers.callMethod(taskViewObject, "getTask");
                            Object key = XposedHelpers.getObjectField(task, "key");
                            Log.d(TAG, "task: " + task + ", key: " + key);
                            Intent intent = (Intent) XposedHelpers.getObjectField(key,
                                    "baseIntent");
                            Log.d(TAG, "base intent: " + intent);
                            final String pkgName = intent.getComponent().getPackageName();

                            playAction.setAction(RecentTaskHook.this, context, pkgName,
                                    fixedSizeImageView);

                            // set the animation
                            // TODO: what is this for?
                            if (headerGutsView.getWindowToken() == null) return;

                            // use header view width and height
                            Log.d(TAG, "mHeaderView,w,h: " + mHeaderView.getWidth() + ", " +
                                    mHeaderView.getHeight());
                            final double horz = Math.max(mHeaderView.getWidth() / 2, 2);
                            final double vert = Math.max(mHeaderView.getHeight() / 2, 2);
                            final float r = (float) Math.hypot(horz, vert);
                            Log.d(TAG, "ripple r: " + r);
                            final Animator a
                                    = ViewAnimationUtils.createCircularReveal(headerGutsView,
                                    mHeaderView.getWidth() / 2, mHeaderView.getHeight() / 2,
                                    0, r);
                            a.setDuration(400);
                            a.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
//                                    mHeaderView.setVisibility(View.INVISIBLE);
                                    Log.d(TAG, "show animation done");
                                }
                            });
                            a.setInterpolator(new LinearInterpolator());
                            a.start();

                            // set that this action we will handle it
                            param.setResult(true);
                            return;
                        }
                        // set the original result back which is false
                        param.setResult(false);
                    }
                });
    }
}
