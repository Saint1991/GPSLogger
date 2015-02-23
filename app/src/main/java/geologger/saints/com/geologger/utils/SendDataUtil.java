package geologger.saints.com.geologger.utils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import geologger.saints.com.geologger.models.CheckinEntry;
import geologger.saints.com.geologger.models.CheckinFreeFormEntry;
import geologger.saints.com.geologger.models.CompanionEntry;
import geologger.saints.com.geologger.models.TableDefinitions;
import geologger.saints.com.geologger.models.TrajectoryEntry;

/**
 * Created by Mizuno on 2015/02/23.
 */
public class SendDataUtil {

    public static JSONObject makeJsonData(List<TrajectoryEntry> trajectory, List<CheckinFreeFormEntry> checkinFreeForm, List<CheckinEntry> checkin, List<CompanionEntry> companion) {

        JSONObject entry = null;
        try {
            Gson gson = new Gson();
            entry = new JSONObject();
            entry.put(TableDefinitions.TRAJECTORY, new JSONArray(gson.toJson(trajectory)));
            entry.put(TableDefinitions.CHECKIN_FREE_FORM, new JSONArray(gson.toJson(checkinFreeForm)));
            entry.put(TableDefinitions.CHECKIN, new JSONArray(gson.toJson(checkin)));
            entry.put(TableDefinitions.COMPANION, new JSONArray(gson.toJson(companion)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return entry;
    }

}
