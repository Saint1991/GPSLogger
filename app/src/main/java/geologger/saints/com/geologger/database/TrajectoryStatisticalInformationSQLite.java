package geologger.saints.com.geologger.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import geologger.saints.com.geologger.models.TableDefinitions;
import geologger.saints.com.geologger.models.TrajectoryStatisticalEntry;

/**
 * Created by Mizuno on 2015/04/06.
 */
@EBean
public class TrajectoryStatisticalInformationSQLite {

    private final String TABLENAME = TableDefinitions.TRAJECTORY_STATISTICAL_INFORMATION;

    @Bean
    BaseSQLiteOpenHelper mDbHelper;

    public TrajectoryStatisticalInformationSQLite() {}

    //region insert

    /**
     * insert an entry that has passed params
     * @param tid
     * @param duration
     * @param distance
     * @param speed
     * @param timestamp
     * @return
     */
    public boolean insert(String tid, float duration, float distance, float speed, String timestamp) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        boolean result = false;
        try {

            ContentValues insertValues = new ContentValues();
            insertValues.put(TrajectoryStatisticalEntry.TID, tid);
            insertValues.put(TrajectoryStatisticalEntry.DURATION, duration);
            insertValues.put(TrajectoryStatisticalEntry.DISTANCE, distance);
            insertValues.put(TrajectoryStatisticalEntry.SPEED, speed);
            if (timestamp != null) {
                insertValues.put(TrajectoryStatisticalEntry.TIMESTAMP, timestamp);
            }

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
     * @param entry
     * @return
     */
    public boolean insert(TrajectoryStatisticalEntry entry) {
        String tid = entry.getTid();
        float duration = entry.getDuration();
        float distance = entry.getDistance();
        float speed = entry.getSpeed();
        String timestamp = entry.getTimestamp();
        return this.insert(tid, duration, distance, speed, timestamp);
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
        int removedCount = -1;
        try {
            db.delete(TABLENAME, TrajectoryStatisticalEntry.TID + "=?", new String[]{tid});
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
     * Get Total Duration of the trajectory that has passed tid
     * @param tid
     * @return
     */
    public long getTotalDuration(String tid) {

        long totalDuration = -1L;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {

            Cursor cursor = db.query(TABLENAME, new String[]{"SUM(" + TrajectoryStatisticalEntry.DURATION + ")"}, TrajectoryStatisticalEntry.TID + "=?", new String[]{tid}, null, null, null);
            try {
                if (cursor.moveToFirst()) {
                    totalDuration = cursor.getLong(0);
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

        return totalDuration;
    }

    /**
     * Get Total Distance of the trajectory that has passed tid
     * @param tid
     * @return
     */
    public float getTotalDistance(String tid) {

        float totalDistance = -1.0f;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {

            Cursor cursor = db.query(TABLENAME, new String[]{"SUM(" + TrajectoryStatisticalEntry.DISTANCE + ")"}, TrajectoryStatisticalEntry.TID + "=?", new String[]{tid}, null, null, null);
            try {
                if (cursor.moveToFirst()) {
                    totalDistance = cursor.getFloat(0);
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

        return totalDistance;
    }

    /**
     * Get Average speed of the trajectory that has passed tid
     * @param tid
     * @return
     */
    public float getAverageSpeed(String tid) {

        float avgSpeed = -1.0f;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {

            Cursor cursor = db.query(TABLENAME, new String[]{"AVG(" + TrajectoryStatisticalEntry.SPEED + ")"}, TrajectoryStatisticalEntry.TID + "=? AND " + TrajectoryStatisticalEntry.SPEED + " != 0" , new String[]{tid}, null, null, null);
            try {
                if (cursor.moveToFirst()) {
                    avgSpeed = cursor.getFloat(0);
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

        return avgSpeed;
    }

    /**
     * Get max speed of the trajectory that has passed tid
     * @param tid
     * @return
     */
    public float getMaxSpeed(String tid) {

        float maxSpeed = -1.0f;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {

            final String maxSpeedQuery = "MAX(" + TrajectoryStatisticalEntry.SPEED + ")";
            Cursor cursor = db.query(TABLENAME, new String[]{maxSpeedQuery}, TrajectoryStatisticalEntry.TID + "=?", new String[]{tid}, null, null, null);
            try {
                if (cursor.moveToFirst()) {
                    maxSpeed = cursor.getFloat(cursor.getColumnIndex(maxSpeedQuery));
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

        return maxSpeed;
    }

    /**
     * Get all speed entries that has passed tid
     * @param tid
     * @return
     */
    public List<Float> getSpeedList(String tid) {

        List<Float> speedList = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {

            Cursor cursor = db.query(TABLENAME, new String[]{TrajectoryStatisticalEntry.SPEED}, TrajectoryStatisticalEntry.TID + "=?", new String[]{tid}, null, null, TrajectoryStatisticalEntry.TIMESTAMP + " ASC");
            try {
                boolean isEOF = cursor.moveToFirst();
                while(isEOF) {
                    float speed = cursor.getFloat(cursor.getColumnIndex(TrajectoryStatisticalEntry.SPEED));
                    speedList.add(speed);
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

        return speedList;
    }

    /**
     * Get all entries that have passed tid as a list
     * @param tid
     * @return
     */
    public List<TrajectoryStatisticalEntry> getEntryList(String tid) {

        List<TrajectoryStatisticalEntry> ret = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {

            Cursor cursor = db.query(TABLENAME, null, TrajectoryStatisticalEntry.TID + "=?", new String[]{tid}, null, null, TrajectoryStatisticalEntry.TIMESTAMP + " ASC");
            try {
                boolean isEOF = cursor.moveToFirst();
                while (isEOF) {
                    TrajectoryStatisticalEntry entry = getEntryFromCursor(cursor);
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
    private TrajectoryStatisticalEntry getEntryFromCursor(Cursor cursor) {

        if (cursor.isNull(cursor.getColumnIndex(TrajectoryStatisticalEntry.TID))) {
            return null;
        }

        String tid = cursor.getString(cursor.getColumnIndex(TrajectoryStatisticalEntry.TID));
        long duration = cursor.getLong(cursor.getColumnIndex(TrajectoryStatisticalEntry.DURATION));
        float distance = cursor.getFloat(cursor.getColumnIndex(TrajectoryStatisticalEntry.DISTANCE));
        float speed = cursor.getFloat(cursor.getColumnIndex(TrajectoryStatisticalEntry.SPEED));
        String timestamp = cursor.getString(cursor.getColumnIndex(TrajectoryStatisticalEntry.TIMESTAMP));
        TrajectoryStatisticalEntry entry = new TrajectoryStatisticalEntry(tid, duration, distance, speed, timestamp);

        return entry;
    }

    //endregion

}
