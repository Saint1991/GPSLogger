package geologger.saints.com.geologger.sensors;

import android.util.Log;

import org.androidannotations.annotations.EBean;

/**
 * Created by Seiya on 2015/02/16.
 */
@EBean(scope = EBean.Scope.Singleton)
public class MyLocationListener2 extends MyLocationListener {

    private final String TAG = getClass().getSimpleName();

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "onProviderDisabled " + provider);
    }
}
