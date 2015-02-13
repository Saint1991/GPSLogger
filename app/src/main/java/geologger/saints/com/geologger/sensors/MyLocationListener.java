package geologger.saints.com.geologger.sensors;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import geologger.saints.com.geologger.utils.Position;

/**
 * Created by Seiya on 2015/01/01.
 */
@EBean(scope = EBean.Scope.Singleton)
public class MyLocationListener implements LocationListener {

    public static final String ACTION = "CurrentPositionUpdated";

    @RootContext
    public Context mContext;

    public MyLocationListener() {}

    //Definition of LocationListener
    @Override
    public void onLocationChanged(Location location) {

        float latitude = (float)location.getLatitude();
        float longitude = (float)location.getLongitude();

        if (mContext == null) {
            Log.i("LocationListener", "mContext is null");
        }
        Position.savePosition(mContext.getApplicationContext(), latitude, longitude);

        Intent broadcastIntent = new Intent(ACTION);
        broadcastIntent.putExtra(Position.LATITUDE, latitude);
        broadcastIntent.putExtra(Position.LONGITUDE, longitude);
        mContext.sendBroadcast(broadcastIntent);
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
