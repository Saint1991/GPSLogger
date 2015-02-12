package geologger.saints.com.geologger.map;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.List;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.utils.Position;
import geologger.saints.com.geologger.utils.TimestampGenerator;


/**
 * Created by Mizuno on 2015/02/12.
 * Class for drawing objects on GoogleMapView
 * You MUST call setMap at first of all
 */
@EBean
public class BaseMapWorker {

    private final String TAG = getClass().getSimpleName();

    protected GoogleMap mMap;
    protected Marker mCurrentPositionMarker;

    @RootContext
    Context mContext;


    public BaseMapWorker() {}

    //region init
    /**
     * Initialize this instance with GoogleMap Object
     * This must be called at first of all
     */
    public void initMap(GoogleMap map) {

        init(map);

        updateCurrentPositionMarker();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentPositionMarker.getPosition(), 15));
    }

    /**
     * Initialize this instance with GoogleMap Object
     * A Marker is set on the designated position.
     * @param map
     * @param firstPosition
     */
    public void initMap(GoogleMap map, LatLng firstPosition, float markerColor, float alpha) {

        init(map);

        updateCurrentPositionMarker(firstPosition);
        mCurrentPositionMarker.setIcon(BitmapDescriptorFactory.defaultMarker(markerColor));
        mCurrentPositionMarker.setAlpha(alpha);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentPositionMarker.getPosition(), 15));
    }

    // Clear Map and set ClickListener on Marker
    private void init(GoogleMap map) {

        map.clear();
        setMap(map);

        if (mCurrentPositionMarker != null) {
            mCurrentPositionMarker = null;
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });
        
    }

    /**
     * Set Google Map Object to this instance
     * @param map
     */
    public void setMap(GoogleMap map) {
        this.mMap = map;
    }

    //endregion



    //region Marker

    /**
     * Draw a marker at the designated position
     * @param position
     * @return Marker Object that is drawn on the map. If map hasn't set, this returns null.
     */
    public Marker addMarker(LatLng position) {

        if (mMap == null) {
            return null;
        }

        MarkerOptions marker = new MarkerOptions();
        marker.position(position);
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        marker.title(mContext.getResources().getString(R.string.timestamp));
        marker.snippet(TimestampGenerator.getTimestamp());
        marker.alpha(0.4F);

        return mMap.addMarker(marker);
    }

    /**
     * Draw a marker at the designated position
     * @param latitude
     * @param longitude
     * @return Marker Object that is drawn on the map. If map hasn't set, this returns null.
     */
    public Marker addMarker(float latitude, float longitude) {
        LatLng position = new LatLng(latitude, longitude);
        return addMarker(position);
    }

    //endregion



    //region CurrentPositionMarker
    /**
     * Draw a marker that represents user's current position with the disignated position
     * @param position
     */
    public void updateCurrentPositionMarker(LatLng position) {

        if (mMap == null) {
            return;
        }

        if (mCurrentPositionMarker == null) {
            MarkerOptions marker = new MarkerOptions();
            marker.position(position).title(mContext.getResources().getString(R.string.imhere));
            mCurrentPositionMarker = mMap.addMarker(marker);
            return;
        }

        mCurrentPositionMarker.setPosition(position);

    }

    /**
     * Draw a marker that represents user's current position with the disignated position
     * @param latitude
     * @param longitude
     */
    public void updateCurrentPositionMarker(float latitude, float longitude) {
        LatLng position = new LatLng(latitude, longitude);
        updateCurrentPositionMarker(position);
    }


    /**
     * Draw a marker that represents user's current position.
     */
    public void updateCurrentPositionMarker() {

        float[] position = Position.getPosition(mContext);
        LatLng currentPosition = new LatLng(position[0], position[1]);

        updateCurrentPositionMarker(currentPosition);

    }

    //endregion



    //region Line

    /**
     * Draw blue line between two designated points
     * @param from
     * @param to
     * @return Polyline Object that is drawn on the map. If map hasn't set, this returns null.
     */
    public Polyline drawLine(LatLng from, LatLng to) {

        if (mMap == null) {
            return null;
        }

        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.width(6.0F);
        lineOptions.color(Color.BLUE);
        lineOptions.add(from, to);

        return mMap.addPolyline(lineOptions);
    }

    /**
     * Draw blue line between two designated points
     * @param fromLat
     * @param fromLng
     * @param toLat
     * @param toLng
     * @return Polyline Object that is drawn on the map. If map hasn't set, this returns null.
     */
    public Polyline drawLine(float fromLat, float fromLng, float toLat, float toLng) {

        LatLng from = new LatLng(fromLat, fromLng);
        LatLng to = new LatLng(toLat, toLng);

        return drawLine(from, to);
    }

    /**
     * Draw blue line connecting LatLngs in disignated list
     * @param latLngList
     * @return
     */
    public Polyline drawLine(List<LatLng> latLngList) {

        if (mMap == null) {
            return null;
        }

        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.width(6.0F);
        lineOptions.color(Color.BLUE);
        for (LatLng point : latLngList) {
            lineOptions.add(point);
        }

        return mMap.addPolyline(lineOptions);
    }

    //endregion

}
