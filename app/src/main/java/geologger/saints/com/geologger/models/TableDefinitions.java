package geologger.saints.com.geologger.models;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by Seiya on 2015/01/01.
 */
public class TableDefinitions {

    public static final String DBNAME = "geologger";

    public static final HashMap <String, HashMap<String, String>> tables = new HashMap<String, HashMap<String, String>>() {

        final String TRAJECTORY = "trajectory";
        final HashMap<String, String> TRAJECTORYTABLE = new HashMap<String, String>() {
            {put("latitude", "REAL");}
            {put("longitude", "REAL");}
            {put("timestamp", "DEFAULT CURRENT_TIMESTAMP");}
            {put("t_id", "INTEGER");}
        };
        {put(TRAJECTORY, TRAJECTORYTABLE);}

        final String DATAQUEUE = "SendDataQueue";
        final HashMap<String, String> SENDDATAQUEUETABLE = new HashMap<String, String>() {
            {put("latitude", "REAL");}
            {put("longitude", "REAL");}
            {put("timestamp", "DEFAULT CURRENT_TIMESTAMP");}
            {put("t_id", "INTEGER");}
        };
        {put(DATAQUEUE, SENDDATAQUEUETABLE);}
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
