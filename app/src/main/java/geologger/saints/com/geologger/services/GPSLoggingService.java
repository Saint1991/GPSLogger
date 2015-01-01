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
import geologger.saints.com.geologger.utils.SendDataQueueSQLiteHandler;

@EService
public class GPSLoggingService extends Service {

    private final String TAG = getClass().getSimpleName();
    private final long SAMPLINGINTERVAL = 20000l;


    @SystemService
    LocationManager locationManager;

    @Bean
    SendDataQueueSQLiteHandler dbHandler;

    @Bean
    MyLocationListener mLocationListener;

    private Timer mTimer = null;

    //Constructor
    public GPSLoggingService() {

    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand");

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);

        String provider = locationManager.getBestProvider(criteria, true);
        if (locationManager.isProviderEnabled(provider)) {
            locationManager.requestLocationUpdates(provider, SAMPLINGINTERVAL, 0, mLocationListener);
        }

        final String tid = UUID.randomUUID().toString();

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                float[] position = Position.getPosition(getApplicationContext());
                float latitude = position[0];
                float longitude = position[1];
                TrajectoryEntry entry = new TrajectoryEntry(tid, latitude, longitude);
                Log.d(TAG,new Gson().toJson(entry));
                dbHandler.insert(entry);
            }

        }, 0l, SAMPLINGINTERVAL);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        mTimer.cancel();
        mTimer = null;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
