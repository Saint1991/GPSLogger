package geologger.saints.com.geologger.models;

/**
 * Created by Mizuno on 2015/04/06.
 */
public class TrajectoryStatisticalEntry {

    public static final String TID = "t_id";
    public static final String DURATION = "duration";
    public static final String DISTANCE = "distance";
    public static final String SPEED = "speed";
    public static final String TIMESTAMP = "timestamp";

    private String tid;
    private float duration;
    private float distance;
    private float speed;
    private String timestamp;

    public TrajectoryStatisticalEntry(String tid, float duration, float distance, float speed) {
        this.setTid(tid);
        this.setDuration(duration);
        this.setDistance(distance);
        this.setSpeed(speed);
    }

    public TrajectoryStatisticalEntry(String tid, float duration, float distance, float speed, String timestamp) {
        this(tid, duration, distance, speed);
        this.setTimestamp(timestamp);
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
