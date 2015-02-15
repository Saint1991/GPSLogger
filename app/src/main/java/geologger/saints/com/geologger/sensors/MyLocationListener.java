package geologger.saints.com.geologger.sensors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import geologger.saints.com.geologger.services.PositioningService_;
import geologger.saints.com.geologger.utils.Position;
import geologger.saints.com.geologger.utils.ServiceRunningConfirmation;

/**
 * Created by Seiya on 2015/01/01.
 */
@EBean(scope = EBean.Scope.Singleton)
public class MyLocationListener implements LocationListener {

    public final String TAG = getClass().getSimpleName();
    public static final String ACTION = "CurrentPositionUpdated";

    @RootContext
    Context mContext;

    @Bean
    ServiceRunningConfirmation mServiceRunningConfirmation;

    public MyLocationListener() {}

    //Definition of LocationListener
    @Override
    public void onLocationChanged(Location location) {

        float latitude = (float)location.getLatitude();
        float longitude = (float)location.getLongitude();

        if (mContext == null) {
            Log.i(TAG, "mContext is null");
        }
        Position.savePosition(mContext.getApplicationContext(), latitude, longitude);

        Intent broadcastIntent = new Intent(ACTION);
        broadcastIntent.putExtra(Position.LATITUDE, latitude);
        broadcastIntent.putExtra(Position.LONGITUDE, longitude);
        mContext.sendBroadcast(broadcastIntent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "onStatusChanged " + provider + " " + status );

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "onProviderEnabled " + provider);

        if (provider.equals(LocationManager.GPS_PROVIDER)) {

            if (mServiceRunningConfirmation.isPositioning()) {
                restartPositioningIfRunning();
            }

        }
    }

    @Override
    public void onProviderDisabled(String provider) {

        Log.i(TAG, "onProviderDisabled " + provider);

        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            restartPositioningIfRunning();
        }

    }


    protected void restartPositioningIfRunning() {

        Intent intent = new Intent(mContext, PositioningService_.class);

        if (mServiceRunningConfirmation.isPositioning()) {
            mContext.stopService(intent);
        }

        mContext.startService(intent);
    }
}
