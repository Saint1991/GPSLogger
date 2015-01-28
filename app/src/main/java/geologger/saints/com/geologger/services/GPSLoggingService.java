package geologger.saints.com.geologger.services;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import geologger.saints.com.geologger.models.TrajectoryEntry;
import geologger.saints.com.geologger.utils.MyLocationListener;
import geologger.saints.com.geologger.utils.Position;

@EService
public class GPSLoggingService extends Service {

    private final String TAG = getClass().getSimpleName();
    private final long SAMPLINGINTERVAL = 10000l;


    @SystemService
    LocationManager locationManager;

    @Bean
    MyLocationListener mLocationListener;


    //Constructor
    public GPSLoggingService() {

    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "onStartCommand");

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);

        String provider = locationManager.getBestProvider(criteria, true);
        if (locationManager.isProviderEnabled(provider)) {
            locationManager.requestLocationUpdates(provider, SAMPLINGINTERVAL, 0, mLocationListener);
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
