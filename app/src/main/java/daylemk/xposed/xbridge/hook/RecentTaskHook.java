package daylemk.xposed.xbridge.hook;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.RippleDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import daylemk.xposed.xbridge.utils.Log;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by DayLemK on 2015/5/4.
 */
public class RecentTaskHook extends Hook {
    public static final String TAG = "RecentTaskHook";

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        super.initZygote(startupParam);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws
            Throwable {
        super.handleLoadPackage(loadPackageParam);
        Class<?> taskViewClass = XposedHelpers.findClass("com.android.systemui.recents.views" +
                        ".TaskView",
                loadPackageParam.classLoader);
        Log.d(TAG, "task view class: " + taskViewClass);
        // set the long press listener
        XposedHelpers.findAndHookMethod(taskViewClass, "onTaskDataLoaded", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
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
        // unset the long press listener
        XposedHelpers.findAndHookMethod(taskViewClass, "onTaskDataUnloaded", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                FrameLayout taskViewObject = (FrameLayout) param.thisObject;
                View mHeaderView = (View) XposedHelpers.getObjectField(taskViewObject,
                        "mHeaderView");
                mHeaderView.setOnLongClickListener(null);
                // call the original method
                param.getResult();
            }
        });

        XposedHelpers.findAndHookMethod(taskViewClass, "onLongClick", View.class, new
                XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        // get original result
                        boolean result = (boolean) param.getResult();
                        final View longClickedView = (View) param.args[0];
                        Log.d(TAG, "long click original result: " + result);
                        Log.d(TAG, "long clicked view: " + longClickedView);
                        if (result == true) {
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
                            // use package name on get identifier
                            int viewHeaderId = context.getResources().getIdentifier
                                    ("recents_task_view_header", "layout", "com.android.systemui");
                            Log.d(TAG, "view header view: " + viewHeaderId);

                            FrameLayout headerGutsView = (FrameLayout) LayoutInflater.from
                                    (context).inflate(viewHeaderId, null);
                            headerGutsView.setBackgroundColor(Color.BLUE);
//                            headerGutsView.setVisibility(View.INVISIBLE);

                            // we don't need dismiss guts view, so dismiss it
                            Log.d(TAG, "headerGutsView: " + headerGutsView);
                            Log.d(TAG, "headerGutsView counts: " + headerGutsView.getChildCount());
                            View dismissTaskView = headerGutsView.getChildAt(headerGutsView
                                    .getChildCount
                                            () - 1);
                            Log.d(TAG, "dismiss guts view: " + dismissTaskView);
                            if (dismissTaskView != null) {
                                dismissTaskView.setVisibility(View.GONE);
                            }
                            // set the visible here
                            headerGutsView.setVisibility(View.INVISIBLE);

                            // EDIT: add to view parent
                            // it seems like there will be no animation
                            ViewGroup.LayoutParams layoutParams = mHeaderView.getLayoutParams();
                            // add view to the last position, and with layoutParams
                            final ViewGroup headerParent = (ViewGroup) mHeaderView.getParent();
                            Log.d(TAG, "header view parent: " + headerParent + ", children: " +
                                    headerParent.getChildCount());
                            headerParent.addView(headerGutsView,
                                    headerParent.getChildCount(), layoutParams);

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
                                    Log.d(TAG, "animation done");
                                }
                            });
                            a.setInterpolator(new LinearInterpolator());
                            headerGutsView.setVisibility(View.VISIBLE);
                            a.start();

                            // set that this action we will handle it
                            param.setResult(true);
                        }
                    }
                });
    }
}
