package geologger.saints.com.geologger.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import geologger.saints.com.geologger.models.SQLiteModelDefinition;
import geologger.saints.com.geologger.models.TableDefinitions;
import geologger.saints.com.geologger.models.TrajectoryEntry;

/**
 * Created by Seiya on 2015/01/01.
 */
@EBean
public class SendDataQueueSQLiteHandler {

    public static final String TABLENAME = "SendDataQueue";
    public static final String TID = "t_id";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String TIMESTAMP = "timestamp";

    private BaseSQLiteOpenHelper dbHelper = null;

    public SendDataQueueSQLiteHandler(Context context) {
        SQLiteModelDefinition model = new SQLiteModelDefinition(TABLENAME, TableDefinitions.getColumnDefinition(TABLENAME));
        this.dbHelper = new BaseSQLiteOpenHelper(context, model);
    }

    public void clearTable() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "DROP TABLE IF EXISTS " + TABLENAME;
        db.execSQL(query);
        dbHelper.onCreate(db);
    }

    public Cursor getAllDataCursor() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.query(TABLENAME, null, null, null, null, null, "timestamp ASC", null);
        return result;
    }

    public List<TrajectoryEntry> readAll() {

        ArrayList<TrajectoryEntry> retData = null;

        Cursor cursor = getAllDataCursor();
        if (cursor.moveToFirst()) {
            retData = new ArrayList<TrajectoryEntry>();
            do {
                String tid = cursor.getString(cursor.getColumnIndex(TID));
                float latitude = cursor.getFloat(cursor.getColumnIndex(LATITUDE));
                float longitude = cursor.getFloat(cursor.getColumnIndex(LONGITUDE));
                String timestamp = cursor.getString(cursor.getColumnIndex(TIMESTAMP));

                TrajectoryEntry entry = new TrajectoryEntry(tid, latitude, longitude, timestamp);
                retData.add(entry);
            } while(cursor.moveToNext());
        }

        return retData;
    }

    public boolean insert(TrajectoryEntry entry) {

        if (!entry.isValid()) {
            return false;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cvs = new ContentValues();
        cvs.put(TID, entry.getTid());
        cvs.put(LATITUDE, entry.getLatitude());
        cvs.put(LONGITUDE, entry.getLongitude());
        if (entry.getTimestamp() != null) {
            cvs.put(TIMESTAMP, entry.getTimestamp());
        }
        long result = db.insert(TABLENAME, null, cvs);

        return result != -1;
    }
}
