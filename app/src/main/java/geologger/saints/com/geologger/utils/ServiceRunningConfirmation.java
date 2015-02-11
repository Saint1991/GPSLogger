package geologger.saints.com.geologger.utils;

import android.app.ActivityManager;
import android.content.Context;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import java.util.List;

import geologger.saints.com.geologger.services.GPSLoggingService_;
import geologger.saints.com.geologger.services.PositioningService_;

/**
 * Created by Mizuno on 2015/02/12.
 * Utility class for checking the service running
 */
@EBean
public class ServiceRunningConfirmation {

    @RootContext
    Context mContext;

    @SystemService
    ActivityManager mActivityManager;

    public ServiceRunningConfirmation() {}

    /**
     * Check if the disignated service is running
     * @param serviceName
     * @return
     */
    public boolean isServiceRunning(String serviceName) {

        List<ActivityManager.RunningServiceInfo> runningServiceList = mActivityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info : runningServiceList) {

            String name = info.service.getClassName();
            if (name.equals(serviceName)) {
                return true;
            }
        }

        return false;
    }


    /**
     * Check if the logging service is running
     * @return true: GPSLoggingService_ is running.
     */
    public boolean isLogging() {
        return isServiceRunning(GPSLoggingService_.class.getName());
    }

    /**
     * Check if the logging service is running
     * @return true: PositioningService_ is running.
     */
    public boolean isPositioning() {
        return isServiceRunning(PositioningService_.class.getName());
    }

}
