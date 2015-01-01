package geologger.saints.com.geologger.routes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.androidannotations.annotations.EReceiver;

import geologger.saints.com.geologger.services.GPSLoggingService;

@EReceiver
public class RouteReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getSimpleName();

    public RouteReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Class startService = Route.route(intent);
        if (startService == null) {
            Log.d(TAG, intent.getAction());
            return;
        }

        Intent serviceIntent = new Intent(context, startService);
        context.startService(serviceIntent);
    }
}
