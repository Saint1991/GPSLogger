package geologger.saints.com.geologger.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Created by Seiya on 2014/12/31.
 * プリファレンスへのユーザIDを生成，読出しを実行するクラス
 */
public class UserId {

    public static final String PREFNAME = "USERID";
    public static final String ISIDCREATED ="ISIDCREATED";

    public static void saveUserId(Context context) {

        SharedPreferences preference = context.getSharedPreferences(PREFNAME, Context.MODE_MULTI_PROCESS);
        boolean isIDCreated = preference.getBoolean(ISIDCREATED, false);

        if (!isIDCreated) {
            SharedPreferences.Editor editor = preference.edit();
            String userId = UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
            editor.putString(PREFNAME, userId);
            editor.putBoolean(ISIDCREATED, true);
            editor.commit();
        }

    }

    public static String getUserId(Context context) {
        SharedPreferences preference = context.getSharedPreferences(PREFNAME, Context.MODE_MULTI_PROCESS);
        String userID = preference.getString(PREFNAME, null);
        return userID;
    }

    public static boolean isUserIdExist(Context context) {
        SharedPreferences preference = context.getSharedPreferences(PREFNAME, Context.MODE_MULTI_PROCESS);
        boolean isIdExist = preference.getBoolean(ISIDCREATED, false);
        return isIdExist;
    }
}
