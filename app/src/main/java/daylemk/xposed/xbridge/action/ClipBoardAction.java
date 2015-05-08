package daylemk.xposed.xbridge.action;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;

import daylemk.xposed.xbridge.hook.Hook;

/**
 * @author DayLemK
 * @version 1.0
 * 28-四月-2015 9:16:48
 */
public class ClipBoardAction extends Action {


    @Override
    protected Intent getIntent(Hook hook, String pkgName) {
        return null;
    }

    @Override
    public Drawable getIcon(PackageManager packageManager) {
        return null;
    }

    @Override
    public String getMenuTitle() {
        return null;
    }

}