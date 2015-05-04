package daylemk.xposed.xbridge.hook;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by DayLemK on 2015/5/4.
 */
public class RecentTaskHook extends Hook {
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        super.initZygote(startupParam);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws
            Throwable {
        super.handleLoadPackage(loadPackageParam);
    }
}
