package geologger.saints.com.geologger.models;

import java.util.HashMap;

/**
 * Created by Seiya on 2015/01/01.
 */
public class SQLiteModelDefinition {

    private String tableName = null;
    private HashMap<String, String> columnDefinitions = null;

    public SQLiteModelDefinition(String tableName, HashMap<String, String> columnDefinitions) {
        this.tableName = tableName;
        this.columnDefinitions = columnDefinitions;
    }

    public String makeQuery() {

        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE IF NOT EXISTS ")
        .append(this.tableName)
        .append("(");

        for (String columnName : this.columnDefinitions.keySet()) {
            String options = columnDefinitions.get(columnName);
            query.append(columnName + " " + options + ", ");
        }

        query.delete(query.length() - 2, query.length());
        query.append(")");

        String retQuery = query.toString();
        return retQuery;
    }

    public String getTableName() {
        return this.tableName;
    }
}
