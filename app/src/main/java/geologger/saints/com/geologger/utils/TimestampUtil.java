package geologger.saints.com.geologger.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mizuno on 2015/01/28.
 * SQLite用のtimestamp文字列を生成する
 */
public class TimestampUtil {

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    private TimestampUtil() {}


    public static String getTimestamp(Timestamp timestamp) {
        String timestampStr = timestamp.toString();
        return timestampStr.substring(0, timestampStr.indexOf("."));
    }

    /**
     * 現在時刻に応じたtimestamp文字列を取得します
     * @return 現在時刻に対応するtimestamp文字列
     */
    public static String getTimestamp() {
        long time = System.currentTimeMillis();
        return getTimestamp(new Timestamp(time));
    }


    /**
     *
     * @param time Unix Timestamp
     * @return　引数に与えた時刻に対応するtimestamp文字列
     */
    public static String getTimestamp(long time) {
        return getTimestamp(new Timestamp(time));
    }

    public static Date parseTimestamp(String timestamp) {

        DateFormat format = new SimpleDateFormat(PATTERN);
        if (timestamp == null) {
            return null;
        }

        Date ret = null;
        try {
            ret = format.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * calculate seconds from "from" to "to"
     * if either timestamp is invalid return -1
     * @param from
     * @param to
     * @return
     */
    public static float calcPassedSec(String from, String to) {

        Date fromDate = parseTimestamp(from);
        Date toDate = parseTimestamp(to);

        if (fromDate == null || toDate == null) {
            return -1L;
        }

        float passedSec = (toDate.getTime() - fromDate.getTime()) / 1000.0F;
        return passedSec;
    }
}
