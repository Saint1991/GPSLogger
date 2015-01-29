package geologger.saints.com.geologger.models;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import geologger.saints.com.geologger.models.CheckinEntry;
import geologger.saints.com.geologger.models.CompanionEntry;
import geologger.saints.com.geologger.models.SentTrajectoryEntry;
import geologger.saints.com.geologger.models.TrajectoryEntry;
import geologger.saints.com.geologger.models.TrajectorySpanEntry;

/**
 * Created by Seiya on 2015/01/01.
 * tableの定義一覧を記述するクラス
 */
public class TableDefinitions {

    public static final String DBNAME = "geologger";
    public static final String TRAJECTORY = "trajectory";
    public static final String COMPANION = "companion";
    public static final String CHECKIN = "checkin";
    public static final String TRAJECTORY_SPAN = "trajectory_span";
    public static final String SENTTRAJECTORY = "sent_trajectory";

    public static final HashMap <String, LinkedHashMap<String, String>> tables = new HashMap<String, LinkedHashMap<String, String>>() {

        //trajectoryテーブルの定義
        final LinkedHashMap<String, String> TRAJECTORYTABLE = new LinkedHashMap<String, String>() {
            {put(TrajectoryEntry.TID, "TEXT");}
            {put(TrajectoryEntry.LATITUDE, "REAL");}
            {put(TrajectoryEntry.LONGITUDE, "REAL");}
            {put(TrajectoryEntry.TIMESTAMP, "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");}
            {put(TrajectoryEntry.ISGPSON, "BOOLEAN");}
            {put("PRIMARY KEY(" + TrajectoryEntry.TID + ", " + TrajectoryEntry.TIMESTAMP + ")", "");}

        };
        {put(TRAJECTORY, TRAJECTORYTABLE);}

        //companionテーブルの定義
        final LinkedHashMap<String, String> COMPANIONTABLE = new LinkedHashMap<String, String>() {
            {put(CompanionEntry.TID, "TEXT PRIMARY KEY");}
            {put(CompanionEntry.COMPANION, "TEXT");}
            {put(CompanionEntry.TIMESTAMP, "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");}
        };
        {put(COMPANION, COMPANIONTABLE);}

        //checkinテーブルの定義
        final LinkedHashMap<String, String> CHECKINTABLE = new LinkedHashMap<String, String>() {
            {put(CheckinEntry.TID, "TEXT");}
            {put(CheckinEntry.PLACEID, "TEXT");}
            {put(CheckinEntry.CATEGORYID, "TEXT");}
            {put(CheckinEntry.TIMESTAMP, "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");}
            {put("PRIMARY KEY(" + CheckinEntry.TID + ", " + CheckinEntry.TIMESTAMP + ")", "");}
        };
        {put(CHECKIN, CHECKINTABLE);}

        //trajectory_spanテーブルの定義
        final LinkedHashMap<String, String> TRAJECTORYSPANTABLE = new LinkedHashMap<String, String>() {
            {put(TrajectorySpanEntry.TID, "TEXT PRIMARY KEY");}
            {put(TrajectorySpanEntry.BEGIN, "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");}
            {put(TrajectorySpanEntry.END, "TEXT");}
        };
        {put(TRAJECTORY_SPAN, TRAJECTORYSPANTABLE);}

        //送信済みのトラジェクトリを管理するテーブルsentの定義
        final LinkedHashMap<String, String> SENTTABLE = new LinkedHashMap<String, String>() {
            {put(SentTrajectoryEntry.TID, "TEXT PRIMARY KEY");}
            {put(SentTrajectoryEntry.ISSENT, "BOOLEAN");}
        };
        {put(SENTTRAJECTORY, SENTTABLE);}

    };

    public static Set<String> tables() {
        return tables.keySet();
    }

    public static LinkedHashMap<String, String> getColumnDefinition(String tableName) {
        return tables.get(tableName);
    }

    public static Set<String> columns(String tableName) {
        return tables.get(tableName).keySet();
    }

    public static String options(String tableName, String columnName) {
        return tables.get(tableName).get(columnName);
    }
}
