package geologger.saints.com.geologger.models;

/**
 * Created by Mizuno on 2015/01/30.
 */
public class CheckinFreeFormEntry {

    public static final String TID = "t_id";
    public static final String PLACENAME = "place_name";
    public static final String TIMESTAMP = "timestamp";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    protected String tid = null;
    protected String placeName = null;
    protected String timestamp = null;
    protected float latitude;
    protected float longitude;

    public CheckinFreeFormEntry(){}
    public CheckinFreeFormEntry(String tid, String placeName, String timestamp, float latitude, float longitude) {
        this.setTid(tid);
        this.setPlaceName(placeName);
        this.setTimestamp(timestamp);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
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

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
