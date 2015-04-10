package geologger.saints.com.geologger.database;

/**
 * Created by Mizuno on 2015/04/08.
 */
public interface IRemoveBy {

    public int removeByTid(String tid);
    public int removeByTimestamp(String timestamp);
}
