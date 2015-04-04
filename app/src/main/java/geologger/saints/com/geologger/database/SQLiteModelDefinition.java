package geologger.saints.com.geologger.database;

import java.util.HashMap;

import geologger.saints.com.geologger.models.TableDefinitions;

/**
 * Created by Seiya on 2015/01/01.
 * Utility class for handle a table information of TableDefinitions．
 */
public class SQLiteModelDefinition {

    private String mTableName = null;
    private HashMap<String, String> mColumnDefinitions = null;

    /**
     * Constructor
     * @param tableName
     */
    public SQLiteModelDefinition(String tableName) {
        this.mTableName = tableName;
        this.mColumnDefinitions = TableDefinitions.getColumnDefinition(tableName);
    }

    /**
     * Get TableName
     * @return table名
     */
    public String getTableName() {
        return this.mTableName;
    }

    /**
     * returning CREATE TABLE Query
     * @return
     */
    public String makeQuery() {

        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE IF NOT EXISTS ")
        .append(this.mTableName)
        .append("(");

        for (String columnName : this.mColumnDefinitions.keySet()) {
            String options = mColumnDefinitions.get(columnName);
            query.append(columnName + " " + options + ", ");
        }

        query.delete(query.length() - 2, query.length());
        query.append(")");

        String retQuery = query.toString();
        return retQuery;
    }


}
