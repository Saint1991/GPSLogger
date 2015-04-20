package geologger.saints.com.geologger.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import geologger.saints.com.geologger.activities.SettingsActivity;
import geologger.saints.com.geologger.database.CheckinFreeFormSQLite;
import geologger.saints.com.geologger.database.CheckinSQLite;
import geologger.saints.com.geologger.database.CompanionSQLite;
import geologger.saints.com.geologger.database.SentTrajectorySQLite;
import geologger.saints.com.geologger.database.TrajectorySQLite;
import geologger.saints.com.geologger.http.AppController;

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

        final JSONArray sendData = SendDataUtil.makeSendData(mTidListToSend, mTrajectoryDbHandler, mCheckinFreeFormDbHandler, mCheckinDbHandler, mCompanionDbHandler);
        if (sendData == null || sendData.toString().equals("[]")) {
            return;
        }
        Log.i(TAG, sendData.toString());

        //メインサーバへの送信
        SendLogRequest request = new SendLogRequest(Request.Method.POST, SERVERURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i(TAG, "response: " + response);
                mSentTrajectoryDbHandler.insertSentTidList(mTidListToSend);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "SendDataError");
                error.printStackTrace();
            }

        }, sendData.toString());


        //Preferenceで設定された2nd Serverへの送信
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String secondUrl = preference.getString(SettingsActivity.SECONDURL, null);
        SendLogRequest request2 = new SendLogRequest(Request.Method.POST, secondUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }, sendData.toString());


        AppController.getInstance().addToRequestQueue(request, TAG);
        AppController.getInstance().addToRequestQueue(request2, TAG);

    }

    private class SendLogRequest extends StringRequest {

        private String mSendData = null;

        SendLogRequest(int method, String url, Response.Listener listener, Response.ErrorListener errorListener, String sendData) {
            super(method, url, listener, errorListener);
            mSendData = sendData;
        }

        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put("Data", mSendData);
            params.put("UserID", UserId.getUserId(mContext));
            return params;
        }
    }

}
