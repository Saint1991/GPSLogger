package geologger.saints.com.geologger.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

/**
 * Created by Mizuno on 2015/04/08.
 */
@EBean
public abstract class MyBaseSQLite<T> {

    private final String TAG = getClass().getSimpleName();

    public interface IRemovedBy {
        public int removeByTid(String tid);
        public int removeByTimestamp(String timestamp);
    }

    public interface IExecuteWithSQLiteDatabase {
        public void execute(SQLiteDatabase db);
    }

    @Bean
    BaseSQLiteOpenHelper mSQLiteOpenHelper;

    protected void executeWithReadableDatabase(IExecuteWithSQLiteDatabase callback) {
        SQLiteDatabase db = mSQLiteOpenHelper.getReadableDatabase();
        execute(db, callback);
    }

    protected void executeWithWritableDatabase(IExecuteWithSQLiteDatabase callback) {
        SQLiteDatabase db = mSQLiteOpenHelper.getWritableDatabase();
        execute(db, callback);
    }

    private void execute(SQLiteDatabase db, IExecuteWithSQLiteDatabase callback) {

        try {

            if (db.isOpen()) {
                callback.execute(db);
            } else {
                Log.i(TAG, "Db is not opened");
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            db.close();
        }
    }
}
