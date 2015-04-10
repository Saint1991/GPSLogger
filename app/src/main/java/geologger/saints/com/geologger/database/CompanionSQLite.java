package geologger.saints.com.geologger.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import geologger.saints.com.geologger.models.CompanionEntry;
import geologger.saints.com.geologger.models.TableDefinitions;
import geologger.saints.com.geologger.utils.TimestampUtil;

/**
 * Created by Mizuno on 2015/01/29.
 */
@EBean
public class CompanionSQLite {

    private final String TABLENAME = TableDefinitions.COMPANION;

    @Bean
    BaseSQLiteOpenHelper mDbHelper;

    public CompanionSQLite() {}


    //region insert

    /**
     * Insert an entry that has passed params
     * @param tid
     * @param companion
     * @return Success:true fail:false
     */
    public boolean insert(String tid, String companion, String timestamp) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        boolean result = false;
        try {
            ContentValues insertValues = new ContentValues();
            insertValues.put(CompanionEntry.TID, tid);
            insertValues.put(CompanionEntry.COMPANION, companion);
            insertValues.put(CompanionEntry.TIMESTAMP, timestamp);
            result = db.insert(TABLENAME, null, insertValues) != -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return result;
    }

    /**
     * Insert an entry that has passed params
     * Timestamp will automatically be generated
     * @param tid
     * @param companion
     * @return success:true，fail:false
     */
    public boolean insert(String tid, String companion) {
        String timestamp = TimestampUtil.getTimestamp();
        return this.insert(tid, companion, timestamp);
    }

    /**
     * Insert entry that has passed params
     * @param entry
     * @return Success:true fail:false
     */
    public boolean insert(CompanionEntry entry) {
        String tid = entry.getTid();
        String companion = entry.getCompanion();
        String timestamp = entry.getTimestamp();
        return this.insert(tid, companion, timestamp);
    }

    //endregion

    //region remove

    /**
     * Remove all entries that have passed tid
     * @param tid
     * @return the number of removed items
     */
    public int removeByTid(String tid) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int removedCount = -1;
        try {
            removedCount = db.delete(TABLENAME, CompanionEntry.TID + "=?", new String[]{tid});
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
     * Get the list of CompanionEntry
     * List will be sorted by timestamp in an ascending order
     * @param tid
     * @return
     */
    public List<CompanionEntry> getCompanionList(String tid, int offset, int limit) {

        List<CompanionEntry> ret = new ArrayList<CompanionEntry>();
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

            Cursor cursor= db.query(TABLENAME, null, CompanionEntry.TID + "=?", new String[]{tid}, null, null, CompanionEntry.TIMESTAMP + " ASC", limitStr);
            try {
                boolean isEOF = cursor.moveToFirst();
                while (isEOF) {
                    CompanionEntry entry = getEntryFromCursor(cursor);
                    ret.add(entry);
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

        return ret;
    }


    /**
     * Get the list of CompanionEntry
     * List will be sorted by timestamp in an ascending order
     * @param tid
     * @return
     */
    public List<CompanionEntry> getCompanionList(String tid) {
        return this.getCompanionList(tid, 0, 0);
    }

    /**
     * Get the CompanionEntry that has passed tid
     * @param tid
     * @return
     */
    public CompanionEntry getCompanion(String tid) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        CompanionEntry ret = null;

        try {

            Cursor cursor = db.query(TABLENAME, null, CompanionEntry.TID + "=?", new String[]{tid}, null, null, null, "1");
            try {
                if (cursor.moveToFirst()) {
                    ret = getEntryFromCursor(cursor);
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

    //endregion

    //region utility

    /**
     * Get corresponding entry to the passed cursor
     * If cursor is invalid return null
     * @param cursor
     * @return
     */
    private CompanionEntry getEntryFromCursor(Cursor cursor) {

        if (cursor.isNull(cursor.getColumnIndex(CompanionEntry.TID))) {
            return null;
        }

        String tid = cursor.getString(cursor.getColumnIndex(CompanionEntry.TID));
        String companion = cursor.getString(cursor.getColumnIndex(CompanionEntry.COMPANION));
        String timestamp = cursor.getString(cursor.getColumnIndex(CompanionEntry.TIMESTAMP));
        CompanionEntry entry = new CompanionEntry(tid, companion, timestamp);

        return entry;
    }

    //endregion
}
