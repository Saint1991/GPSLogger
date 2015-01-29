package geologger.saints.com.geologger.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import geologger.saints.com.geologger.models.TableDefinitions;
import geologger.saints.com.geologger.models.TrajectorySpanEntry;
import geologger.saints.com.geologger.utils.TimestampGenerator;

/**
 * Created by Mizuno on 2015/01/29.
 */
@EBean
public class TrajectorySpanSQLite {

    private final String TABLENAME = TableDefinitions.TRAJECTORY_SPAN;

    @Bean
    BaseSQLiteOpenHelper mDbHelper;

    public TrajectorySpanSQLite() {}

    /**
     * 指定したtid, 開始時刻beginを持つエントリを作成する
     * @param tid
     * @param begin
     * @return
     */
    public boolean insert(String tid, String begin) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues insertValues = new ContentValues();
        insertValues.put(TrajectorySpanEntry.TID, tid);
        insertValues.put(TrajectorySpanEntry.BEGIN, begin);

        boolean result = db.insert(TABLENAME, null, insertValues) != -1;
        db.close();

        return result;
    }

    /**
     * 指定したtidのエントリを作成
     * 開始時刻は現在時で補完される
     * @param tid
     * @return
     */
    public boolean insert(String tid) {
        String begin = TimestampGenerator.getTimestamp();
        return this.insert(tid, begin);
    }

    /**
     * 指定したtidのエントリに終了時刻をセットする．
     * @param tid
     * @param end
     * @return 成功時true, 失敗時false
     */
    public boolean setEnd(String tid, String end) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues updateValues = new ContentValues();
        updateValues.put(TrajectorySpanEntry.END, end);

        boolean result = db.update(TABLENAME, updateValues, TrajectorySpanEntry.TID + "=?", new String[]{tid}) == 1;
        db.close();

        return result;
    }


    /**
     * 指定したエントリにendが設定されているかを確認することで，
     * ロギングが終了したトラジェクトリかをチェックする．
     * @param tid
     * @return true: 終了したトラジェクトリ, false: 未終了もしくは存在しないトラジェクトリ
     */
    public boolean isEnd(String tid) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, null, TrajectorySpanEntry.TID + "=?", new String[]{tid}, null, null, null, null);

        if (!cursor.moveToFirst()) {
            return false;
        }

        String end = cursor.getString(cursor.getColumnIndex(TrajectorySpanEntry.END));
        if (end == null) {
            return false;
        }

        cursor.close();
        db.close();

        end = end.toLowerCase();
        return end != "null";
    }

    /**
     * トラジェクトリスパンの一覧をListで取得する
     * 結果は開始時刻beginでソートされている
     * @return
     */
    public List<TrajectorySpanEntry> getSpanList() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, null, null, null, null, null, TrajectorySpanEntry.BEGIN + " ASC");

        List<TrajectorySpanEntry> ret = new ArrayList<TrajectorySpanEntry>();
        boolean isEOF = cursor.moveToFirst();
        while (isEOF) {
            TrajectorySpanEntry entry = getEntryFromCursor(cursor);
            ret.add(entry);
            isEOF = cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return ret;
    }


    /**
     * トラジェクトリIDの一覧をListで返す
     * @return
     */
    public List<String> getTidList() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, new String[]{TrajectorySpanEntry.TID}, null, null, null, null, TrajectorySpanEntry.BEGIN + " ASC");

        List<String> ret = new ArrayList<String>();
        boolean isEOF = cursor.moveToFirst();
        while (isEOF) {
            String tid = cursor.getString(cursor.getColumnIndex(TrajectorySpanEntry.TID));
            ret.add(tid);
            isEOF = cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return ret;
    }

    //カーソルの現在位置からエントリを取得する
    //取得できない場合はnullを返す
    private TrajectorySpanEntry getEntryFromCursor(Cursor cursor) {

        if(cursor.isNull(cursor.getColumnIndex(TrajectorySpanEntry.TID))) {
            return null;
        }

        String tid = cursor.getString(cursor.getColumnIndex(TrajectorySpanEntry.TID));
        String begin = cursor.getString(cursor.getColumnIndex(TrajectorySpanEntry.BEGIN));
        String end = cursor.getString(cursor.getColumnIndex(TrajectorySpanEntry.END));
        TrajectorySpanEntry entry = new TrajectorySpanEntry(tid, begin, end);

        return entry;
    }
}
