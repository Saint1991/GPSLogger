package geologger.saints.com.geologger.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;

import java.util.Timer;
import java.util.TimerTask;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.activities.RecordActivity;
import geologger.saints.com.geologger.activities.RecordActivity_;
import geologger.saints.com.geologger.activities.SettingsActivity;
import geologger.saints.com.geologger.database.TrajectorySQLite;
import geologger.saints.com.geologger.database.TrajectorySpanSQLite;
import geologger.saints.com.geologger.models.TrajectoryEntry;
import geologger.saints.com.geologger.utils.Position;
import geologger.saints.com.geologger.utils.TimestampGenerator;

@EService
public class GPSLoggingService extends Service {

    private final String TAG = getClass().getSimpleName();
    public static final String ACTION = "GpsLogged";
    private final String DEFAULTSAMPLINGINTERVAL = "10000";
    private static final int LOGGING_NOTIFICATION_ID = 1;


    @Bean
    TrajectorySQLite mTrajectoryDbHandler;

    @Bean
    TrajectorySpanSQLite mTrajectorySpanDbHandler;

    private String mTid = null;
    Timer mTimer = null;


    public GPSLoggingService() {

    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);

        Log.i(TAG, "onStartCommand");

        //インテントからtidを取得し，トラジェクトリの開始時刻を記録
        if (this.mTid == null) {
            this.mTid = intent.getStringExtra(TrajectoryEntry.TID);
            mTrajectorySpanDbHandler.insert(this.mTid);
        }


        if (mTimer == null) {
            mTimer = new Timer();
        }

        //Get Logging Interval From SharedPreference
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        String loggingIntervalStr = preference.getString(SettingsActivity.LOGGINGINTERVAL, DEFAULTSAMPLINGINTERVAL);
        final long loggingInterval = Long.parseLong(loggingIntervalStr);
        Log.i(TAG, "Logging with interval " + loggingIntervalStr + "msec");

        //Make This service foreground
        makeForegroundService();

        mTimer.schedule(new TimerTask() {

            //mSamplingIntervalごとに緯度経度情報を取得しデータベースに格納する
            //insertに成功した場合，緯度経度をブロードキャストする
            @Override
            public void run() {

                float[] position = Position.getPosition(getApplicationContext());
                float latitude = position[0];
                float longitude = position[1];
                if (mTrajectoryDbHandler.insert(mTid, latitude, longitude)) {
                    Intent broadcastIntent = new Intent(ACTION);
                    broadcastIntent.putExtra(Position.LATITUDE, latitude);
                    broadcastIntent.putExtra(Position.LONGITUDE, longitude);
                    sendBroadcast(broadcastIntent);
                }


            }

        },0L, loggingInterval);

        return START_REDELIVER_INTENT;
    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        this.mTimer.cancel();
        this.mTrajectorySpanDbHandler.setEnd(this.mTid, TimestampGenerator.getTimestamp());
        this.mTid = null;

        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void makeForegroundService() {

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(getResources().getString(R.string.start_logging))
                .setContentText(getResources().getString(R.string.now_logging))
                .setContentTitle(TAG)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(), RecordActivity.RECORDNOTIFICATIONCODE, new Intent(getApplicationContext(), RecordActivity_.class), PendingIntent.FLAG_UPDATE_CURRENT))
                .build();

        startForeground(LOGGING_NOTIFICATION_ID, notification);
    }

}
