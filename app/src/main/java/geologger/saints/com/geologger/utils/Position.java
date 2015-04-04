package geologger.saints.com.geologger.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Seiya on 2015/01/01.
 */
public class Position {

    private static final String PREFNAME = "Position";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String TIMESTAMP ="timestamp";

    public static void savePosition(Context context, float latitude, float longitude) {
        Log.i(PREFNAME, "savePosition");
        SharedPreferences preference = context.getSharedPreferences(PREFNAME, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = preference.edit();
        editor.putFloat(LATITUDE, latitude);
        editor.putFloat(LONGITUDE, longitude);
        editor.putString(TIMESTAMP, TimestampGenerator.getTimestamp());
        editor.commit();
    }

    public static float[] getPosition(Context context) {
        Log.i(PREFNAME, "getPosition");
        SharedPreferences preference = context.getSharedPreferences(PREFNAME, Context.MODE_MULTI_PROCESS);
        float[] position = new float[2];
        position[0] = preference.getFloat(LATITUDE, 0.0f);
        position[1] = preference.getFloat(LONGITUDE, 0.0f);
        return position;
    }

    public static String getLastUpdateTimestamp(Context context) {
        Log.i(PREFNAME, "getPosition");
        SharedPreferences preference = context.getSharedPreferences(PREFNAME, Context.MODE_MULTI_PROCESS);
        String timestamp = preference.getString(TIMESTAMP, null);
        return timestamp;
    }

    /**
     * Return passed time from last update of the position (msec)
     * @return
     */
    public static long getPassedTimeFromLastUpdate(Context context) {

        long ret = -1L;

        try {

            SharedPreferences preference = context.getSharedPreferences(PREFNAME, Context.MODE_MULTI_PROCESS);
            Date lastUpdate = DateFormat.getDateInstance().parse(preference.getString(TIMESTAMP, null));

            long now = System.currentTimeMillis();
            ret = now - lastUpdate.getTime();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return ret;
    }

}
