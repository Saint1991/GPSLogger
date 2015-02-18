package geologger.saints.com.geologger.routes;

import android.content.Intent;
import android.net.wifi.WifiManager;

import org.androidannotations.annotations.EBean;

import java.util.HashMap;

import geologger.saints.com.geologger.services.PositioningService_;
import geologger.saints.com.geologger.services.SendDataService_;

/**
 * Created by Seiya on 2014/12/31.
 */
@EBean
public class Route {

    private static HashMap<String, Class> routeMap = new HashMap<String, Class>() {
        {
            put(WifiManager.NETWORK_STATE_CHANGED_ACTION, SendDataService_.class);
            put(Intent.ACTION_BOOT_COMPLETED, PositioningService_.class);
        }
    };

    public static Class route(Intent intent) {
        String action = intent.getAction();
        return routeMap.get(action);
    }
}
