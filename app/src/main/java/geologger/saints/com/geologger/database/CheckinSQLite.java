package geologger.saints.com.geologger.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import geologger.saints.com.geologger.models.CheckinEntry;
import geologger.saints.com.geologger.models.TableDefinitions;
import geologger.saints.com.geologger.utils.TimestampGenerator;

/**
 * Created by Mizuno on 2015/01/29.
 */
@EBean
public class CheckinSQLite implements IRemoveBy {

    private final String TABLENAME = TableDefinitions.CHECKIN;

    @Bean
    BaseSQLiteOpenHelper mDbHelper;

    public CheckinSQLite(Context context) {
        mDbHelper = new BaseSQLiteOpenHelper(context);
    }

    //region insert

    /**
     * Insert an entry that has passed params
     * @param tid
     * @param placeId
     * @param categoryId
     * @param timestamp
     * @return success:true，fail:false
     */
    public boolean insert(String tid, String placeId, String categoryId, String timestamp, float latitude, float longitude, String placeName) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues insertValues = new ContentValues();
        insertValues.put(CheckinEntry.TID, tid);
        insertValues.put(CheckinEntry.PLACEID, placeId);
        insertValues.put(CheckinEntry.CATEGORYID, categoryId);
        insertValues.put(CheckinEntry.TIMESTAMP, timestamp);
        insertValues.put(CheckinEntry.LATITUDE, latitude);
        insertValues.put(CheckinEntry.LONGITUDE, longitude);
        insertValues.put(CheckinEntry.PLACENAME, placeName);

        boolean result = db.insert(TABLENAME, null, insertValues) != -1;
        db.close();

        return result;
    }

    /**
     * Insert an entry that has passed params
     * Timestamp will be automatically complemented
     * @param tid
     * @param placeId
     * @param categoryId
     * @return
     */
    public boolean insert(String tid, String placeId, String categoryId, float latitude, float longitude, String placeName) {
        String timestamp = TimestampGenerator.getTimestamp();
        return this.insert(tid, placeId, categoryId, timestamp, latitude, longitude, placeName );
    }

    /**
     * Insert an entry that has passed params
     * @param entry
     * @return success:true，fail:false
     */
    public boolean insert(CheckinEntry entry) {

        String tid = entry.getTid();
        String placeId = entry.getPlaceId();
        String categoryId = entry.getCategoryId();
        String timestamp = entry.getTimestamp();
        float latitude = entry.getLatitude();
        float longitude = entry.getLongitude();
        String placeName = entry.getPlaceName();

        return this.insert(tid, placeId, categoryId, timestamp, latitude, longitude, placeName);
    }

    //endregion

    //region remove

    /**
     * remove all entries that has passed tid
     * @param tid
     * @return the number of removed items
     */
    public int removeByTid(String tid) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int removedCount = db.delete(TABLENAME, CheckinEntry.TID + "=?", new String[]{tid});
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
        int removeCount = db.delete(TABLENAME, CheckinEntry.TIMESTAMP + "=?", new String[]{timestamp});
        db.close();

        return removeCount;
    }

    //endregion

    //region find

    /**
     * Get a list of CheckinEntry
     * @param tid
     * @param offset
     * @param limit
     * @return
     */
    public List<CheckinEntry> getCheckinList(String tid, int offset, int limit) {

        List<CheckinEntry> ret = new ArrayList<CheckinEntry>();
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

            Cursor cursor = db.query(TABLENAME, null, CheckinEntry.TID + "=?", new String[]{tid}, null, null, CheckinEntry.TIMESTAMP + " ASC", limitStr);

            boolean isEOF = cursor.moveToFirst();
            while (isEOF) {
                CheckinEntry entry = getEntryFromCursor(cursor);
                ret.add(entry);
                isEOF = cursor.moveToNext();
            }
            cursor.close();
            db.close();
        }

        return ret;
    }

    public List<CheckinEntry> getCheckinList(String tid) {
        return this.getCheckinList(tid, 0, 0);
    }

    public ArrayList<CheckinEntry> getCheckinArrayList(String tid) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, null, CheckinEntry.TID + "=?", new String[]{tid}, null, null, CheckinEntry.TIMESTAMP + " ASC");

        ArrayList<CheckinEntry> ret = new ArrayList<CheckinEntry>();
        boolean isEOF = cursor.moveToFirst();
        while (isEOF) {
            CheckinEntry entry = getEntryFromCursor(cursor);
            ret.add(entry);
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
    private CheckinEntry getEntryFromCursor(Cursor cursor) {

        if (cursor.isNull(cursor.getColumnIndex(CheckinEntry.TID))) {
            return null;
        }

        String tid = cursor.getString(cursor.getColumnIndex(CheckinEntry.TID));
        String placeId = cursor.getString(cursor.getColumnIndex(CheckinEntry.PLACEID));
        String categoryId = cursor.getString(cursor.getColumnIndex(CheckinEntry.CATEGORYID));
        String timestamp = cursor.getString(cursor.getColumnIndex(CheckinEntry.TIMESTAMP));
        float latitude = cursor.getFloat(cursor.getColumnIndex(CheckinEntry.LATITUDE));
        float longitude = cursor.getFloat(cursor.getColumnIndex(CheckinEntry.LONGITUDE));
        String placeName = cursor.getString(cursor.getColumnIndex(CheckinEntry.PLACENAME));

        CheckinEntry entry = new CheckinEntry(tid, placeId, categoryId, timestamp, latitude, longitude, placeName);

        return entry;
    }

    //endregion
}
