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

@EService
public class PositioningService extends Service {

    private final String TAG = getClass().getSimpleName();
    private static final String DEFAULTSAMPLINGINTERVAL = "10000";
    private static final String DEFAULTSAMPLINGDISTANCE = "3";

    @SystemService
    LocationManager mLocationManager;

    @Bean
    MyLocationListener mLocationListener;


    //Constructor
    public PositioningService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
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
        String positioningDistanceStr = preference.getString(SettingsActivity.POSITIONINGDISTANCE, DEFAULTSAMPLINGDISTANCE);
        long positioningInterval = Long.parseLong(positioningIntervalStr);
        long positioningDistance = Long.parseLong(positioningDistanceStr);


        if (provider != null && mLocationManager.isProviderEnabled(provider)) {
            Log.i(TAG, "Start Positioning With " + provider + " interval: " + positioningIntervalStr + " distance: " + positioningDistanceStr);
            mLocationManager.requestLocationUpdates(provider, positioningInterval, positioningDistance, mLocationListener);
        }

        return START_REDELIVER_INTENT;
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

}
