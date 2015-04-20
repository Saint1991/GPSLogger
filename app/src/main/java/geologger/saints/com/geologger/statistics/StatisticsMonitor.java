package geologger.saints.com.geologger.statistics;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.EBean;

import geologger.saints.com.geologger.models.TrajectoryStatisticalEntry;
import geologger.saints.com.geologger.utils.TimestampUtil;

/**
 * Created by Mizuno on 2015/04/06.
 */
@EBean
public class StatisticsMonitor {

    //km/h
    private final float SPEEDTHRESHOULD = 4;

    private LatLng mPreviousPosition = null;
    private long mPreviousTimestamp = -1L;
    private float mPreviousSpeed = -1.0F;


    public StatisticsMonitor() {
        clearPrevious();
    }

    public void clearPrevious() {
        mPreviousPosition = null;
        mPreviousTimestamp = -1L;
    }

    public TrajectoryStatisticalEntry culcEntryInfoFromPrevious(String tid, LatLng currentPosition) {

        long now = System.currentTimeMillis();

        if (mPreviousPosition == null || mPreviousTimestamp == -1L) {
            mPreviousPosition = currentPosition;
            mPreviousTimestamp = now;
            return null;
        }

        long duration = now - mPreviousTimestamp;

        float[] distanceArray = new float[1];
        Location.distanceBetween(mPreviousPosition.latitude, mPreviousPosition.longitude, currentPosition.latitude, currentPosition.longitude, distanceArray);
        float distance = distanceArray[0];

        mPreviousTimestamp = now;
        mPreviousPosition = currentPosition;

        float speed = distance / duration; // (m/msec)
        if (mPreviousSpeed != -1.0F) {
            if (Math.abs(mPreviousSpeed - speed) > SPEEDTHRESHOULD / 3600.0F) {
                mPreviousSpeed = speed;
                return null;
            }
        }

        mPreviousSpeed = speed;

        String timestamp = TimestampUtil.getTimestamp(now);
        return new TrajectoryStatisticalEntry(tid, duration, distance, speed, timestamp);
    }
}
