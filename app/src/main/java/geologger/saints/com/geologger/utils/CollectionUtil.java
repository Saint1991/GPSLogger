package geologger.saints.com.geologger.utils;

import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mizuno on 2015/04/20.
 */
public class CollectionUtil {

    /**
     * Convert SparseBooleanArray to the List which contains indexes whose values are true
     * @param sparseCheckedArray
     * @return
     */
    public static List<Integer> convertToCheckedIndexList(SparseBooleanArray sparseCheckedArray) {

        List<Integer> checkList = new ArrayList<>();
        for (int i = 0; i < sparseCheckedArray.size(); i++) {
            int index = sparseCheckedArray.keyAt(i);
            if (sparseCheckedArray.valueAt(i)) {
                checkList.add(index);
            }
        }

        return checkList;
    }
}
