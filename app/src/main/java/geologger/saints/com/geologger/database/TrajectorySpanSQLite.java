package geologger.saints.com.geologger.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import geologger.saints.com.geologger.models.TableDefinitions;
import geologger.saints.com.geologger.models.TrajectorySpanEntry;
import geologger.saints.com.geologger.utils.TimestampGenerator;

/**
 * Created by Mizuno on 2015/01/29.
 */
@EBean
public class TrajectorySpanSQLite  {

    private final String TABLENAME = TableDefinitions.TRAJECTORY_SPAN;

    @Bean
    BaseSQLiteOpenHelper mDbHelper;

    public TrajectorySpanSQLite() {}


    //region insert

    /**
     * Insert an entry that has passed params
     * @param tid
     * @param begin
     * @return
     */
    public boolean insert(String tid, String begin) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues insertValues = new ContentValues();
        insertValues.put(TrajectorySpanEntry.TID, tid);
        insertValues.put(TrajectorySpanEntry.BEGIN, begin);

        boolean result = db.insert(TABLENAME, null, insertValues) != -1;
        db.close();

        return result;
    }

    /**
     * Insert an entry
     * Start time will be automatically complemented
     * @param tid
     * @return
     */
    public boolean insert(String tid) {
        String begin = TimestampGenerator.getTimestamp();
        return this.insert(tid, begin);
    }

    /**
     * Set End time to the entry that has passed tid
     * @param tid
     * @param end
     * @return success:true, fail:false
     */
    public boolean setEnd(String tid, String end) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Cursor cursor = db.query(TABLENAME, null, TrajectorySpanEntry.TID + "=?", new String[]{tid}, null, null, null, null);
        TrajectorySpanEntry entry = null;
        if (cursor.moveToFirst()) {
            entry = getEntryFromCursor(cursor);
        }

        String target = null;
        if (entry == null) {
            target = getLoggingTid();
        }

        if (target == null && entry == null) {
            return false;
        }

        boolean result = false;
        ContentValues updateValues = new ContentValues();
        if (entry != null) {
            updateValues.put(TrajectorySpanEntry.END, end);
            result = db.update(TABLENAME, updateValues, TrajectorySpanEntry.TID + "=?", new String[]{tid}) == 1;
        } else if (target != null) {
            updateValues.put(TrajectorySpanEntry.TID, target);
            updateValues.put(TrajectorySpanEntry.END, end);
            result = db.insert(TABLENAME, null, updateValues) != -1;
        }

        db.close();

        return result;
    }


    //endregion

    //region remove

    /**
     * Remove all entries that has passed tid
     * @param tid
     * @return success:true, fail:false
     */
    public int removeByTid(String tid) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int removedCount = db.delete(TABLENAME, TrajectorySpanEntry.TID + "=?", new String[]{tid});
        db.close();

        return removedCount;
    }

    //endregion

    //region find

    /**
     * Check if the record that has passed tid has end time value
     * that means check if the logging of the trajectory has been completed
     * @param tid
     * @return true: completed, false: ongoing
     */
    public boolean isEnd(String tid) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, null, TrajectorySpanEntry.TID + "=?", new String[]{tid}, null, null, null, null);

        if (!cursor.moveToFirst()) {
            return false;
        }

        String end = cursor.getString(cursor.getColumnIndex(TrajectorySpanEntry.END));
        if (end == null) {
            return false;
        }

        cursor.close();
        db.close();

        end = end.toLowerCase();
        return end != "null";
    }

    /**
     * Check if passed tid already exist
     * @param tid
     * @return
     */
    public boolean isExistTid(String tid) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, null, TrajectorySpanEntry.TID + "=?", new String[]{tid}, null, null, null, null);

        boolean isExist = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return isExist;
    }

    /**
     * Get the tid whose trajectory is on logging
     * if logging is not going return null
     * @return tid that of currently logging trajectory
     */
    public String getLoggingTid() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, new String[]{TrajectorySpanEntry.TID}, TrajectorySpanEntry.END + " IS NULL", null, null, null, TrajectorySpanEntry.BEGIN + " DESC");

        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return null;
        }

        String tid = cursor.getString(cursor.getColumnIndex(TrajectorySpanEntry.TID));
        if (tid.length() == 0 || tid == null) {
            cursor.close();
            db.close();
            return null;
        }

        cursor.close();
        db.close();

        return tid;
    }

    /**
     * Get a list of TrajectorySpanEntry
     * @param offset
     * @param limit
     * @return
     */
    public List<TrajectorySpanEntry> getSpanList(int offset, int limit) {

        List<TrajectorySpanEntry> ret = new ArrayList<TrajectorySpanEntry>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        StringBuilder limitBuilder = new StringBuilder();
        if (limit > 0) {
            if (offset > 0) {
                limitBuilder.append(offset + ", ");
            }
            limitBuilder.append(limit);
        }
        String limitStr = limitBuilder.length() == 0 ? null : limitBuilder.toString();

        Cursor cursor = db.query(TABLENAME, null, null, null, null, null, TrajectorySpanEntry.BEGIN + " ASC", limitStr);

        boolean isEOF = cursor.moveToFirst();
        while (isEOF) {
            TrajectorySpanEntry entry = getEntryFromCursor(cursor);
            ret.add(entry);
            isEOF = cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return ret;
    }

    /**
     * Get a list of TrajectorySpanEntry
     * @return
     */
    public List<TrajectorySpanEntry> getSpanList() {
        return this.getSpanList(0, 0);
    }

    /**
     * Get all tids stored in the DB
     * @return
     */
    public List<String> getTidList() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, new String[]{TrajectorySpanEntry.TID}, null, null, null, null, TrajectorySpanEntry.BEGIN + " DESC");

        List<String> ret = new ArrayList<String>();
        boolean isEOF = cursor.moveToFirst();
        while (isEOF) {
            String tid = cursor.getString(cursor.getColumnIndex(TrajectorySpanEntry.TID));
            ret.add(tid);
            isEOF = cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return ret;
    }

    /**
     * Get a list of tid that logging is completed
     * @return
     */
    public List<String> getLoggingFinishedTidList() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, new String[]{TrajectorySpanEntry.TID}, TrajectorySpanEntry.END + " IS NOT NULL", null, null, null, TrajectorySpanEntry.BEGIN + " ASC");

        List<String> ret = new ArrayList<String>();
        boolean isEOF = cursor.moveToFirst();
        while (isEOF) {
            String tid = cursor.getString(cursor.getColumnIndex(TrajectorySpanEntry.TID));
            ret.add(tid);
            isEOF = cursor.moveToNext();
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
    private TrajectorySpanEntry getEntryFromCursor(Cursor cursor) {

        if(cursor.getCount() == 0 || cursor.isNull(cursor.getColumnIndex(TrajectorySpanEntry.TID))) {
            return null;
        }

        String tid = cursor.getString(cursor.getColumnIndex(TrajectorySpanEntry.TID));
        String begin = cursor.getString(cursor.getColumnIndex(TrajectorySpanEntry.BEGIN));
        String end = cursor.getString(cursor.getColumnIndex(TrajectorySpanEntry.END));
        TrajectorySpanEntry entry = new TrajectorySpanEntry(tid, begin, end);

        return entry;
    }

    //endregion
}
