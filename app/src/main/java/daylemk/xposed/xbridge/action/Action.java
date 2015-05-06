package daylemk.xposed.xbridge.action;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import daylemk.xposed.xbridge.data.IntegerBox;
import daylemk.xposed.xbridge.hook.Hook;
import daylemk.xposed.xbridge.utils.Log;

/**
 * @author DayLemK
 * @version 1.0
 * 28-四月-2015 9:16:48
 */
public abstract class Action {
    public static final String TAG = "Action";

    public static final int FROM_STATUS_BAR = 1;
    public static final int FROM_APP_INFO = 2;
//    public static final int FROM_STATUS_BAR = 1;

    // this call is from which inject object
    protected int fromWhere = -1;

    /**
     * get the icon of this action
     *
     * @param packageManager
     * @return
     */
    public abstract Drawable getIcon(PackageManager packageManager);

    /**
     * set the click action of this action
     */
    public abstract void setAction(Hook hook, Context context, String pkgName,
                                   View view);

    /*public static boolean isNeed2Add(ViewGroup viewGroup) {
        Log.i(TAG, "the action menu id is: " + );
        boolean isNeed = Action.isNeed2Add(viewGroup, PlayAction.sIdBox);
        Log.i(TAG, "is need to add the play action: " + isNeed);
        return isNeed;
    }*/
    /**
     * check if the action need to be added, if so, generate the id and save it to idBox. And
     * every subclass should have this method.
     *
     * @param viewGroup
     * @param idBox
     * @return
     */
    protected static boolean isNeed2Add(ViewGroup viewGroup, IntegerBox idBox) {
        Log.i(TAG, "action id is: " + idBox.value);
        if (idBox.value != 0) {
            // the id is not 0
            if (viewGroup.findViewById(idBox.value) != null) {
                // and found this id in this notification, so we already added it
                return false;
            } else {
                // we added the action before but not at this nofication
                // need add it
            }
        } else {
            // need add it
            // the first, need generate a id for it
            idBox.value = View.generateViewId();
        }

        return true;
    }

    /*protected abstract int getMenuId();

    public void addAppInfoMenu (Menu menu){
//        MenuItem menuItem = new MenuItem
    }*/
}