package geologger.saints.com.geologger.foursquare;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import geologger.saints.com.geologger.foursquare.models.FourSquarePoi;

/**
 * Created by Mizuno on 2015/02/13.
 */
public class FourSquareParser {

    public static List<FourSquarePoi> parsePoiSearchResult(String json) {

        List<FourSquarePoi> ret = null;

        try {
            JSONObject root = new JSONObject(json);
            JSONObject response = root.getJSONObject("response");
            JSONArray venues = response.getJSONArray("venues");

            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<FourSquarePoi>>(){}.getType();
            ret = gson.fromJson(venues.toString(), collectionType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static List<String> parsePhotoSearchResult(String json, int width, int height) {

        List<String> ret = null;

        try {
            JSONObject root = new JSONObject(json);
            JSONObject response = root.getJSONObject("response");
            JSONObject photos = response.getJSONObject("photos");

            int count = photos.getInt("count");
            if (0 < count) {
                ret = new ArrayList<String>();
            }
            JSONArray items = photos.getJSONArray("items");

            for (int i = 0; i < count; i++) {
                JSONObject entry = items.getJSONObject(i);
                String prefix = entry.getString("prefix");
                String suffix = entry.getString("suffix");
                String url = prefix + width + "x" + height + suffix;
                ret.add(url);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }
}
