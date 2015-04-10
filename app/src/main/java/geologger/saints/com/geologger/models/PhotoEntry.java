package geologger.saints.com.geologger.models;

/**
 * Created by Mizuno on 2015/04/10.
 */
public class PhotoEntry {

    public static final String TID = "t_id";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String TIMESTAMP = "timestamp";
    public static final String FILEPATH = "file_path";
    public static final String MEMO = "memo";

    private String tid;
    private float latitude;
    private float longitude;
    private String timestamp;
    private String filePath;
    private String memo;

    public PhotoEntry(String tid, float latitude, float longitude, String timestamp, String filePath, String memo) {
        this.setTid(tid);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setTimestamp(timestamp);
        this.setFilePath(filePath);
        this.setMemo(memo);
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
