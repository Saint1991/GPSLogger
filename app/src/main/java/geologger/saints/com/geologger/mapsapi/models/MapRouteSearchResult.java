package geologger.saints.com.geologger.mapsapi.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mizuno on 2015/04/01.
 */
public class MapRouteSearchResult {

    private List<MapLeg> mResult;

    public MapRouteSearchResult(List<MapLeg> result) {
        this.mResult = result;
    }

    /**
     * Get a list that contains all coordinates in each step
     * @return
     */
    public List<LatLng> getPolyLinePoints() {

        List<LatLng> ret = new ArrayList<>();
        for (MapLeg leg : mResult) {
            List<MapStep> steps = leg.getSteps();
            for (MapStep step : steps) {
                ret.addAll(step.getRouteLine());
            }
        }

        return ret;
    }

    /**
     * Get total time(minute)
     * @return
     */
    public int getTotalMinutes() {

        int totalSec = 0;
        for (MapLeg leg : mResult) {
            totalSec += leg.getDuration();
        }

        return totalSec / 60;
    }

    /**
     * Get Total Distance(m)
     * @return
     */
    public int getTotalDistance() {

        int totalDist = 0;
        for (MapLeg leg : mResult) {
            totalDist += leg.getDistance();
        }

        return totalDist;
    }

    /**
     * Get Instruction for each step as a list
     * @return
     */
    public List<String> getInstructionList() {

        List<String> instructions = new ArrayList<>();
        for (MapLeg leg: mResult) {
            List<MapStep> steps = leg.getSteps();
            for (MapStep step : steps) {
                instructions.add(step.getInstruction());
            }
        }

        return instructions;
    }

    /**
     * Get endAddress of the last leg entry as the destination
     * @return
     */
    public String getDestination() {
        int lastIndex = mResult.size() - 1;
        return mResult.get(lastIndex).getEndAddress();
    }
}
