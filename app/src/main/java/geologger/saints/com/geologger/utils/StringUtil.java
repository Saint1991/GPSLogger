package geologger.saints.com.geologger.utils;

import android.util.SparseBooleanArray;

import java.util.List;

/**
 * Created by Mizuno on 2015/04/04.
 */
public class StringUtil {

    /**
     * Combine Strings in the list with the passed delimiter
     * @param list
     * @param delimiter
     * @return
     */
    public static String combineWithDelimiter(Iterable<String> list, String delimiter) {

        StringBuilder builder = new StringBuilder();
        for (String entry : list) {
            builder.append(entry + delimiter);
        }

        if (builder.length() < 1) {
            return "";
        }

        return builder.substring(0, builder.length() - delimiter.length());
    }

    /**
     * Combine strings in the list whose value of useArray is true with passed delimiter
     * @param list
     * @param delimiter
     * @param useArray
     * @return
     */
    public static String combineWithDelimiter(List<String> list, String delimiter, SparseBooleanArray useArray) {

        int useCount = useArray.size();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i < useCount && useArray.get(i)) {
                builder.append(list.get(i) + delimiter);
            }
        }

        if (builder.length() < 1) {
            return "";
        }

        return builder.substring(0, builder.length() - delimiter.length());
    }
}
