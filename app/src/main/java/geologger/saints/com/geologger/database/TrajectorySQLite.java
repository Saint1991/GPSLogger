package geologger.saints.com.geologger.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

import java.util.ArrayList;
import java.util.List;

import geologger.saints.com.geologger.models.TableDefinitions;
import geologger.saints.com.geologger.models.TrajectoryEntry;
import geologger.saints.com.geologger.utils.TimestampGenerator;

/**
 * Created by Mizuno on 2015/01/28.
 */
@EBean
public class TrajectorySQLite {

    private final String TABLENAME = TableDefinitions.TRAJECTORY;

    @SystemService
    LocationManager mLocationManager;

    @Bean
    BaseSQLiteOpenHelper mDbHelper;

    public TrajectorySQLite() {}

    //region insert

    /**
     * Insert an entry that has passed params
     * @param tid
     * @param latitude
     * @param longitude
     * @param timestamp
     * @param isGpsOn
     * @return success:true，fail:false
     */
    public boolean insert(String tid, float latitude, float longitude, String timestamp, boolean isGpsOn) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues insertValues = new ContentValues();
        insertValues.put(TrajectoryEntry.TID, tid);
        insertValues.put(TrajectoryEntry.LATITUDE, latitude);
        insertValues.put(TrajectoryEntry.LONGITUDE, longitude);
        insertValues.put(TrajectoryEntry.TIMESTAMP, timestamp);
        insertValues.put(TrajectoryEntry.ISGPSON, isGpsOn);

        boolean result = db.insert(TABLENAME, null, insertValues) != -1;
        db.close();

        return result;
    }

    /**
     * Insert an entry that has passed params
     * Timestamp will be automatically complemented
     * @param tid
     * @param latitude
     * @param longitude
     * @return success:true, fail:false
     */
    public boolean insert(String tid, float latitude, float longitude) {
        String timestamp = TimestampGenerator.getTimestamp();
        boolean isGpsOn = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return this.insert(tid, latitude, longitude, timestamp, isGpsOn);
    }

    /**
     * Insert an entry that has passed params
     * @param entry
     * @return success:true, fail:false
     */
    public boolean insert(TrajectoryEntry entry) {
        String tid = entry.getTid();
        float latitude = entry.getLatitude();
        float longitude = entry.getLongitude();
        String timestamp = entry.getTimestamp();
        boolean isGpsOn = entry.getIsGpsOn();
        return this.insert(tid, latitude, longitude, timestamp, isGpsOn);
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
        int removedCount = db.delete(TABLENAME, TrajectoryEntry.TID + "=?", new String[]{tid});
        db.close();

        return removedCount;
    }

    //endregion

    //region find

    /**
     * Get First entry of the trajectory whose id is passed tid
     * @param tid
     * @return
     */
    public TrajectoryEntry getFirstEntry(String tid) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, null, TrajectoryEntry.TID + "=?", new String[]{tid}, null, null, TrajectoryEntry.TIMESTAMP + " ASC", "1");
        if (!cursor.moveToFirst()) {
            return null;
        }

        TrajectoryEntry entry = getEntryFromCursor(cursor);
        cursor.close();
        db.close();

        return entry;
    }

    /**
     * Get Last entry of the trajectory whose id is passed tid
     * @param tid
     * @return
     */
    public TrajectoryEntry getLastEntry(String tid) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, null, TrajectoryEntry.TID + "=?", new String[]{tid}, null, null, TrajectoryEntry.TIMESTAMP + " DESC", "1");
        if (!cursor.moveToFirst()) {
            return null;
        }

        TrajectoryEntry entry = getEntryFromCursor(cursor);
        cursor.close();
        db.close();

        return entry;
    }

    /**
     * Get all entries that has passed tid as a list
     * @param tid
     * @return
     */
    public List<TrajectoryEntry> getTrajectory(String tid) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, null, TrajectoryEntry.TID + "=?", new String[]{tid}, null, null, TrajectoryEntry.TIMESTAMP + " ASC");

        List<TrajectoryEntry> ret = new ArrayList<TrajectoryEntry>();
        boolean isEOF = cursor.moveToFirst();
        while (isEOF) {
            TrajectoryEntry entry = getEntryFromCursor(cursor);
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
    private TrajectoryEntry getEntryFromCursor(Cursor cursor) {

        if (cursor.isNull(cursor.getColumnIndex(TrajectoryEntry.TID))) {
            return null;
        }

        String tid = cursor.getString(cursor.getColumnIndex(TrajectoryEntry.TID));
        float latitude = cursor.getFloat(cursor.getColumnIndex(TrajectoryEntry.LATITUDE));
        float longitude = cursor.getFloat(cursor.getColumnIndex(TrajectoryEntry.LONGITUDE));
        String timestamp = cursor.getString(cursor.getColumnIndex(TrajectoryEntry.TIMESTAMP));
        boolean isGpsOn = cursor.getInt(cursor.getColumnIndex(TrajectoryEntry.ISGPSON)) == 1;
        TrajectoryEntry entry = new TrajectoryEntry(tid, latitude, longitude, timestamp, isGpsOn);

        return entry;
    }

    //endregion

}
