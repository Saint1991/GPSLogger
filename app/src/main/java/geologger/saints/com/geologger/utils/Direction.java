package geologger.saints.com.geologger.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Mizuno on 2015/02/13.
 */
public class Direction {

    private static final String PREFNAME = "Direction";
    public static final String DIRECTION = "Direction";

    public static void saveDirection(Context context, float direction) {
        Log.i(PREFNAME, "saveDirection");
        SharedPreferences preference = context.getSharedPreferences(PREFNAME, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = preference.edit();
        editor.putFloat(DIRECTION, direction);
        editor.commit();
    }

    public static float getDirection(Context context) {
        Log.i(PREFNAME, "getDirection");
        SharedPreferences preference = context.getSharedPreferences(PREFNAME, Context.MODE_MULTI_PROCESS);
        float direction = preference.getFloat(DIRECTION, 0);
        return direction;
    }
}
