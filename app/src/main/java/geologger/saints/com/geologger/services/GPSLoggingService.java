package geologger.saints.com.geologger.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import geologger.saints.com.geologger.database.CheckinSQLite;
import geologger.saints.com.geologger.database.SQLiteModelDefinition;
import geologger.saints.com.geologger.database.TrajectorySQLite;
import geologger.saints.com.geologger.database.TrajectorySpanSQLite;
import geologger.saints.com.geologger.models.TableDefinitions;
import geologger.saints.com.geologger.models.TrajectoryEntry;
import geologger.saints.com.geologger.utils.Position;
import geologger.saints.com.geologger.utils.TimestampGenerator;

@EService
public class GPSLoggingService extends Service {

    private final String TAG = getClass().getSimpleName();

    @Bean
    TrajectorySQLite mTrajectoryDbHandler;

    @Bean
    TrajectorySpanSQLite mTrajectorySpanDbHandler;

    private long mSamplingInterval = 20000L;
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
            mTimer = new Timer(true);
        }

        mTimer.schedule(new TimerTask() {

            //mSamplingIntervalごとに緯度経度情報を取得しデータベースに格納する
            @Override
            public void run() {

                float[] position = Position.getPosition(getApplicationContext());
                float latitude = position[0];
                float longitude = position[1];
                mTrajectoryDbHandler.insert(mTid, latitude, longitude);
                Log.i(TAG, "store " + latitude + ", " + longitude);
            }

        },0L, mSamplingInterval);

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        this.mTimer.cancel();
        this.mTrajectorySpanDbHandler.setEnd(this.mTid, TimestampGenerator.getTimestamp());
        this.mTid = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
