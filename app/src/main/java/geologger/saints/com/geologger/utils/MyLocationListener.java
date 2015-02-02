package geologger.saints.com.geologger.utils;

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
    Context mContext;

    public MyLocationListener(Context context) {}

    //Definition of LocationListener
    @Override
    public void onLocationChanged(Location location) {

        Log.i("MyLocationListener", "onLocationChanged");

        float latitude = (float)location.getLatitude();
        float longitude = (float)location.getLongitude();

        Intent broadcastIntent = new Intent(ACTION);
        broadcastIntent.putExtra(Position.LATITUDE, latitude);
        broadcastIntent.putExtra(Position.LONGITUDE, longitude);
        mContext.sendBroadcast(broadcastIntent);

        Position.savePosition(mContext, latitude, longitude);
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
