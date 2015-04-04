package geologger.saints.com.geologger.models;

import java.io.Serializable;

/**
 * Created by Mizuno on 2015/01/29.
 * CompanionテーブルのBean
 */
public class CompanionEntry implements Serializable {

    private String tid = null;
    private String companion = null;
    private String timestamp = null;

    public static final String TID = "t_id";
    public static final String COMPANION = "companion";
    public static final String TIMESTAMP = "timestamp";

    public CompanionEntry(){}
    public CompanionEntry(String tid, String companion) {
        this.setTid(tid);
        this.setCompanion(companion);
    }
    public CompanionEntry(String tid, String companion, String timestamp) {
        this(tid, companion);
        this.setTimestamp(timestamp);
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getCompanion() {
        return companion;
    }

    public void setCompanion(String companion) {
        this.companion = companion;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
