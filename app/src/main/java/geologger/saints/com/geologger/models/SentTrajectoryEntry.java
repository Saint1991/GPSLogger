package geologger.saints.com.geologger.models;

import java.io.Serializable;

/**
 * Created by Mizuno on 2015/01/29.
 */
public class SentTrajectoryEntry implements Serializable {

    public static final String TID = "t_id";
    public static final String ISSENT = "is_sent";

    private String tid = null;
    private boolean isSent = false;

    public SentTrajectoryEntry(){}
    public SentTrajectoryEntry(String tid, boolean isSent) {
        this.setTid(tid);
        this.setIsSent(isSent);
    }
    public SentTrajectoryEntry(String tid, int isSent) {
        this.setTid(tid);
        this.setIsSent(isSent == 1);
    }


    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public boolean getIsSent() {
        return isSent;
    }

    public void setIsSent(boolean isSent) {
        this.isSent = isSent;
    }
}
