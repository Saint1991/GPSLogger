package geologger.saints.com.geologger.database;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by Seiya on 2015/01/01.
 * tableの定義一覧を記述するクラス
 */
public class TableDefinitions {

    public static final String DBNAME = "geologger";

    private static final String TID = "t_id";
    private static final String TIMESTAMP = "timestamp";

    public static final HashMap <String, HashMap<String, String>> tables = new HashMap<String, HashMap<String, String>>() {

        //trajectoryテーブルの定義
        final String TRAJECTORY = "trajectory";
        final HashMap<String, String> TRAJECTORYTABLE = new HashMap<String, String>() {
            {put(TID, "TEXT");}
            {put("latitude", "REAL");}
            {put("longitude", "REAL");}
            {put(TIMESTAMP, "DEFAULT CURRENT_TIMESTAMP");}
            {put("is_gps_on", "BOOLEAN");}
            {put("PRIMARY KEY(" + TID + ", " + TIMESTAMP + ")", "");}

        };
        {put(TRAJECTORY, TRAJECTORYTABLE);}

        //companionテーブルの定義
        final String COMPANION = "companion";
        final HashMap<String, String> COMPANIONTABLE = new HashMap<String, String>() {
            {put(TID, "TEXT PRIMARY KEY");}
            {put("companion", "TEXT");}
        };

        //checkinテーブルの定義
        final String CHECKIN = "checkin";
        final HashMap<String, String> CHECKINTABLE = new HashMap<String, String>() {
            {put(TID, "TEXT");}
            {put("place_id", "TEXT");}
            {put("category_id", "TEXT");}
            {put(TIMESTAMP, "DEFAULT CURRENT_TIMESTAMP");}
            {put("PRIMARY KEY(" + TID + ", " + TIMESTAMP + ")", "");}
        };

        //trajectory_spanテーブルの定義
        final String TRAJECTORY_SPAN = "trajectory_span";
        final HashMap<String, String> TRAJECTORYSPANTABLE = new HashMap<String, String>() {
            {put(TID, "TEXT PRIMARY KEY");}
            {put("begin", "DEFAULT CURRENT_TIMESTAMP");}
            {put("end", "TEXT");}
        };

        //送信済みのトラジェクトリを管理するテーブルsentの定義
        final String SENT = "sent";
        final HashMap<String, String> SENTTABLE = new HashMap<String, String>() {
            {put(TID, "TEXT PRIMARY KEY");}
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
