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
public class CheckinSQLite {

    private final String TABLENAME = TableDefinitions.CHECKIN;

    @Bean
    BaseSQLiteOpenHelper mDbHelper;

    public CheckinSQLite() {}

    /**
     * チェックイン情報のエントリを格納する
     * @param tid
     * @param placeId
     * @param categoryId
     * @param timestamp
     * @return 成功時true，失敗時false
     */
    public boolean insert(String tid, String placeId, String categoryId, String timestamp) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues insertValues = new ContentValues();
        insertValues.put(CheckinEntry.TID, tid);
        insertValues.put(CheckinEntry.PLACEID, placeId);
        insertValues.put(CheckinEntry.CATEGORYID, categoryId);
        insertValues.put(CheckinEntry.TIMESTAMP, timestamp);

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

        return this.insert(tid, placeId, categoryId, timestamp);
    }

    /**
     * 指定したエントリを格納する
     * タイムスタンプは自動で補完する
     * @param tid
     * @param placeId
     * @param categoryId
     * @return
     */
    public boolean insert(String tid, String placeId, String categoryId) {
        String timestamp = TimestampGenerator.getTimestamp();
        return this.insert(tid, placeId, categoryId, timestamp);
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
        CheckinEntry entry = new CheckinEntry(tid, placeId, categoryId, timestamp);

        return entry;
    }
}
