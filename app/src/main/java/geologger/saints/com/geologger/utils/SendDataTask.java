package geologger.saints.com.geologger.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import geologger.saints.com.geologger.activities.SettingsActivity;
import geologger.saints.com.geologger.database.CheckinFreeFormSQLite;
import geologger.saints.com.geologger.database.CheckinSQLite;
import geologger.saints.com.geologger.database.CompanionSQLite;
import geologger.saints.com.geologger.database.SentTrajectorySQLite;
import geologger.saints.com.geologger.database.TrajectorySQLite;

/**
 * Created by Mizuno on 2015/03/13.
 */
@EBean
public class SendDataTask implements Runnable {

    private String TAG = getClass().getSimpleName();
    private final String SERVERURL = "http://133.1.244.46/mizuno/geologger/server.php";

    private List<String> mTidListToSend = null;

    @RootContext
    Context mContext;

    @Bean
    BaseHttpClient mHttpClient;

    @Bean
    CheckinFreeFormSQLite mCheckinFreeFormDbHandler;

    @Bean
    CheckinSQLite mCheckinDbHandler;

    @Bean
    CompanionSQLite mCompanionDbHandler;

    @Bean
    TrajectorySQLite mTrajectoryDbHandler;

    @Bean
    SentTrajectorySQLite mSentTrajectoryDbHandler;

    public SendDataTask(Context context) {
        mContext = context;
    }

    public void setTidList(List<String> tidList) {
        mTidListToSend = tidList;
    }

    @Override
    public void run() {

        if (mTidListToSend == null || mTidListToSend.size() == 0) {
            return;
        }

        JSONArray sendData = SendDataUtil.makeSendData(mTidListToSend, mTrajectoryDbHandler, mCheckinFreeFormDbHandler, mCheckinDbHandler, mCompanionDbHandler);

        if (sendData == null || sendData.toString().equals("[]")) {
            return;
        }

        Log.i(TAG, "[Send Data] " + sendData.toString());
        List<NameValuePair> sendParams = new ArrayList<>();
        sendParams.add(new BasicNameValuePair("Data", sendData.toString()));
        sendParams.add(new BasicNameValuePair("UserID", UserId.getUserId(mContext)));


        //Preferenceで設定された2nd ServerのURLを取得
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String secondUrl = preference.getString(SettingsActivity.SECONDURL, null);

        //2nd URLに対する送信
        if (secondUrl != null && secondUrl.length() > 7) {
            mHttpClient.sendHttpPostRequest(secondUrl, sendParams);
            Log.i(TAG, "Sent to the second Server " + secondUrl);
        }

        //1st URLに対する送信
        //成功時に送信済みのTIDを記録する
        String result = mHttpClient.sendHttpPostRequest(SERVERURL, sendParams);
        if (result != null) {
            mSentTrajectoryDbHandler.insertSentTidList(mTidListToSend);
        }

        Log.i(TAG,"response: " + result);
    }
}
