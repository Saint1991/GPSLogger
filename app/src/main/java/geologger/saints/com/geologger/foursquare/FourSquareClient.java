package geologger.saints.com.geologger.foursquare;

import android.content.Context;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.EBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import geologger.saints.com.geologger.utils.BaseHttpClient;
import geologger.saints.com.geologger.utils.Position;

/**
 * Created by Mizuno on 2015/01/25.
 */
@EBean
public class FourSquareClient extends BaseHttpClient {

    private final String TAG = getClass().getSimpleName();
    private static final String ENDPOINT_POISEARCH = "https://api.foursquare.com/v2/venues/search?";
    private static final String LANGUAGE = Locale.getDefault().toString();
    private static final int LIMIT = 25;

    private static final String CLIENT_ID = "ZTOAPLAJWKM5A5HETKK1GILH2V2EVFQCOI1QMKPNEBXNJH4Q";
    private static final String CLIENT_SECRET = "ARI1OE35YBJDQQXCRWW3ZWSU5EO3XNT2DZUTDWK1CZ3ANWCE";


    public FourSquareClient() {

    }

    public List<Poi> searchPoi(String term) {

        List<Poi> ret = null;

        float[] position = Position.getPosition(mContext);
        float latitude = position[0];
        float longitude = position[1];

        //make query for searching POI
        StringBuffer query = new StringBuffer();
        query.append(ENDPOINT_POISEARCH);
        query.append("client_id=" + CLIENT_ID);
        query.append("&client_secret=" + CLIENT_SECRET);
        query.append("&ll=" + latitude + "," + longitude);
        query.append("&limit=" + LIMIT);
        query.append("&intent=checkin");
        query.append("&m=foursquare");
        query.append("&v=20150126");
        if (term != null && term.length() > 0) {
            query.append("&query=" + term);
        }
        String result  = this.sendHttpGetRequest(query.toString());
        ret = parsePoiSearchResult(result);

        return ret;
    }

    private List<Poi> parsePoiSearchResult(String json) {

        List<Poi> ret = null;

        try {
            JSONObject root = new JSONObject(json);
            JSONObject response = root.getJSONObject("response");
            JSONArray venues = response.getJSONArray("venues");

            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<Poi>>(){}.getType();
            ret = gson.fromJson(venues.toString(), collectionType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }
}
