package geologger.saints.com.geologger.utils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import geologger.saints.com.geologger.database.CheckinFreeFormSQLite;
import geologger.saints.com.geologger.database.CheckinSQLite;
import geologger.saints.com.geologger.database.CompanionSQLite;
import geologger.saints.com.geologger.database.TrajectorySQLite;
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

    // 送信データのJSONArrayを作成
    public static JSONArray makeSendData(List<String> tidListToSend, TrajectorySQLite trajectoryDbHandler, CheckinFreeFormSQLite checkinFreeFormDbHandler, CheckinSQLite checkinDbHandler, CompanionSQLite companionDbHandler) {

        JSONArray sendData = new JSONArray();
        for (String tid : tidListToSend) {
            JSONObject entry = makeJsonEntryByTid(tid, trajectoryDbHandler, checkinFreeFormDbHandler, checkinDbHandler, companionDbHandler);
            if (entry != null) {
                sendData.put(entry);
            }
        }

        return sendData;
    }


    // TIDに対応するエントリのリストをJSONObjectで取得する
    private static JSONObject makeJsonEntryByTid(String tid, TrajectorySQLite trajectoryDbHandler, CheckinFreeFormSQLite checkinFreeFormDbHandler, CheckinSQLite checkinDbHandler, CompanionSQLite companionDbHandler) {

        List<TrajectoryEntry> trajectory = trajectoryDbHandler.getTrajectory(tid);
        List<CheckinFreeFormEntry> checkinFreeForm = checkinFreeFormDbHandler.getCheckinFreeFormList(tid);
        List<CheckinEntry> checkin = checkinDbHandler.getCheckinList(tid);
        List<CompanionEntry> companion = companionDbHandler.getCompanionList(tid);

        JSONObject entry = SendDataUtil.makeJsonData(trajectory, checkinFreeForm, checkin, companion);

        return entry;
    }

}
