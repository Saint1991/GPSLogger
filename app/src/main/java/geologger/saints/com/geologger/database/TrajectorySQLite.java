package geologger.saints.com.geologger.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.LocationManager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

import java.util.ArrayList;
import java.util.List;

import geologger.saints.com.geologger.models.TableDefinitions;
import geologger.saints.com.geologger.models.TrajectoryEntry;
import geologger.saints.com.geologger.utils.TimestampUtil;

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
        boolean result = false;

        try {
            ContentValues insertValues = new ContentValues();
            insertValues.put(TrajectoryEntry.TID, tid);
            insertValues.put(TrajectoryEntry.LATITUDE, latitude);
            insertValues.put(TrajectoryEntry.LONGITUDE, longitude);
            insertValues.put(TrajectoryEntry.TIMESTAMP, timestamp);
            insertValues.put(TrajectoryEntry.ISGPSON, isGpsOn);
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
     * Timestamp will be automatically complemented
     * @param tid
     * @param latitude
     * @param longitude
     * @return success:true, fail:false
     */
    public boolean insert(String tid, float latitude, float longitude) {
        String timestamp = TimestampUtil.getTimestamp();
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
        int removedCount = -1;
        try {
            removedCount = db.delete(TABLENAME, TrajectoryEntry.TID + "=?", new String[]{tid});
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
     * Get First entry of the trajectory whose id is passed tid
     * @param tid
     * @return
     */
    public TrajectoryEntry getFirstEntry(String tid) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        TrajectoryEntry entry = null;
        try {

            Cursor cursor = db.query(TABLENAME, null, TrajectoryEntry.TID + "=?", new String[]{tid}, null, null, TrajectoryEntry.TIMESTAMP + " ASC", "1");
            try {
                if (!cursor.moveToFirst()) {
                    return null;
                }

                entry = getEntryFromCursor(cursor);

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

        return entry;
    }

    /**
     * Get Last entry of the trajectory whose id is passed tid
     * @param tid
     * @return
     */
    public TrajectoryEntry getLastEntry(String tid) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        TrajectoryEntry entry = null;
        try {

            Cursor cursor = db.query(TABLENAME, null, TrajectoryEntry.TID + "=?", new String[]{tid}, null, null, TrajectoryEntry.TIMESTAMP + " DESC", "1");
            try {

                if (!cursor.moveToFirst()) {
                    return null;
                }
                entry = getEntryFromCursor(cursor);

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

        return entry;
    }

    /**
     * Get all entries that have passed tid as a list
     * @param tid
     * @return
     */
    public List<TrajectoryEntry> getTrajectory(String tid) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        List<TrajectoryEntry> ret = new ArrayList<TrajectoryEntry>();
        try {

            Cursor cursor = db.query(TABLENAME, null, TrajectoryEntry.TID + "=?", new String[]{tid}, null, null, TrajectoryEntry.TIMESTAMP + " ASC");
            try {
                boolean isEOF = cursor.moveToFirst();
                while (isEOF) {
                    TrajectoryEntry entry = getEntryFromCursor(cursor);
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
