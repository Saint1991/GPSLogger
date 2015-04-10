package geologger.saints.com.geologger.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import geologger.saints.com.geologger.models.SentTrajectoryEntry;
import geologger.saints.com.geologger.models.TableDefinitions;

/**
 * Created by Mizuno on 2015/01/29.
 */
@EBean
public class SentTrajectorySQLite {

    private final String TABLENAME = TableDefinitions.SENTTRAJECTORY;

    @Bean
    BaseSQLiteOpenHelper mDbHelper;

    public SentTrajectorySQLite() {}


    //region insert

    /**
     * Insert an entry that has passed params
     * @param tid
     * @param isSent
     * @return true: if success, false if failure
     */
    public boolean insert(String tid, boolean isSent) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        boolean result = false;
        try {
            ContentValues insertValues = new ContentValues();
            insertValues.put(SentTrajectoryEntry.TID, tid);
            insertValues.put(SentTrajectoryEntry.ISSENT, isSent);
            result = db.insertWithOnConflict(TABLENAME, null, insertValues, SQLiteDatabase.CONFLICT_REPLACE) != -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return result;
    }

    /**
     * Insert an entry that has passed params
     * @param entry
     * @return　true: if success, false: if failure
     */
    public boolean insert(SentTrajectoryEntry entry) {

        String tid = entry.getTid();
        boolean isSent = entry.getIsSent();

        return this.insert(tid, isSent);
    }

    /**
     * Insert List of entries
     * @param sentTidList
     * @return
     */
    public void insertSentTidList(List<String> sentTidList) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        try {
            for (String tid : sentTidList) {
                ContentValues insertValues = new ContentValues();
                insertValues.put(SentTrajectoryEntry.TID, tid);
                insertValues.put(SentTrajectoryEntry.ISSENT, true);
                db.insertWithOnConflict(TABLENAME, null, insertValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    //endregion

    //region remove

    /**
     * Remove an entry that has passed tid
     * @param tid
     * @return success:true, fail:false
     */
    public int removeByTid(String tid) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int removedCount = -1;
        try {
            removedCount = db.delete(TABLENAME, SentTrajectoryEntry.TID + "=?", new String[]{tid});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return removedCount;
    }

    //endregion

    //region find

    /**
     * check if the trajectory data that has passed tid has already been sent
     * @param tid
     * @return true: has been sent, false: not yet
     */
    public boolean isSent(String tid) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        boolean ret = false;
        try {

            Cursor cursor = db.query(TABLENAME, null, SentTrajectoryEntry.TID + "=?" , new String[]{tid}, null, null, null, "1");
            try {
                ret = cursor.moveToFirst();
                if (!ret) {
                    return ret;
                }

                ret = !cursor.isNull(cursor.getColumnIndex(SentTrajectoryEntry.ISSENT)) && cursor.getInt(cursor.getColumnIndex(SentTrajectoryEntry.ISSENT)) == 1;
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
     * Get all entries as a list
     * @return
     */
    public List<String> getSentTrajectoryList() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        List<String> sentList = new ArrayList<String>();
        try {

            Cursor cursor = db.query(TABLENAME, null, null, null, null, null, null);
            try {


                boolean isEOF = cursor.moveToFirst();
                while (isEOF) {
                    SentTrajectoryEntry entry = getEntryFromCursor(cursor);
                    if (entry.getIsSent()) {
                        sentList.add(entry.getTid());
                    }
                    isEOF = cursor.moveToNext();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return sentList;
    }

    //endregion

    //region utility
    /**
     * Get the entry from cursor
     * If cursor is invalid state, return null
     * @param cursor
     * @return
     */
    private SentTrajectoryEntry getEntryFromCursor(Cursor cursor) {

        if (cursor.isNull(cursor.getColumnIndex(SentTrajectoryEntry.TID))) {
            return null;
        }

        String tid = cursor.getString(cursor.getColumnIndex(SentTrajectoryEntry.TID));
        boolean isSent = cursor.getInt(cursor.getColumnIndex(SentTrajectoryEntry.ISSENT)) == 1;
        SentTrajectoryEntry entry = new SentTrajectoryEntry(tid, isSent);

        return entry;
    }

    //endregion

}
