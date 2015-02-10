package geologger.saints.com.geologger.database;

import android.content.ContentValues;
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
public class CheckinFreeFormSQLite implements IRemoveByTid {

    private final String TABLENAME = TableDefinitions.CHECKIN_FREE_FORM;

    @Bean
    BaseSQLiteOpenHelper mDbHelper;

    public CheckinFreeFormSQLite() {}

    /**
     * チェックインの自由入力欄のエントリを格納する
     * @param tid
     * @param placeName
     * @param timestamp
     * @return
     */
    public boolean insert(String tid, String placeName, String timestamp) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues insertValues = new ContentValues();
        insertValues.put(CheckinFreeFormEntry.TID, tid);
        insertValues.put(CheckinFreeFormEntry.PLACENAME, placeName);
        insertValues.put(CheckinFreeFormEntry.TIMESTAMP, timestamp);

        boolean result = db.insert(TABLENAME, null, insertValues) != -1;
        db.close();

        return result;
    }

    /**
     * チェックインの自由入力欄のエントリを格納する
     * @param entry
     * @return
     */
    public boolean insert(CheckinFreeFormEntry entry) {

        String tid = entry.getTid();
        String placeName = entry.getPlaceName();
        String timestamp = entry.getTimestamp();

        return this.insert(tid, placeName, timestamp);
    }

    /**
     * チェックインの自由入力欄のエントリを格納する
     * タイムスタンプは現在時刻で補完する
     * @param tid
     * @param placeName
     * @return
     */
    public boolean insert(String tid, String placeName) {
        String timestamp = TimestampGenerator.getTimestamp();
        return this.insert(tid, placeName, timestamp);
    }

    /**
     * 指定したtidに対応するエントリを削除する
     * @param tid
     * @return 成功時true, 失敗時false
     */
    public int removeByTid(String tid) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int removedCount = db.delete(TABLENAME, CheckinFreeFormEntry.TID + "=?", new String[]{tid});
        db.close();

        return removedCount;
    }

    /**
     * エントリ一覧をリストで取得する
     * @param tid
     * @return
     */
    public List<CheckinFreeFormEntry> getCheckinFreeFormList(String tid) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, null, CheckinFreeFormEntry.TID + "=?", new String[]{tid}, null, null, CheckinFreeFormEntry.TIMESTAMP + " ASC");

        List<CheckinFreeFormEntry> ret = new ArrayList<CheckinFreeFormEntry>();
        boolean isEOF = cursor.moveToFirst();
        while (isEOF) {
            CheckinFreeFormEntry entry = getEntryFromCursor(cursor);
            ret.add(entry);
            isEOF = cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return ret;

    }

    //カーソルの現在位置からエントリを取得する
    //取得できない場合はnullを返す
    private CheckinFreeFormEntry getEntryFromCursor(Cursor cursor) {

        if (cursor.isNull(cursor.getColumnIndex(CheckinFreeFormEntry.TID))) {
            return null;
        }

        String tid = cursor.getString(cursor.getColumnIndex(CheckinFreeFormEntry.TID));
        String placeName = cursor.getString(cursor.getColumnIndex(CheckinFreeFormEntry.PLACENAME));
        String timestamp = cursor.getString(cursor.getColumnIndex(CheckinFreeFormEntry.TIMESTAMP));
        CheckinFreeFormEntry entry = new CheckinFreeFormEntry(tid, placeName, timestamp);

        return entry;
    }
}
