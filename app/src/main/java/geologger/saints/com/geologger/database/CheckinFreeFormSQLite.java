package geologger.saints.com.geologger.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import geologger.saints.com.geologger.models.CheckinFreeFormEntry;
import geologger.saints.com.geologger.models.TableDefinitions;
import geologger.saints.com.geologger.utils.TimestampGenerator;

/**
 * Created by Mizuno on 2015/01/30.
 */
@EBean
public class CheckinFreeFormSQLite implements IRemoveBy {

    private final String TABLENAME = TableDefinitions.CHECKIN_FREE_FORM;

    @Bean
    BaseSQLiteOpenHelper mDbHelper;

    public CheckinFreeFormSQLite(Context context) {
        mDbHelper = new BaseSQLiteOpenHelper(context);
    }


    //region insert

    /**
     * insert an entry that has passed parameters
     * @param tid
     * @param placeName
     * @param timestamp
     * @param latitude
     * @param longitude
     * @return
     */
    public boolean insert(String tid, String placeName, String timestamp, float latitude, float longitude) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues insertValues = new ContentValues();
        insertValues.put(CheckinFreeFormEntry.TID, tid);
        insertValues.put(CheckinFreeFormEntry.PLACENAME, placeName);
        insertValues.put(CheckinFreeFormEntry.TIMESTAMP, timestamp);
        insertValues.put(CheckinFreeFormEntry.LATITUDE, latitude);
        insertValues.put(CheckinFreeFormEntry.LONGITUDE, longitude);

        boolean result = db.insert(TABLENAME, null, insertValues) != -1;
        db.close();

        return result;
    }

    /**
     * insert an entry that has passed parameters
     * timestamp will be automatically complemented
     * @param tid
     * @param placeName
     * @return
     */
    public boolean insert(String tid, String placeName, float latitude, float longitude) {
        String timestamp = TimestampGenerator.getTimestamp();
        return this.insert(tid, placeName, timestamp, latitude, longitude);
    }

    /**
     * insert an entry
     * @param entry
     * @return
     */
    public boolean insert(CheckinFreeFormEntry entry) {

        String tid = entry.getTid();
        String placeName = entry.getPlaceName();
        String timestamp = entry.getTimestamp();
        float latitude = entry.getLatitude();
        float longitude = entry.getLongitude();

        return this.insert(tid, placeName, timestamp, latitude, longitude);
    }



    //endregion

    //region remove

    /**
     * remove all entries that has passed tid
     * @param tid
     * @return success:true, fail:false
     */
    public int removeByTid(String tid) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int removedCount = db.delete(TABLENAME, CheckinFreeFormEntry.TID + "=?", new String[]{tid});
        db.close();

        return removedCount;
    }

    /**
     * remove all entries that has passed timestamp
     * @param timestamp
     * @return
     */
    public int removeByTimestamp(String timestamp) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int removeCount = db.delete(TABLENAME, CheckinFreeFormEntry.TIMESTAMP + "=?", new String[]{timestamp});
        db.close();

        return removeCount;
    }

    //endregion

    //region find

    /**
     * Get a list of CheckinFreeFormEntry
     * @param tid
     * @param offset
     * @param limit
     * @return
     */
    public List<CheckinFreeFormEntry> getCheckinFreeFormList(String tid, int offset, int limit) {

        List<CheckinFreeFormEntry> ret = new ArrayList<CheckinFreeFormEntry>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        if (db.isOpen()) {

            StringBuilder limitBuilder = new StringBuilder();
            if (limit > 0) {
                if (offset > 0) {
                    limitBuilder.append(offset + ", ");
                }
                limitBuilder.append(limit);
            }
            String limitStr = limitBuilder.length() == 0 ? null : limitBuilder.toString();

            Cursor cursor = db.query(TABLENAME, null, CheckinFreeFormEntry.TID + "=?", new String[]{tid}, null, null, CheckinFreeFormEntry.TIMESTAMP + " ASC", limitStr);

            boolean isEOF = cursor.moveToFirst();
            while (isEOF) {
                CheckinFreeFormEntry entry = getEntryFromCursor(cursor);
                ret.add(entry);
                isEOF = cursor.moveToNext();
            }
            cursor.close();
            db.close();
        }

        return ret;
    }

    /**
     * get All Entries As a list
     * @param tid
     * @return
     */
    public List<CheckinFreeFormEntry> getCheckinFreeFormList(String tid) {
        return this.getCheckinFreeFormList(tid, 0, 0);
    }

    //endregion

    //region utility

    /**
     * Get the entry from cursor
     * If cursor is invalid state, return null
     * @param cursor
     * @return
     */
    private CheckinFreeFormEntry getEntryFromCursor(Cursor cursor) {

        if (cursor.isNull(cursor.getColumnIndex(CheckinFreeFormEntry.TID))) {
            return null;
        }

        String tid = cursor.getString(cursor.getColumnIndex(CheckinFreeFormEntry.TID));
        String placeName = cursor.getString(cursor.getColumnIndex(CheckinFreeFormEntry.PLACENAME));
        String timestamp = cursor.getString(cursor.getColumnIndex(CheckinFreeFormEntry.TIMESTAMP));
        float latitude = cursor.getFloat(cursor.getColumnIndex(CheckinFreeFormEntry.LATITUDE));
        float longitude = cursor.getFloat(cursor.getColumnIndex(CheckinFreeFormEntry.LONGITUDE));

        CheckinFreeFormEntry entry = new CheckinFreeFormEntry(tid, placeName, timestamp, latitude, longitude);

        return entry;
    }

    //endregion
}
