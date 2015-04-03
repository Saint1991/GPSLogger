package geologger.saints.com.geologger.models;

/**
 * Created by Mizuno on 2015/04/03.
 */
public class LogListEntry {

    private String tid = null;
    private String title = null;
    private String beginTimestamp = null;
    private String endTimestamp = null;
    private String companion = null;
    private String description = null;

    public LogListEntry(String tid, String title, String beginTimestamp, String endTimestamp, String companion, String description) {
        this.setTid(tid);
        this.setTitle(title);
        this.setBeginTimestamp(beginTimestamp);
        this.setEndTimestamp(endTimestamp);
        this.setCompanion(companion);
        this.setDescription(description);
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

    public String getBeginTimestamp() {
        return beginTimestamp;
    }

    public void setBeginTimestamp(String beginTimestamp) {
        this.beginTimestamp = beginTimestamp;
    }

    public String getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(String endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public String getCompanion() {
        return companion;
    }

    public void setCompanion(String companion) {
        this.companion = companion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
