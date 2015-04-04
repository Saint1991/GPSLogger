package geologger.saints.com.geologger.models;

import java.io.Serializable;

/**
 * Created by Mizuno on 2015/04/03.
 */
public class TrajectoryPropertyEntry implements Serializable {

    public static final String TID = "t_id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";

    private String tid = null;
    private String title = null;
    private String description = null;

    public TrajectoryPropertyEntry(String tid, String title, String description) {
        this(tid, title);
        this.setDescription(description);
    }

    public TrajectoryPropertyEntry(String tid, String title) {
        this.setTid(tid);
        this.setTitle(title);
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
