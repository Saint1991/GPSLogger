package geologger.saints.com.geologger.database;

import java.util.HashMap;

/**
 * Created by Seiya on 2015/01/01.
 * テーブル作成のためのスキーマを定義する．
 */
public class SQLiteModelDefinition {

    private String mTableName = null;
    private HashMap<String, String> mColumnDefinitions = null;

    /**
     * コンストラクタ
     * @param tableName table名
     */
    public SQLiteModelDefinition(String tableName) {
        this.mTableName = tableName;
        this.mColumnDefinitions = TableDefinitions.getColumnDefinition(tableName);
    }

    /**
     * コンストラクタで渡されたtable名とカラム定義から
     * CREATE TABLEクエリを文字列で返す
     * @return CREATE TABLE用クエリ
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

    /**
     * table名を取得する
     * @return table名
     */
    public String getTableName() {
        return this.mTableName;
    }
}
