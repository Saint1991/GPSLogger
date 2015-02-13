package geologger.saints.com.geologger.foursquare;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import geologger.saints.com.geologger.activities.SettingsActivity;
import geologger.saints.com.geologger.foursquare.models.FourSquarePoi;
import geologger.saints.com.geologger.utils.BaseHttpClient;
import geologger.saints.com.geologger.utils.Position;

/**
 * Created by Mizuno on 2015/01/25.
 */
@EBean
public class FourSquareClient extends BaseHttpClient {

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

    public List<FourSquarePoi> searchPoi(String term) {

        List<FourSquarePoi> ret = null;

        float[] position = Position.getPosition(mContext);
        float latitude = position[0];
        float longitude = position[1];

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(mContext);
        String poiCount = preference.getString(SettingsActivity.POICOUNT, DEFAULTPOICOUNT);


        //make query for searching POI
        StringBuffer query = new StringBuffer();
        query.append(ENDPOINT_POISEARCH);
        query.append("client_id=" + CLIENT_ID);
        query.append("&client_secret=" + CLIENT_SECRET);
        query.append("&ll=" + latitude + "," + longitude);
        query.append("&limit=" + poiCount);
        query.append("&locale=" + LANGUAGE);
        query.append("&intent=checkin");
        query.append("&m=foursquare");
        query.append("&v=20150126");
        if (term != null && term.length() > 0) {
            query.append("&query=" + term);
        }

        try {

            String result  = this.sendHttpGetRequest(query.toString());
            ret = FourSquareParser.parsePoiSearchResult(result);

        } catch (Exception e) {
            e.printStackTrace();
        }


        Log.i(TAG, query.toString());

        return ret;
    }


    /**
     * Searching Photos of the corresponding placeId
     * @param placeId
     * @param width
     * @param height
     * @return
     */
    public List<String> searchPhotos(String placeId, int resultCount, int width, int height) {

        List<String> urlList = new ArrayList<String>();

        StringBuilder query = new StringBuilder();
        query.append(ENDPOINT_PHOTOSEARCH);
        query.append(placeId + "/");
        query.append("photos");
        query.append("?limit=" + resultCount);
        query.append("&client_id=" + CLIENT_ID);
        query.append("&client_secret=" + CLIENT_SECRET);
        query.append("&m=foursquare");
        query.append("&v=20150126");
        Log.i(TAG, query.toString());

        try {

            String result = this.sendHttpGetRequest(query.toString());
            urlList = FourSquareParser.parsePhotoSearchResult(result, width, height);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlList;

    }

    public String searchPhoto(String placeId, int width, int height) {

        String ret = null;

        List<String> result = searchPhotos(placeId, 1, width, height);
        if (result != null && result.size() > 0) {
            ret = result.get(0);
        }

        return ret;
    }

}
