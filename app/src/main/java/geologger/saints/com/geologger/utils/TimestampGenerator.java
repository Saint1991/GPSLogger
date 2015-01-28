package geologger.saints.com.geologger.utils;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Mizuno on 2015/01/28.
 * SQLite用のtimestamp文字列を生成する
 */
public class TimestampGenerator {

    private TimestampGenerator() {}

    /**
     * 現在時刻に応じたtimestamp文字列を取得します
     * @return 現在時刻に対応するtimestamp文字列
     */
    public static String getTimestamp() {
        Date now = new Date();
        long time = now.getTime();
        Timestamp timestamp = new Timestamp(time);
        String timestampStr = timestamp.toString();
        return timestampStr.substring(0, timestampStr.indexOf("."));
    }

    /**
     *
     * @param time Unix Timestamp
     * @return　引数に与えた時刻に対応するtimestamp文字列
     */
    public static String getTimestamp(long time) {
        Timestamp timestamp = new Timestamp(time);
        String timestampStr = timestamp.toString();
        return timestampStr.substring(0, timestampStr.indexOf("."));
    }
}
