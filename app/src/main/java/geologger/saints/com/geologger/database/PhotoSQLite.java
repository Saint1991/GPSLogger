package geologger.saints.com.geologger.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import geologger.saints.com.geologger.models.PhotoEntry;
import geologger.saints.com.geologger.models.TableDefinitions;

/**
 * Created by Mizuno on 2015/04/11.
 */
@EBean
public class PhotoSQLite {

    private final String TABLENAME = TableDefinitions.PHOTOS;

    @Bean
    BaseSQLiteOpenHelper mDbHelper;

    public PhotoSQLite() {}


    //region insert

    /**
     * Insert an entry with the passed params
     * @param tid
     * @param latitude
     * @param longitude
     * @param timestamp
     * @param filePath
     * @param memo
     * @return
     */
    public boolean insert(String tid, float latitude, float longitude, String timestamp, String filePath, String memo) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        boolean result = false;

        try {
            ContentValues insertValues = new ContentValues();
            insertValues.put(PhotoEntry.TID, tid);
            insertValues.put(PhotoEntry.LATITUDE, latitude);
            insertValues.put(PhotoEntry.LONGITUDE, longitude);
            insertValues.put(PhotoEntry.FILEPATH, filePath);
            insertValues.put(PhotoEntry.MEMO, memo);
            if (timestamp != null) {
                insertValues.put(PhotoEntry.TIMESTAMP, timestamp);
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
     * insert an entry
     * @param entry
     * @return
     */
    public boolean insert(PhotoEntry entry) {
        String tid = entry.getTid();
        float latitude = entry.getLatitude();
        float longitude = entry.getLongitude();
        String timestamp = entry.getTimestamp();
        String filePath = entry.getFilePath();
        String memo = entry.getMemo();
        return this.insert(tid, latitude, longitude, timestamp, filePath, memo);
    }

    //endregion

    //region remove

    public int removeByTid(String tid) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int removedCount = -1;

        try {
            removedCount = db.delete(TABLENAME, PhotoEntry.TID + "=?", new String[]{tid});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return removedCount;
    }

    //endregion

    //region find

    public List<PhotoEntry> getEntryListByTid(String tid) {

        List<PhotoEntry> ret = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {

            Cursor cursor = db.query(TABLENAME, null, PhotoEntry.TID + "=?", new String[]{tid}, null, null, null, null);

            try {
                boolean isEOF = cursor.moveToFirst();
                while(isEOF) {
                    PhotoEntry entry = getEntryFromCursor(cursor);
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
    private PhotoEntry getEntryFromCursor(Cursor cursor) {

        if (cursor.getCount() == 0 || cursor.isNull(cursor.getColumnIndex(PhotoEntry.TID))) {
            return null;
        }

        String tid = cursor.getString(cursor.getColumnIndex(PhotoEntry.TID));
        float latitude = cursor.getFloat(cursor.getColumnIndex(PhotoEntry.LATITUDE));
        float longitude = cursor.getFloat(cursor.getColumnIndex(PhotoEntry.LONGITUDE));
        String timestamp = cursor.getString(cursor.getColumnIndex(PhotoEntry.TIMESTAMP));
        String filePath = cursor.getString(cursor.getColumnIndex(PhotoEntry.FILEPATH));
        String memo = cursor.getString(cursor.getColumnIndex(PhotoEntry.MEMO));
        PhotoEntry entry = new PhotoEntry(tid, latitude, longitude, timestamp, filePath, memo);

        return entry;
    }

    //endregion

}
