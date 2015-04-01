package geologger.saints.com.geologger.mapsapi;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import geologger.saints.com.geologger.utils.BaseHttpClient;

/**
 * Created by Seiya on 2015/03/17.
 */
@EBean
public class MapsApiClient {

    private String TAG = getClass().getSimpleName();
    private static final String BASEURL = "https://maps.googleapis.com/maps/api/directions/json?";

    @Bean
    BaseHttpClient mHttpClient;


    public MapsApiClient() {

    }

    public String query(LatLng origin, LatLng destination) {

        String travelMode = "walking";
        String query = BASEURL + "origin=" + origin.latitude + "," + origin.longitude + "&" + "destination=" + destination.latitude + "," + destination.longitude + "&mode=" + travelMode;
        String response = mHttpClient.sendHttpGetRequest(query);
        Log.i(TAG, query);
        return response;
    }

    public String query(String origin, String destination) {
        String query = BASEURL + "origin=" + origin + "&" + "destination=" + destination;
        String response = mHttpClient.sendHttpGetRequest(query);
        return response;
    }
}
