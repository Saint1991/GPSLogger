package geologger.saints.com.geologger.models;

import java.util.HashMap;
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

    public static final HashMap <String, HashMap<String, String>> tables = new HashMap<String, HashMap<String, String>>() {

        //trajectoryテーブルの定義
        final String TRAJECTORY = "trajectory";
        final HashMap<String, String> TRAJECTORYTABLE = new HashMap<String, String>() {
            {put(TrajectoryEntry.TID, "TEXT");}
            {put(TrajectoryEntry.LATITUDE, "REAL");}
            {put(TrajectoryEntry.LONGITUDE, "REAL");}
            {put(TrajectoryEntry.TIMESTAMP, "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");}
            {put(TrajectoryEntry.ISGPSON, "BOOLEAN");}
            {put("PRIMARY KEY(" + TrajectoryEntry.TID + ", " + TrajectoryEntry.TIMESTAMP + ")", "");}

        };
        {put(TRAJECTORY, TRAJECTORYTABLE);}

        //companionテーブルの定義
        final String COMPANION = "companion";
        final HashMap<String, String> COMPANIONTABLE = new HashMap<String, String>() {
            {put(CompanionEntry.TID, "TEXT PRIMARY KEY");}
            {put(CompanionEntry.COMPANION, "TEXT");}
            {put(CompanionEntry.TIMESTAMP, "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");}
        };

        //checkinテーブルの定義
        final String CHECKIN = "checkin";
        final HashMap<String, String> CHECKINTABLE = new HashMap<String, String>() {
            {put(CheckinEntry.TID, "TEXT");}
            {put(CheckinEntry.PLACEID, "TEXT");}
            {put(CheckinEntry.CATEGORYID, "TEXT");}
            {put(CheckinEntry.TIMESTAMP, "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");}
            {put("PRIMARY KEY(" + CheckinEntry.TID + ", " + CheckinEntry.TIMESTAMP + ")", "");}
        };

        //trajectory_spanテーブルの定義
        final String TRAJECTORY_SPAN = "trajectory_span";
        final HashMap<String, String> TRAJECTORYSPANTABLE = new HashMap<String, String>() {
            {put(TrajectorySpanEntry.TID, "TEXT PRIMARY KEY");}
            {put(TrajectorySpanEntry.BEGIN, "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");}
            {put(TrajectorySpanEntry.END, "TEXT");}
        };

        //送信済みのトラジェクトリを管理するテーブルsentの定義
        final String SENTTRAJECTORY = "sent_trajectory";
        final HashMap<String, String> SENTTABLE = new HashMap<String, String>() {
            {put(SentTrajectoryEntry.TID, "TEXT PRIMARY KEY");}
            {put(SentTrajectoryEntry.ISSENT, "BOOLEAN");}
        };

    };

    public static Set<String> tables() {
        return tables.keySet();
    }

    public static HashMap<String, String> getColumnDefinition(String tableName) {
        return tables.get(tableName);
    }

    public static Set<String> columns(String tableName) {
        return tables.get(tableName).keySet();
    }

    public static String options(String tableName, String columnName) {
        return tables.get(tableName).get(columnName);
    }
}
