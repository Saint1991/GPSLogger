package geologger.saints.com.geologger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.androidannotations.annotations.EBean;

import java.util.Set;

import geologger.saints.com.geologger.models.TableDefinitions;

/**
 * Created by Seiya on 2015/01/01.
 * Implementation of SQLiteOpenHelper
 * Create Table referring to SQLiteModelDefinition
 * */
@EBean
public class BaseSQLiteOpenHelper extends SQLiteOpenHelper {

    private final String TAG = getClass().getSimpleName();

    public BaseSQLiteOpenHelper(Context context) {
        super(context, TableDefinitions.DBNAME, null, 4);
    }

    //Create All Tables that are stated in the TableDefinitions
    @Override
    public void onCreate(SQLiteDatabase db) {
        Set<String> tables = TableDefinitions.tables();
        for (String tableName : tables) {
            String query = new SQLiteModelDefinition(tableName).makeQuery();
            db.execSQL(query);
            Log.i(TAG, query);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade");
        onCreate(db);
    }

    //Remove All Tables that are stated in TableDefinitions
    protected void dropTable(SQLiteDatabase db) {
        Set<String> tables = TableDefinitions.tables();
        for (String tableName : tables) {
            String query = "DROP TABLE IF EXISTS " + tableName;
            db.execSQL(query);
        }
    }
}
