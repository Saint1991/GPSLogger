package geologger.saints.com.geologger.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import geologger.saints.com.geologger.models.CompanionEntry;
import geologger.saints.com.geologger.models.TableDefinitions;
import geologger.saints.com.geologger.utils.TimestampGenerator;

/**
 * Created by Mizuno on 2015/01/29.
 */
@EBean
public class CompanionSQLite {

    private final String TABLENAME = TableDefinitions.COMPANION;

    @Bean
    BaseSQLiteOpenHelper mDbHelper;

    public CompanionSQLite() {}

    /**
     * 同伴者情報のエントリを格納する
     * @param tid
     * @param companion
     * @return 成功時true，失敗時false
     */
    public boolean insert(String tid, String companion, String timestamp) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues insertValues = new ContentValues();
        insertValues.put(CompanionEntry.TID, tid);
        insertValues.put(CompanionEntry.COMPANION, companion);
        insertValues.put(CompanionEntry.TIMESTAMP, timestamp);

        boolean result = db.insert(TABLENAME, null, insertValues) != -1;
        db.close();

        return result;
    }

    /**
     * 指定したエントリを格納する
     * @param entry
     * @return 成功時true，失敗時false
     */
    public boolean insert(CompanionEntry entry) {
        String tid = entry.getTid();
        String companion = entry.getCompanion();
        String timestamp = entry.getTimestamp();

        return this.insert(tid, companion, timestamp);
    }

    /**
     * 指定したエントリを格納する．
     * タイムスタンプは自動で補完する
     * @param tid
     * @param companion
     * @return 成功時true，失敗時false
     */
    public boolean insert(String tid, String companion) {
        String timestamp = TimestampGenerator.getTimestamp();
        return this.insert(tid, companion, timestamp);
    }

    /**
     * 指定したtidに対応するcompanionのListを返す
     * Listはtimestampでソートされている
     * @param tid
     * @return
     */
    public List<CompanionEntry> getCompanionList(String tid) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor= db.query(TABLENAME, null, CompanionEntry.TID + "=?", new String[]{tid}, null, null, CompanionEntry.TIMESTAMP + " ASC");

        List<CompanionEntry> ret = new ArrayList<CompanionEntry>();
        boolean isEOF = cursor.moveToFirst();
        while (isEOF) {
            CompanionEntry entry = getEntryFromCursor(cursor);
            ret.add(entry);
            isEOF = cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return ret;
    }

    //カーソルの現在位置からエントリを取得する
    //取得できない場合はnullを返す
    private CompanionEntry getEntryFromCursor(Cursor cursor) {

        if (cursor.isNull(cursor.getColumnIndex(CompanionEntry.TID))) {
            return null;
        }

        String tid = cursor.getString(cursor.getColumnIndex(CompanionEntry.TID));
        String companion = cursor.getString(cursor.getColumnIndex(CompanionEntry.COMPANION));
        CompanionEntry entry = new CompanionEntry(tid, companion);

        return entry;
    }
}