package geologger.saints.com.geologger.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import geologger.saints.com.geologger.models.SQLiteModelDefinition;
import geologger.saints.com.geologger.models.TableDefinitions;

/**
 * Created by Seiya on 2015/01/01.
 */

public class BaseSQLiteOpenHelper extends SQLiteOpenHelper {

    private SQLiteModelDefinition mTableDefinition = null;

    public BaseSQLiteOpenHelper(Context context, SQLiteModelDefinition tableDefinition) {
        super(context, TableDefinitions.DBNAME, null, 1);
        this.mTableDefinition = tableDefinition;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = this.mTableDefinition.makeQuery();
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable(db);
        onCreate(db);
    }

    protected void dropTable(SQLiteDatabase db) {
        String tableName = this.mTableDefinition.getTableName();
        String query = "DROP TABLE IF EXISTS " + tableName;
        db.execSQL(query);
    }
}