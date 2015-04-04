package geologger.saints.com.geologger.routes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.EReceiver;

@EReceiver
public class RouteReceiver extends BroadcastReceiver {

    public RouteReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Class startService = Route.route(intent);
        if (startService == null) {
            return;
        }

        intent.setClass(context, startService);
        context.startService(intent);
    }
}
