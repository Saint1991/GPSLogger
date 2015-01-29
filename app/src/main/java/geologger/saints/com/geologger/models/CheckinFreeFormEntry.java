package geologger.saints.com.geologger.models;

/**
 * Created by Mizuno on 2015/01/30.
 */
public class CheckinFreeFormEntry {

    public static final String TID = "t_id";
    public static final String PLACENAME = "place_name";
    public static final String TIMESTAMP = "timestamp";

    private String tid = null;
    private String placeName = null;
    private String timestamp = null;

    public CheckinFreeFormEntry(){}
    public CheckinFreeFormEntry(String tid, String placeName, String timestamp) {
        this.setTid(tid);
        this.setPlaceName(placeName);
        this.setTimestamp(timestamp);
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
