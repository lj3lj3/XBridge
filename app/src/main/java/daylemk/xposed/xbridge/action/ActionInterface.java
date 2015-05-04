package daylemk.xposed.xbridge.action;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.widget.ImageButton;

/**
 * @author DayLemK
 * @version 1.0
 * 28-四月-2015 9:16:48
 */
public interface ActionInterface {

    /**
     * get the icon of this action
     * @param packageManager
     * @return
     */
    public Drawable getIcon(PackageManager packageManager);

    /**
     * set the click action of this action
     * @param image
     */
    public void setClickAction(Context context, String pkgName, ImageButton image);

    /**
     * get id of this action, so we can identify the action is added or not
     * @return
     */
    public int getId ();
}