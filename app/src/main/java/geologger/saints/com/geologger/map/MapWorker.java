package geologger.saints.com.geologger.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.androidannotations.annotations.EBean;

/**
 * Created by Mizuno on 2015/01/31.
 * Class for drawing objects on GoogleMapView
 * You MUST call setMap at first of all
 */
@EBean
public class MapWorker extends BaseMapWorker {

    private LatLng mPreviousPosition = null;

    public MapWorker() {}

    //region Marker

    /**
     * Draw Marker at the disginated point and connect with the previous point with blue line
     * @param latitude
     * @param longitude
     */
    @Override
    public Marker addMarker(float latitude, float longitude) {

        LatLng position = new LatLng(latitude, longitude);
        return addMarker(position);

    }

    /**
     * Draw Marker at the disginated point and connect with the previous point with blue line
     * @param position
     * @return
     */
    @Override
    public Marker addMarker(LatLng position) {

        Marker ret = super.addMarker(position);

        if (mPreviousPosition != null) {
           super.drawLine(this.mPreviousPosition, position);
        }
        mPreviousPosition = position;

        return ret;
    }

    //endregion
}
