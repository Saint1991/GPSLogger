package geologger.saints.com.geologger.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;

import geologger.saints.com.geologger.activities.SettingsActivity;
import geologger.saints.com.geologger.sensors.MyLocationListener;
import geologger.saints.com.geologger.sensors.MyLocationListener2;

@EService
public class PositioningService extends Service {

    private final String TAG = getClass().getSimpleName();
    private static final String DEFAULTSAMPLINGINTERVAL = "1000";
    private static final String DEFAULTSAMPLINGDISTANCE = "2";

    @SystemService
    LocationManager mLocationManager;

    @Bean
    MyLocationListener mLocationListener;

    @Bean
    MyLocationListener2 mLocationListener2;

    //Constructor
    public PositioningService() {}

    //region lifecycle
    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "onStartCommand");

        String provider = (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) ? LocationManager.GPS_PROVIDER : null;
        if (provider == null && mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        }

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        String positioningIntervalStr = preference.getString(SettingsActivity.POSITIONINGINTERVAL, DEFAULTSAMPLINGINTERVAL);
        long positioningInterval = Long.parseLong(positioningIntervalStr);
        String positioningDistanceStr = preference.getString(SettingsActivity.POSITIONINGDISTANCE, DEFAULTSAMPLINGDISTANCE);
        long positioningDistance = Long.parseLong(positioningDistanceStr);


        if (provider != null && mLocationManager.isProviderEnabled(provider)) {
            Log.i(TAG, "Start Positioning With " + provider + " interval: " + positioningIntervalStr + " distance: " + positioningDistanceStr);
            mLocationManager.requestLocationUpdates(provider, positioningInterval, positioningDistance, mLocationListener);

            if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, positioningInterval, positioningDistance, mLocationListener2);
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        mLocationManager.removeUpdates(mLocationListener);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //endregion

}
