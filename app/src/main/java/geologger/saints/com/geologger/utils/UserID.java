package geologger.saints.com.geologger.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Created by Seiya on 2014/12/31.
 */
public class UserID {

    public static final String PREFNAME = "USERID";
    public static final String ISIDCREATED ="ISIDCREATED";

    public static void saveUserID(Context context) {

        SharedPreferences preference = context.getSharedPreferences(PREFNAME, Context.MODE_MULTI_PROCESS);
        boolean isIDCreated = preference.getBoolean(ISIDCREATED, false);

        if (!isIDCreated) {
            SharedPreferences.Editor editor = preference.edit();
            String userID = UUID.randomUUID().toString();
            editor.putString(PREFNAME, userID);
            editor.putBoolean(ISIDCREATED, true);
            editor.commit();
        }

    }

    public static String getUserID(Context context) {
        SharedPreferences preference = context.getSharedPreferences(PREFNAME, Context.MODE_MULTI_PROCESS);
        String userID = preference.getString(PREFNAME, null);
        return userID;
    }
}
