package geologger.saints.com.geologger.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.HashMap;
import java.util.Map;

import geologger.saints.com.geologger.models.TableDefinitions;
import geologger.saints.com.geologger.models.TrajectoryPropertyEntry;

/**
 * Created by Mizuno on 2015/04/03.
 */
@EBean
public class TrajectoryPropertySQLite {

    private final String TABLENAME = TableDefinitions.TRAJECTORY_PROPERTIES;

    @Bean
    BaseSQLiteOpenHelper mDbHelper;

    public TrajectoryPropertySQLite() {}

    //region insert

    /**
     * insert an entry that has passed params
     * @param tid
     * @param title
     * @param description
     * @return
     */
    public boolean insert(String tid, String title, String description) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues insertValues = new ContentValues();
        insertValues.put(TrajectoryPropertyEntry.TID, tid);
        insertValues.put(TrajectoryPropertyEntry.TITLE, title);
        insertValues.put(TrajectoryPropertyEntry.DESCRIPTION, description);

        boolean result = db.insert(TABLENAME, null, insertValues) != -1;
        db.close();

        return result;
    }

    /**
     * Insert an entry
     * @param entry
     * @return
     */
    public boolean insert(TrajectoryPropertyEntry entry) {
        String tid = entry.getTid();
        String title = entry.getTitle();
        String description = entry.getDescription();
        return this.insert(tid, title, description);
    }

    //endregion

    //region remove

    /**
     * Remove the corresponding entry to the passed tid
     * @param tid
     * @return
     */
    public int removeByTid(String tid) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int removeCount = db.delete(TABLENAME, TrajectoryPropertyEntry.TID + "=?", new String[]{tid});
        db.close();

        return removeCount;
    }

    //endregion

    //region find

    /**
     * Load an entry that have the passed TID
     * @param tid
     * @return
     */
    public TrajectoryPropertyEntry getEntry(String tid) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, null, TrajectoryPropertyEntry.TID + "=?", new String[]{tid}, null, null, null, "1");

        TrajectoryPropertyEntry ret = null;
        if (cursor.moveToFirst()) {
            ret = getEntryFromCursor(cursor);
        }

        cursor.close();
        db.close();

        return ret;
    }

    //endregion

    //region utility

    /**
     * Get the entry from cursor
     * If cursor is invalid state, return null
     * @param cursor
     * @return
     */
    private TrajectoryPropertyEntry getEntryFromCursor(Cursor cursor) {

        if (cursor.isNull(cursor.getColumnIndex(TrajectoryPropertyEntry.TID))) {
            return null;
        }

        String tid = cursor.getString(cursor.getColumnIndex(TrajectoryPropertyEntry.TID));
        String title = cursor.getString(cursor.getColumnIndex((TrajectoryPropertyEntry.TITLE)));
        String description = cursor.getString(cursor.getColumnIndex(TrajectoryPropertyEntry.DESCRIPTION));
        TrajectoryPropertyEntry entry = new TrajectoryPropertyEntry(tid, title, description);

        return entry;
    }

    //endregion
}
