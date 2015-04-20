package geologger.saints.com.geologger.foursquare;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import geologger.saints.com.geologger.activities.SettingsActivity;
import geologger.saints.com.geologger.foursquare.models.FourSquarePoi;
import geologger.saints.com.geologger.http.AppController;
import geologger.saints.com.geologger.utils.Position;

/**
 * Created by Mizuno on 2015/01/25.
 */
@EBean
public class FourSquareClient {

    private final String TAG = getClass().getSimpleName();
    public static final String FOURSQUARE_ROOT = "https://ja.foursquare.com/v/";

    private static final String ENDPOINT_POISEARCH = "https://api.foursquare.com/v2/venues/search?";
    private static final String ENDPOINT_PHOTOSEARCH ="https://api.foursquare.com/v2/venues/";
    private static final String LANGUAGE = Locale.getDefault().toString();
    private static final String DEFAULTPOICOUNT = "100";

    private static final String CLIENT_ID = "ZTOAPLAJWKM5A5HETKK1GILH2V2EVFQCOI1QMKPNEBXNJH4Q";
    private static final String CLIENT_SECRET = "ARI1OE35YBJDQQXCRWW3ZWSU5EO3XNT2DZUTDWK1CZ3ANWCE";

    @RootContext
    Context mContext;

    public FourSquareClient(Context context) {
        mContext = context;
    }

    //region POI

    public void searchPoi(final String term, final IPoiSearchResultCallback callback) {

        float[] position = Position.getPosition(mContext);
        final float latitude = position[0];
        final float longitude = position[1];

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String poiCount = preference.getString(SettingsActivity.POICOUNT, DEFAULTPOICOUNT);

        StringBuilder url = new StringBuilder(ENDPOINT_POISEARCH);
        url.append("client_id=" + CLIENT_ID);
        url.append("&client_secret=" + CLIENT_SECRET);
        url.append("&ll=" + latitude + "," + longitude);
        url.append("&limit=" + poiCount);
        url.append("&locale=" + LANGUAGE);
        url.append("&intent=checkin");
        url.append("&m=foursquare");
        url.append("&v=20150126");
        if (term != null && term.length() > 0) {
            try {
                url.append("&query=" + URLEncoder.encode(term, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        Log.i(TAG, url.toString());

        StringRequest request = new StringRequest(Request.Method.GET, url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSearchResult(FourSquareParser.parsePoiSearchResult(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                callback.onErrorResult();
            }
        });

        AppController.getInstance().addToRequestQueue(request, TAG);
    }

    public interface IPoiSearchResultCallback {
        public void onSearchResult(List<FourSquarePoi> result);
        public void onErrorResult();
    }

    //endregion

    //region photo

    /**
     * Searching Photos of the corresponding placeId
     * @param placeId
     * @param width
     * @param height
     * @return
     */
    public void searchPhotos(String placeId, final int resultCount, final int width, final int height, final IPhotoSearchResult callback) {

        StringBuilder url = new StringBuilder(ENDPOINT_PHOTOSEARCH + placeId +"/photos?");
        url.append("limit=" + resultCount);
        url.append("&client_id=" + CLIENT_ID);
        url.append("&client_secret=" + CLIENT_SECRET);
        url.append("&m=foursquare");
        url.append("&v=20150126");

        StringRequest request = new StringRequest(Request.Method.GET, url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSearchResult(FourSquareParser.parsePhotoSearchResult(response, width, height));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        AppController.getInstance().addToRequestQueue(request, TAG);
    }

    public void searchPhoto(String placeId, final int width, final int height, final IPhotoSearchResult callback) {
        searchPhotos(placeId, 1, width, height, callback);
    }

    public interface IPhotoSearchResult {
        public void onSearchResult(List<String> urlList);
    }
    //endregion

}
