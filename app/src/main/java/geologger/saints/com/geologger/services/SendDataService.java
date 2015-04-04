package geologger.saints.com.geologger.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import geologger.saints.com.geologger.activities.SettingsActivity;
import geologger.saints.com.geologger.database.CheckinFreeFormSQLite;
import geologger.saints.com.geologger.database.CheckinSQLite;
import geologger.saints.com.geologger.database.CompanionSQLite;
import geologger.saints.com.geologger.database.SentTrajectorySQLite;
import geologger.saints.com.geologger.database.TrajectorySQLite;
import geologger.saints.com.geologger.database.TrajectorySpanSQLite;
import geologger.saints.com.geologger.models.CheckinEntry;
import geologger.saints.com.geologger.models.CheckinFreeFormEntry;
import geologger.saints.com.geologger.models.CompanionEntry;
import geologger.saints.com.geologger.models.TrajectoryEntry;
import geologger.saints.com.geologger.utils.BaseHttpClient;
import geologger.saints.com.geologger.utils.SendDataTask;
import geologger.saints.com.geologger.utils.SendDataUtil;
import geologger.saints.com.geologger.utils.UserId;


@EService
public class SendDataService extends Service {

    private final String TAG = getClass().getSimpleName();

    @SystemService
    ConnectivityManager mConnectivityManager;

    @Bean
    SendDataTask mSendDataTask;

    @Bean
    BaseHttpClient mHttpClient;

    @Bean
    CheckinFreeFormSQLite mCheckinFreeFormDbHandler;

    @Bean
    CheckinSQLite mCheckinDbHandler;

    @Bean
    CompanionSQLite mCompanionDbHandler;

    @Bean
    SentTrajectorySQLite mSentTrajectoryDbHandler;

    @Bean
    TrajectorySpanSQLite mTrajectorySpanDbHandler;

    @Bean
    TrajectorySQLite mTrajectoryDbHandler;

    public SendDataService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        if (!isReadyToSendData(intent)) {
            return START_NOT_STICKY;
        }

        List<String> tidListToSend = makeTidListToSend();
        mSendDataTask.setTidList(tidListToSend);

        new Thread(mSendDataTask).start();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    //region utility

    // Check if connected to WIFI or WIMAX
    private boolean isReadyToSendData(Intent intent) {

        NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (netInfo == null) {
            Log.i(TAG, "NetInfo is null");
            return false;
        }

        if (!(netInfo.getType() == ConnectivityManager.TYPE_WIFI || netInfo.getType() == ConnectivityManager.TYPE_WIMAX)) {
            return false;
        }

        NetworkInfo.State state = netInfo.getState();
        if (!state.equals(NetworkInfo.State.CONNECTED)) {
            return false;
        }

        return true;
    }


    // Get TID List to send whose trajectory is finished logging and has not been sent
    private List<String> makeTidListToSend() {

        List<String> sentList = mSentTrajectoryDbHandler.getSentTrajectoryList();
        List<String> tidList = mTrajectorySpanDbHandler.getLoggingFinishedTidList();

        List<String> ret = new ArrayList<>();
        for (String tid : tidList) {

            if (!sentList.contains(tid)) {
                ret.add(tid);
            }
        }

        return ret;
    }

    //endregion

}
