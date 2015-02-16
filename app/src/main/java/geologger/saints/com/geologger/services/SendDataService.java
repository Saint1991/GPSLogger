package geologger.saints.com.geologger.services;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;



/**
 * TODO 要実装
 */
@EService
public class SendDataService extends Service {

    private final String TAG = getClass().getSimpleName();
    private final String SERVERURL = "http://";

    @SystemService
    ConnectivityManager connectivityManager;

    public SendDataService() {
    }


    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "onStartCommand");

        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo == null || netInfo.getState() != NetworkInfo.State.CONNECTED) {
            Log.d(TAG, "Service Stopping");
            this.stopSelf();
            return START_NOT_STICKY;
        }

        /*
        List<TrajectoryEntry> load = dbHandler.readAll();
        Gson gson = new Gson();
        String data = gson.toJson(load);

        //Debug
        Log.i(TAG, data);

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(SERVERURL);
        HttpResponse response = null;

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add( new BasicNameValuePair("data", data) );
        params.add( new BasicNameValuePair("uuid", UserID.getUserId(getApplicationContext())));

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            post.setEntity(entity);
            response = client.execute(post);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            this.stopSelf(startId);
        }


        //TODO Failure
        if (response == null || response.getStatusLine().getStatusCode() != 200) {
            Log.d(TAG, "Send Failure");
        }
        */

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
}
