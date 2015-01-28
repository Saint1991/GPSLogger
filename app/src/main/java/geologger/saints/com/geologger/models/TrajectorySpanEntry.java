package geologger.saints.com.geologger.models;

/**
 * Created by Mizuno on 2015/01/29.
 * TrajectorySpanテーブルのエントリ
 */
public class TrajectorySpanEntry {

    public static final String TID = "t_id";
    public static final String BEGIN = "begin";
    public static final String END = "end";

    private String tid;
    private String begin;
    private String end;

    public TrajectorySpanEntry(){}
    public TrajectorySpanEntry(String tid, String begin, String end) {
        this.setTid(tid);
        this.setBegin(begin);
        this.setEnd(end);
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
