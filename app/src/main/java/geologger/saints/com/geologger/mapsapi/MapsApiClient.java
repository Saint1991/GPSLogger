package geologger.saints.com.geologger.mapsapi;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.EBean;

import java.util.List;

import geologger.saints.com.geologger.http.AppController;
import geologger.saints.com.geologger.mapsapi.models.MapLeg;

/**
 * Created by Seiya on 2015/03/17.
 */
@EBean
public class MapsApiClient {

    private String TAG = getClass().getSimpleName();
    private static final String BASEURL = "https://maps.googleapis.com/maps/api/directions/json?";

    public MapsApiClient() {}

    public void query(final LatLng origin, final LatLng destination, final String language, final INavigationCallback callback) {

        StringBuilder url = new StringBuilder(BASEURL);
        url.append("origin=" + origin.latitude + "," + origin.longitude);
        url.append("&destination=" + destination.latitude + "," + destination.longitude);
        url.append("&mode=walking");
        url.append("&language=" + language);

        StringRequest request = new StringRequest(Request.Method.GET, url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onNavigationResult(MapsApiParser.parseRoute(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        AppController.getInstance().addToRequestQueue(request, TAG);
    }

    public interface INavigationCallback {
        public void onNavigationResult(List<MapLeg> result);
    }
}
