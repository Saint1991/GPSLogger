package geologger.saints.com.geologger.mapsapi.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Mizuno on 2015/04/01.
 */
public class MapLeg {

    private int distance;
    private int duration;
    private LatLng start;
    private LatLng end;
    private String startAddress;
    private String endAddress;
    private List<MapStep> steps;

    public MapLeg(int distance, int duration, LatLng start, LatLng end, String startAddress, String endAddress, List<MapStep> steps) {
        this.distance = distance;
        this.duration = duration;
        this.start = start;
        this.end = end;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.steps = steps;
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

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public List<MapStep> getSteps() {
        return steps;
    }

    public void setSteps(List<MapStep> steps) {
        this.steps = steps;
    }
}
