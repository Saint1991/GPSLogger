package geologger.saints.com.geologger.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import geologger.saints.com.geologger.models.TableDefinitions;
import geologger.saints.com.geologger.models.TrajectorySpanEntry;
import geologger.saints.com.geologger.utils.TimestampUtil;

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
        boolean result = false;

        try {
            ContentValues insertValues = new ContentValues();
            insertValues.put(TrajectorySpanEntry.TID, tid);
            insertValues.put(TrajectorySpanEntry.BEGIN, begin);
            result = db.insert(TABLENAME, null, insertValues) != -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return result;
    }

    /**
     * Insert an entry
     * Start time will be automatically complemented
     * @param tid
     * @return
     */
    public boolean insert(String tid) {
        String begin = TimestampUtil.getTimestamp();
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
        boolean result = false;
        try {

            Cursor cursor = db.query(TABLENAME, null, TrajectorySpanEntry.TID + "=?", new String[]{tid}, null, null, null, null);
            try {

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

                ContentValues updateValues = new ContentValues();
                if (entry != null) {
                    updateValues.put(TrajectorySpanEntry.END, end);
                    result = db.update(TABLENAME, updateValues, TrajectorySpanEntry.TID + "=?", new String[]{tid}) == 1;
                } else if (target != null) {
                    updateValues.put(TrajectorySpanEntry.TID, target);
                    updateValues.put(TrajectorySpanEntry.END, end);
                    result = db.insert(TABLENAME, null, updateValues) != -1;
                }

            } catch (SQLiteException ex) {
                ex.printStackTrace();
            } finally {
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

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
     * Get timestamp of begining and end of the trajectory that has the passed tid
     * [0] => begin, [1] => end
     * if not found, null is returned
     * @param tid
     * @return
     */
    public String[] getStartAndEndTimestamp(String tid) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] timestamps = new String[2];
        try {

            Cursor cursor = db.query(TABLENAME, null, TrajectorySpanEntry.TID + "=?", new String[]{tid}, null, null, null, "1");
            try {
                if (!cursor.moveToFirst()) {
                    return null;
                }

                timestamps[0] = cursor.getString(cursor.getColumnIndex(TrajectorySpanEntry.BEGIN));
                timestamps[1] = cursor.getString(cursor.getColumnIndex(TrajectorySpanEntry.END));

            } catch (SQLiteException ex) {
                ex.printStackTrace();
            } finally {
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return timestamps;
    }

    /**
     * Check if the record that has passed tid has end time value
     * that means check if the logging of the trajectory has been completed
     * @param tid
     * @return true: completed, false: ongoing
     */
    public boolean isEnd(String tid) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String end = null;
        try {

            Cursor cursor = db.query(TABLENAME, null, TrajectorySpanEntry.TID + "=?", new String[]{tid}, null, null, null, null);
            try {

                if (!cursor.moveToFirst()) {
                    return false;
                }

                end = cursor.getString(cursor.getColumnIndex(TrajectorySpanEntry.END));
                if (end == null) {
                    return false;
                }
            } catch (SQLiteException ex) {
                ex.printStackTrace();
            } finally {
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

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
        boolean isExist = true;
        try {

            Cursor cursor = db.query(TABLENAME, null, TrajectorySpanEntry.TID + "=?", new String[]{tid}, null, null, null, null);
            try {
                isExist = cursor.getCount() > 0;

            } catch (SQLiteException ex) {
                ex.printStackTrace();
            } finally {
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return isExist;
    }

    /**
     * Get the tid whose trajectory is on logging
     * if logging is not going return null
     * @return tid that of currently logging trajectory
     */
    public String getLoggingTid() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String tid = null;
        try {

            Cursor cursor = db.query(TABLENAME, new String[]{TrajectorySpanEntry.TID}, TrajectorySpanEntry.END + " IS NULL", null, null, null, TrajectorySpanEntry.BEGIN + " DESC");
            try {

                if (!cursor.moveToFirst()) {
                    cursor.close();
                    db.close();
                    return null;
                }

                tid = cursor.getString(cursor.getColumnIndex(TrajectorySpanEntry.TID));
                if (tid.length() == 0 || tid == null) {
                    cursor.close();
                    db.close();
                    return null;
                }

            } catch (SQLiteException ex) {
                ex.printStackTrace();
            } finally {
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

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

        try {

            StringBuilder limitBuilder = new StringBuilder();
            if (limit > 0) {
                if (offset > 0) {
                    limitBuilder.append(offset + ", ");
                }
                limitBuilder.append(limit);
            }
            String limitStr = limitBuilder.length() == 0 ? null : limitBuilder.toString();

            Cursor cursor = db.query(TABLENAME, null, null, null, null, null, TrajectorySpanEntry.BEGIN + " ASC", limitStr);
            try {
                boolean isEOF = cursor.moveToFirst();
                while (isEOF) {
                    TrajectorySpanEntry entry = getEntryFromCursor(cursor);
                    ret.add(entry);
                    isEOF = cursor.moveToNext();
                }
            } catch (SQLiteException ex) {
                ex.printStackTrace();
            } finally {
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

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
        List<String> ret = new ArrayList<String>();
        try {

            Cursor cursor = db.query(TABLENAME, new String[]{TrajectorySpanEntry.TID}, null, null, null, null, TrajectorySpanEntry.BEGIN + " DESC");
            try {
                boolean isEOF = cursor.moveToFirst();
                while (isEOF) {
                    String tid = cursor.getString(cursor.getColumnIndex(TrajectorySpanEntry.TID));
                    ret.add(tid);
                    isEOF = cursor.moveToNext();
                }
            } catch (SQLiteException ex) {
                ex.printStackTrace();
            } finally {
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return ret;
    }

    /**
     * Get a list of tid that logging is completed
     * @return
     */
    public List<String> getLoggingFinishedTidList() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        List<String> ret = new ArrayList<String>();
        try {

            Cursor cursor = db.query(TABLENAME, new String[]{TrajectorySpanEntry.TID}, TrajectorySpanEntry.END + " IS NOT NULL", null, null, null, TrajectorySpanEntry.BEGIN + " ASC");
            try {

                boolean isEOF = cursor.moveToFirst();
                while (isEOF) {
                    String tid = cursor.getString(cursor.getColumnIndex(TrajectorySpanEntry.TID));
                    ret.add(tid);
                    isEOF = cursor.moveToNext();
                }

            } catch (SQLiteException ex) {
                ex.printStackTrace();
            } finally {
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        db.close();

        return ret;
    }

    /**
     * Get the number of Logs
     * @return
     */
    public int getLogCount() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String columnName = "COUNT(*)";
        int count = -1;

        try {

            Cursor cursor = db.query(TABLENAME, new String[]{columnName}, null, null, null, null, null);
            try {
                if (cursor.moveToFirst()) {
                    count = cursor.getInt(cursor.getColumnIndex(columnName));
                }
            } catch (SQLiteException ex) {
                ex.printStackTrace();
            } finally {
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return count;
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
