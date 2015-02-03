package geologger.saints.com.geologger.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by Seiya on 2015/01/01.
 */
@EBean
public class MyLocationListener implements LocationListener{

    public static final String ACTION = "CurrentPositionUpdated";

    @RootContext
    Service mService;

    public MyLocationListener() {}

    //Definition of LocationListener
    @Override
    public void onLocationChanged(Location location) {

        float latitude = (float)location.getLatitude();
        float longitude = (float)location.getLongitude();

        Position.savePosition(mService, latitude, longitude);

        Intent broadcastIntent = new Intent(ACTION);
        broadcastIntent.putExtra(Position.LATITUDE, latitude);
        broadcastIntent.putExtra(Position.LONGITUDE, longitude);
        mService.sendBroadcast(broadcastIntent);

        Log.i("MyLocationListener", ACTION);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i("MyLocationListener", "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i("MyLocationListener", "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i("MyLocationListener", "onProviderDisabled " + provider);
    }
}
