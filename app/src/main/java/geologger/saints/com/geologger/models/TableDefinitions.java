package geologger.saints.com.geologger.models;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;


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
    public static final String CHECKIN_FREE_FORM = "checkin_free_form";
    public static final String TRAJECTORY_PROPERTIES = "trajectory_property";
    public static final String TRAJECTORY_STATISTICAL_INFORMATION = "trajectory_statistical_information";
    public static final String PHOTOS = "photos";

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
            {put(CompanionEntry.COMPANION, "TEXT NOT NULL");}
            {put(CompanionEntry.TIMESTAMP, "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");}
        };
        {put(COMPANION, COMPANIONTABLE);}

        //checkinテーブルの定義
        final LinkedHashMap<String, String> CHECKINTABLE = new LinkedHashMap<String, String>() {
            {put(CheckinEntry.TID, "TEXT");}
            {put(CheckinEntry.PLACEID, "TEXT NOT NULL");}
            {put(CheckinEntry.CATEGORYID, "TEXT DEFAULT NULL");}
            {put(CheckinEntry.TIMESTAMP, "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");}
            {put(CheckinEntry.LATITUDE, "REAL");}
            {put(CheckinEntry.LONGITUDE, "REAL");}
            {put(CheckinEntry.PLACENAME, "TEXT NOT NULL");}
            {put("PRIMARY KEY(" + CheckinEntry.TID + ", " + CheckinEntry.TIMESTAMP + ")", "");}
        };
        {put(CHECKIN, CHECKINTABLE);}

        //フリーフォームのチェックイン入力用
        final LinkedHashMap<String, String> CHECKINFREEFORMTABLE = new LinkedHashMap<String, String>() {
            {put(CheckinFreeFormEntry.TID, "TEXT");}
            {put(CheckinFreeFormEntry.PLACENAME, "TEXT NOT NULL");}
            {put(CheckinFreeFormEntry.TIMESTAMP, "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");}
            {put(CheckinFreeFormEntry.LATITUDE, "REAL");}
            {put(CheckinFreeFormEntry.LONGITUDE, "REAL");}
            {put("PRIMARY KEY (" + CheckinFreeFormEntry.TID + ", " + CheckinFreeFormEntry.TIMESTAMP + ")" , "");}
        };
        {put(CHECKIN_FREE_FORM, CHECKINFREEFORMTABLE);}

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

        //トラジェクトリの付随情報
        final LinkedHashMap<String, String> TRAJECTORY_PROPERTIES_TABLE = new LinkedHashMap<String, String>() {
            {put(TrajectoryPropertyEntry.TID, "TEXT PRIMARY KEY");}
            {put(TrajectoryPropertyEntry.TITLE, "TEXT NOT NULL");}
            {put(TrajectoryPropertyEntry.DESCRIPTION, "TEXT DEFAULT NULL");}
        };
        {put(TRAJECTORY_PROPERTIES, TRAJECTORY_PROPERTIES_TABLE);}

        //トラジェクトリの統計情報
        final LinkedHashMap<String, String> TRAJECTORY_STATISTICAL_INFORMATION_TABLE = new LinkedHashMap<String, String>() {
            {put(TrajectoryStatisticalEntry.TID, "TEXT");}
            {put(TrajectoryStatisticalEntry.DURATION, "REAL NOT NULL");}
            {put(TrajectoryStatisticalEntry.DISTANCE, "REAL NOT NULL");}
            {put(TrajectoryStatisticalEntry.SPEED, "REAL NOT NULL");}
            {put(TrajectoryStatisticalEntry.TIMESTAMP, "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");}
            {put("PRIMARY KEY (" + TrajectoryStatisticalEntry.TID + ", " + TrajectoryStatisticalEntry.TIMESTAMP + ")" , "");}
        };
        {put(TRAJECTORY_STATISTICAL_INFORMATION, TRAJECTORY_STATISTICAL_INFORMATION_TABLE);}

        //写真情報
        final LinkedHashMap<String, String> PHOTO_TABLE = new LinkedHashMap<String, String>() {
            {put(PhotoEntry.TID, "TEXT");}
            {put(PhotoEntry.LATITUDE, "REAL NOT NULL");}
            {put(PhotoEntry.LONGITUDE, "REAL NOT NULL");}
            {put(PhotoEntry.TIMESTAMP, "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");}
            {put(PhotoEntry.FILEPATH, "TEXT NOT NULL");}
            {put(PhotoEntry.MEMO, "TEXT");}
        };
        {put(PHOTOS, PHOTO_TABLE);}
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
