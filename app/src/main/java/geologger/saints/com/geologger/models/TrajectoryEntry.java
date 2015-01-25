package geologger.saints.com.geologger.models;

import android.util.Log;

/**
 * Created by Seiya on 2014/12/31.
 */
public class TrajectoryEntry {

    protected float latitude = -1;
    protected float longitude = -1;
    protected String timestamp = null;
    protected String tid = null;

    public TrajectoryEntry() {}

    public TrajectoryEntry(String tid, float latitude, float longitude) {
        this.setTid(tid);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
    }

    public TrajectoryEntry(String tid, float latitude, float longitude, String timestamp) {
        this(tid, latitude, longitude);
        this.setTimestamp(timestamp);
    }


    public boolean isValid() {
        boolean isValid =  this.latitude != -1 && this.longitude != -1;
        return isValid;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {

        if (latitude < -90 || 90 < latitude) {
            Log.e(this.getClass().getName(), "Invalid latitude");
            return;
        }
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {

        if (longitude < -180 || 180 < longitude) {
            Log.e(this.getClass().getName(), "Invalid longitude");
            return;
        }
        this.longitude = longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        if (timestamp.length() > 18) {
            this.timestamp = timestamp;
        }
    }

    public String getTid() {
        return this.tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
