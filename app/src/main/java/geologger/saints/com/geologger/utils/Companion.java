package geologger.saints.com.geologger.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


/**
 * Created by Mizuno on 2015/01/30.
 */
public class Companion {

    private static final String PREFNAME ="Companion";

    public static void saveCompanions(Context context, String[] companions) {

        StringBuilder save = new StringBuilder();
        for (String companion : companions) {
            save.append(companion + ",");
        }
        saveCompanion(context, save.substring(0, save.length() - 1));
    }

    public static void saveCompanion(Context context, String companion) {
        Log.i("Companion", "saveCompanion");
        SharedPreferences preference = context.getSharedPreferences(PREFNAME, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString(PREFNAME, companion);
        editor.commit();
    }

    public static String getCompanion(Context context) {
        Log.i("Companion", "getCompanion");
        SharedPreferences preference = context.getSharedPreferences(PREFNAME, Context.MODE_MULTI_PROCESS);
        String companion = preference.getString(PREFNAME, null);
        return companion;
    }
}
