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

    /**
     * チェックイン情報のエントリを格納する
     * @param tid
     * @param placeId
     * @param categoryId
     * @param timestamp
     * @return 成功時true，失敗時false
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
     * 指定したエントリを格納する
     * @param entry
     * @return 成功時true，失敗時false
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

    /**
     * 指定したエントリを格納する
     * タイムスタンプは自動で補完する
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
     * 指定したtidに対応するエントリを削除する
     * @param tid
     * @return 成功時true, 失敗時false
     */
    public int removeByTid(String tid) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int removedCount = db.delete(TABLENAME, CheckinEntry.TID + "=?", new String[]{tid});
        db.close();

        return removedCount;
    }

    /**
     * 指定したtimestampに対応するエントリを削除する
     * @param timestamp
     * @return
     */
    public int removeByTimestamp(String timestamp) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int removeCount = db.delete(TABLENAME, CheckinEntry.TIMESTAMP + "=?", new String[]{timestamp});
        db.close();

        return removeCount;
    }

    /**
     * 指定したtidに対応するCheckinのListを返す．
     * Listはtimestampでソートされている
     * @param tid
     * @return
     */
    public List<CheckinEntry> getCheckinList(String tid) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, null, CheckinEntry.TID + "=?", new String[]{tid}, null, null, CheckinEntry.TIMESTAMP + " ASC");

        List<CheckinEntry> ret = new ArrayList<CheckinEntry>();
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

    //カーソルの現在位置からエントリを取得する
    //取得できない場合はnullを返す
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
}
