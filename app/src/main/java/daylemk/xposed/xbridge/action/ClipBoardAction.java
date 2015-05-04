package daylemk.xposed.xbridge.action;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.widget.ImageButton;

import daylemk.xposed.xbridge.hook.Hook;

/**
 * @author DayLemK
 * @version 1.0
 * 28-四月-2015 9:16:48
 */
public class ClipBoardAction extends Action {


    @Override
    public Drawable getIcon(PackageManager packageManager) {
        return null;
    }

    @Override
    public void setAction(Hook hook, Context context, String pkgName, ImageButton image) {

    }

}