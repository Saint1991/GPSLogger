package geologger.saints.com.geologger.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.HashMap;
import java.util.Map;

import geologger.saints.com.geologger.models.SentTrajectoryEntry;
import geologger.saints.com.geologger.models.TableDefinitions;

/**
 * Created by Mizuno on 2015/01/29.
 */
@EBean
public class SentTrajectorySQLite {

    private final String TABLENAME = TableDefinitions.SENTTRAJECTORY;

    @Bean
    BaseSQLiteOpenHelper mDbHelper;

    public SentTrajectorySQLite() {}

    /**
     * 指定したトラジェクトリ送信情報のエントリを格納する
     * @param tid
     * @param isSent
     * @return true: 成功時, false: 失敗時
     */
    public boolean insert(String tid, boolean isSent) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues insertValues = new ContentValues();
        insertValues.put(SentTrajectoryEntry.TID, tid);
        insertValues.put(SentTrajectoryEntry.ISSENT, isSent);

        return db.insert(TABLENAME, null, insertValues) != -1;
    }

    /**
     * 指定したエントリを格納する
     * @param entry
     * @return　true: 成功時, false: 失敗時
     */
    public boolean insert(SentTrajectoryEntry entry) {

        String tid = entry.getTid();
        boolean isSent = entry.getIsSent();

        return this.insert(tid, isSent);
    }

    /**
     * 指定したトラジェクトリIDのデータを送信したかをチェック
     * @param tid
     * @return true: 送信済み, false 未送信
     */
    public boolean isSent(String tid) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, null, SentTrajectoryEntry.TID + "=?" , new String[]{tid}, null, null, null, "1");

        boolean ret = cursor.moveToFirst();
        if (!ret) {
            return ret;
        }

        ret = !cursor.isNull(cursor.getColumnIndex(SentTrajectoryEntry.ISSENT)) && cursor.getInt(cursor.getColumnIndex(SentTrajectoryEntry.ISSENT)) == 1;
        cursor.close();

        return ret;
    }

    /**
     * tid => issentのハッシュマップを取得する
     * @return
     */
    public Map<String, Boolean> getSentTrajectoryList() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, null, null, null, null, null, null);

        Map<String, Boolean> sentTable = new HashMap<String, Boolean>();
        boolean isEOF = cursor.moveToFirst();
        while (isEOF) {
            SentTrajectoryEntry entry = getEntryFromCursor(cursor);
            sentTable.put(entry.getTid(), entry.getIsSent());
            isEOF = cursor.moveToNext();
        }
        cursor.close();

        return sentTable;
    }

    //カーソルの現在位置からエントリを取得する
    //取得できない場合はnullを返す
    private SentTrajectoryEntry getEntryFromCursor(Cursor cursor) {

        if (cursor.isNull(cursor.getColumnIndex(SentTrajectoryEntry.TID))) {
            return null;
        }

        String tid = cursor.getString(cursor.getColumnIndex(SentTrajectoryEntry.TID));
        boolean isSent = cursor.getInt(cursor.getColumnIndex(SentTrajectoryEntry.ISSENT)) == 1;
        SentTrajectoryEntry entry = new SentTrajectoryEntry(tid, isSent);

        return entry;
    }

}
