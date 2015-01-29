package geologger.saints.com.geologger.database;

import android.content.ContentValues;
import android.content.Context;
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
 * Trajectoryテーブルに対するデータの操作を扱うクラス
 */
@EBean
public class TrajectorySQLite {

    private final String TABLENAME = TableDefinitions.TRAJECTORY;

    @SystemService
    LocationManager mLocationManager;

    @Bean
    BaseSQLiteOpenHelper mDbHelper;

    public TrajectorySQLite() {}

    /**
     * トラジェクトリのエントリを格納する
     * @param tid
     * @param latitude
     * @param longitude
     * @param timestamp
     * @param isGpsOn
     * @return 成功時true，失敗時false
     */
    public boolean insert(String tid, float latitude, float longitude, String timestamp, boolean isGpsOn) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues insertValues = new ContentValues();
        insertValues.put(TrajectoryEntry.TID, tid);
        insertValues.put(TrajectoryEntry.LATITUDE, latitude);
        insertValues.put(TrajectoryEntry.LONGITUDE, longitude);
        insertValues.put(TrajectoryEntry.TIMESTAMP, timestamp);
        insertValues.put(TrajectoryEntry.ISGPSON, isGpsOn);

        return db.insert(TABLENAME, null, insertValues) != -1;
    }

    /**
     * 指定したTrajectoryEntryを格納する
     * @param entry
     * @return 成功時true, 失敗時false
     */
    public boolean insert(TrajectoryEntry entry) {
        String tid = entry.getTid();
        float latitude = entry.getLatitude();
        float longitude = entry.getLongitude();
        String timestamp = entry.getTimestamp();
        boolean isGpsOn = entry.getIsGpsOn();
        return this.insert(tid, latitude, longitude, timestamp, isGpsOn);
    }

    /**
     * 指定したトラジェクトリのエントリを挿入する．
     * タイムスタンプとGpsのオンオフは自動で補完する．
     * @param tid
     * @param latitude
     * @param longitude
     * @return 成功時true, 失敗時false
     */
    public boolean insert(String tid, float latitude, float longitude) {
        String timestamp = TimestampGenerator.getTimestamp();
        boolean isGpsOn = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return this.insert(tid, latitude, longitude, timestamp, isGpsOn);
    }

    /**
     * 指定したtidに相当する最初のトラジェクトリエントリを取得する
     * @param tid
     * @return 対応する最初のトラジェクトリ, 対応するデータがない場合はnullを返す
     */
    public TrajectoryEntry getFirstEntry(String tid) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, null, TrajectoryEntry.TID + "=?", new String[]{tid}, null, null, TrajectoryEntry.TIMESTAMP + " ASC", "1");
        if (!cursor.moveToFirst()) {
            return null;
        }

        TrajectoryEntry entry = getEntryFromCursor(cursor);
        cursor.close();

        return entry;
    }

    /**
     * 指定したtidに対応するトラジェクトリの最後のエントリを返す
     * @param tid
     * @return 最後のトラジェクトリエントリ
     */
    public TrajectoryEntry getLastEntry(String tid) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLENAME, null, TrajectoryEntry.TID + "=?", new String[]{tid}, null, null, TrajectoryEntry.TIMESTAMP + " DESC", "1");
        if (!cursor.moveToFirst()) {
            return null;
        }

        TrajectoryEntry entry = getEntryFromCursor(cursor);
        cursor.close();

        return entry;
    }

    /**
     * tidに対応するトラジェクトリデータをListで取得する
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

        return ret;
    }

    //カーソルの現在位置からエントリを取得する
    //取得できない場合はnullを返す
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



}
