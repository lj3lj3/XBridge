package daylemk.xposed.xbridge.action;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import daylemk.xposed.xbridge.data.IntegerBox;
import daylemk.xposed.xbridge.hook.Hook;
import daylemk.xposed.xbridge.hook.StatusBarHook;
import daylemk.xposed.xbridge.utils.Log;

/**
 * @author DayLemK
 * @version 1.0
 *          28-四月-2015 9:16:48
 */
public class PlayAction extends Action {
    public static final String TAG = "PlayAction";
    public static final String STR_VIEW_IN_PLAY_STORE = "View in Play store";

    public static final String PKG_NAME_PLAY_STORE = "com.android.vending";
    // just need to init the icon and listener once
    // EDIT: maybe the icon is already one instance in the system
    public static Drawable sIcon = null;
    //public static View.OnClickListener sOnClickListener = null;
    // EDIT: the on click listener should be different

    // every class need a sIdBox
    public static IntegerBox sIdBox = new IntegerBox();
//    public static int sMenuId = View.generateViewId();


    @Override
    public Drawable getIcon(PackageManager packageManager) {
        // check the icon. if good, just return.
        // TODO: check if the app is just install or upgrade and the icon should be changed
        if (sIcon == null) {
//        Drawable pkgIcon = null;
            try {
                sIcon = packageManager.getApplicationIcon(PKG_NAME_PLAY_STORE);
                Log.d(TAG, "play store icon is found: " + sIcon);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, "play store icon can't be found, use generic icon");
                // app is gone, just show package name and generic icon
                sIcon = packageManager.getDefaultActivityIcon();
            }
        } else {
            Log.d(TAG, "icon is ok, no need to create again");
        }
        return sIcon;
    }

    public void setStatusBar(Object statusBar) {

    }

    @Override
    public void setAction(final Hook hook, final Context context,
                          final String pkgName,
                          View view) {
//        super.setAction(hook, context, pkgName, imageButton);
        // set the button id first
        // the button id can be the same within the different notification
        view.setId(PlayAction.sIdBox.value);
        Log.d(TAG, "set click action, pkg: " + pkgName);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "on click play action, hook: " + hook);
                Intent intent = getIntent(pkgName);
                if (hook instanceof StatusBarHook) {
                    // dismiss the keyguard adn collapse panels
                    ((StatusBarHook) hook).dismissKeyguardAndStartIntent(intent, pkgName);
                } else {
                    // TODO: start activity as user
                    context.startActivity(intent);
                }

                Log.d(TAG, "play action done");
            }
        });

    }

    public Intent getIntent(String pkgName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //Common.debugLog(TAG + TAG_CLASS + "use browser : " + XposedInit.useBrowser);
        // add browser-store switch
        //if (XposedInit.useBrowser) {
        intent.setData(Uri.parse("http://play.google.com/store/apps/details?id="
                + pkgName));
        //} else {
        // move to this, so can view in different app store
        //    intent.setData(Uri.parse("market://details?id=" + packageName));
        //}
        //checkPlay(ctx, intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        Log.d(TAG, "set intent finished: " + intent);
        return intent;
    }

    /**
     * check if need to add the action
     *
     * @param viewGroup
     * @return
     */
    public static boolean isNeed2Add(ViewGroup viewGroup) {
        boolean isNeed = Action.isNeed2Add(viewGroup, PlayAction.sIdBox);
        Log.i(TAG, "is need to add the play action: " + isNeed);
        return isNeed;
    }

    /*@Override
    protected int getMenuId() {
        return sMenuId;
    }*/
}