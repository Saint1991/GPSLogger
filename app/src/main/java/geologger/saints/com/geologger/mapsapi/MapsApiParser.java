package geologger.saints.com.geologger.mapsapi;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import geologger.saints.com.geologger.mapsapi.models.MapLeg;
import geologger.saints.com.geologger.mapsapi.models.MapStep;

/**
 * Created by Seiya on 2015/03/17.
 */
public class MapsApiParser {


    public static List<MapLeg> parseRoute(String json) {

        List<MapLeg> ret = new ArrayList<MapLeg>();

        try {

            JSONObject response = new JSONObject(json);
            JSONArray routes = response.getJSONArray("routes");
            if (routes.length() == 0) {
                return ret;
            }

            JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");
            for (int j = 0; j < legs.length(); j++) {
                JSONObject leg = legs.getJSONObject(j);
                int lDistance = leg.getJSONObject("distance").getInt("value");
                int lDuration = leg.getJSONObject("duration").getInt("value");
                LatLng lStart = new LatLng(leg.getJSONObject("start_location").getDouble("lat"), leg.getJSONObject("start_location").getDouble("lng"));
                LatLng lEnd = new LatLng(leg.getJSONObject("end_location").getDouble("lat"), leg.getJSONObject("end_location").getDouble("lng"));
                String lStartAddress = leg.getString("start_address");
                String lEndAddress = leg.getString("end_address");
                List<MapStep> lSteps = new ArrayList<MapStep>();

                JSONArray steps = leg.getJSONArray("steps");
                for (int k = 0; k < steps.length(); k++) {
                    JSONObject stepJson = steps.getJSONObject(k);

                    int distance = stepJson.getJSONObject("distance").getInt("value");
                    int duration = stepJson.getJSONObject("duration").getInt("value");
                    LatLng start = new LatLng(stepJson.getJSONObject("start_location").getDouble("lat"), stepJson.getJSONObject("start_location").getDouble("lng"));
                    LatLng end = new LatLng(stepJson.getJSONObject("end_location").getDouble("lat"), stepJson.getJSONObject("end_location").getDouble("lng"));
                    String instruction = stepJson.getString("html_instructions");
                    List<LatLng> routeLine = decodePolyLineStr(stepJson.getJSONObject("polyline").getString("points"));
                    MapStep step = new MapStep(distance, duration, start, end, instruction, routeLine);
                    lSteps.add(step);
                }

                MapLeg mLeg = new MapLeg(lDistance, lDuration, lStart, lEnd, lStartAddress, lEndAddress, lSteps);
                ret.add(mLeg);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Decode string of Polyline Field to the List of LatLng
     * @param encoded
     * @return
     */
    private static List<LatLng> decodePolyLineStr(String encoded) {

        List<LatLng> ret = new ArrayList<>();

        int index = 0;
        int length = encoded.length();
        int latitude = 0;
        int longitude = 0;
        while(index < length) {

            int shift = 0;
            int b = -1;
            int result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            latitude += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            longitude += dlng;

            LatLng point = new LatLng(((double)latitude / 1E5) , ((double)longitude / 1E5));
            ret.add(point);
        }

        return ret;
    }
}
