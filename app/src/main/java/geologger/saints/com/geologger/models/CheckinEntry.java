package geologger.saints.com.geologger.models;

/**
 * Created by Mizuno on 2015/01/29.
 */
public class CheckinEntry {

    public static final String TID = "t_id";
    public static final String PLACEID = "place_id";
    public static final String CATEGORYID = "category_id";
    public static final String TIMESTAMP = "timestamp";

    private String tid = null;
    private String placeId = null;
    private String categoryId = null;
    private String timestamp = null;

    public CheckinEntry(){}
    public CheckinEntry(String tid, String placeId, String categoryId, String timestamp) {
        this.setTid(tid);
        this.setPlaceId(placeId);
        this.setCategoryId(categoryId);
        this.setTimestamp(timestamp);
    }


    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
