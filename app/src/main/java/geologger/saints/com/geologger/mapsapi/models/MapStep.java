package geologger.saints.com.geologger.mapsapi.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Mizuno on 2015/04/01.
 */
public class MapStep {

    private int distance;
    private int duration;
    private LatLng start;
    private LatLng end;
    private String instruction;
    private List<LatLng> routeLine;

    public MapStep(int distance, int duration, LatLng start, LatLng end, String instruction, List<LatLng> routeLine) {
        this.distance = distance;
        this.duration = duration;
        this.start = start;
        this.end = end;
        this.instruction = instruction;
        this.routeLine = routeLine;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LatLng getStart() {
        return start;
    }

    public void setStart(LatLng start) {
        this.start = start;
    }

    public LatLng getEnd() {
        return end;
    }

    public void setEnd(LatLng end) {
        this.end = end;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public List<LatLng> getRouteLine() {
        return routeLine;
    }

    public void setRouteLine(List<LatLng> routeLine) {
        this.routeLine = routeLine;
    }
}
