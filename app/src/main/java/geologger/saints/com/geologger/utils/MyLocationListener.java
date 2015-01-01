package geologger.saints.com.geologger.utils;

import android.content.Context;
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

    @RootContext
    Context context;

    public MyLocationListener(Context context) {}

    //Definition of LocationListener
    @Override
    public void onLocationChanged(Location location) {

        Log.d("MyLocationListener", "onLocationChanged");

        float latitude = (float)location.getLatitude();
        float longitude = (float)location.getLongitude();

        Position.savePosition(context, latitude, longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("MyLocationListener", "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("MyLocationListener", "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("MyLocationListener", "onProviderDisabled");
    }
}
