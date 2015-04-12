package geologger.saints.com.geologger.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
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
import geologger.saints.com.geologger.models.TrajectoryEntry;
import geologger.saints.com.geologger.utils.Position;
import geologger.saints.com.geologger.utils.TimestampUtil;


/**
 * Created by Mizuno on 2015/02/12.
 * Class for drawing objects on GoogleMapView
 * You MUST call setMap at first of all
 */
@EBean
public class BaseMapWorker {

    protected GoogleMap mMap;
    protected Marker mCurrentPositionMarker;
    protected boolean mUseMyLocation = false;

    @RootContext
    Activity mActivity;


    public BaseMapWorker() {mUseMyLocation = false;}

    //region initialize
    /**
     * Initialize this instance with GoogleMap Object
     * If useDefaultCurrentPositionMarker is true setMyLocationEnabled will be set true
     * @param map
     * @param useMyLocation
     */
    public void initMap(GoogleMap map, boolean useMyLocation) {

        init(map);
        mUseMyLocation = useMyLocation;

        if (mUseMyLocation) {
            enableMyLocation();
        } else {
            updateCurrentPositionMarker();
        }

        animateCameraToCurrentPosition();
    }

    /**
     * Initialize this instance with GoogleMap Object
     * A Marker is set on the designated position.
     * @param map
     * @param firstPosition
     */
    public void initMap(GoogleMap map, LatLng firstPosition) {
        init(map);
        updateCurrentPositionMarker(firstPosition);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentPositionMarker.getPosition(), 15));
    }

    /**
     * Initialize this instance with GoogleMap Object
     * A Marker is set on the designated position.
     * @param map
     * @param firstPosition
     * @param markerColor
     * @param alpha
     */
    public void initMap(GoogleMap map, LatLng firstPosition, float markerColor, float alpha) {
        initMap(map, firstPosition);
        mCurrentPositionMarker.setIcon(BitmapDescriptorFactory.defaultMarker(markerColor));
        mCurrentPositionMarker.setAlpha(alpha);
    }

    // Clear Map and set ClickListener on Marker
    private void init(GoogleMap map) {

        map.clear();
        setMap(map);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        setMyLocationButtonListener();
        mCurrentPositionMarker = null;
    }

    //endregion

    //region Marker

    /**
     * Add Marker with the designated color
     * @param position
     * @param color
     * @param alpha
     * @return
     */
    public Marker addMarker(LatLng position, float color, float alpha) {

        if (mMap == null) {
            return null;
        }

        MarkerOptions marker = new MarkerOptions();
        marker.position(position);
        marker.icon(BitmapDescriptorFactory.defaultMarker(color));
        marker.title(mActivity.getResources().getString(R.string.timestamp));
        marker.snippet(TimestampUtil.getTimestamp());
        marker.alpha(alpha);

        return mMap.addMarker(marker);
    }

    /**
     * dd Marker with the designated color
     * @param latitude
     * @param longitude
     * @param color
     * @param alpha
     * @return
     */
    public Marker addMarker(float latitude, float longitude, float color, float alpha) {
        LatLng position = new LatLng(latitude, longitude);
        return addMarker(position, color, alpha);
    }

    /**
     * Draw a marker at the designated position
     * @param position
     * @return Marker Object that is drawn on the map. If map hasn't set, this returns null.
     */
    public Marker addMarker(LatLng position) {
        return addMarker(position, BitmapDescriptorFactory.HUE_BLUE, 0.4F);
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

    /**
     * Draw a marker which corresponds to the designated entry
     * @param entry
     * @return
     */
    public Marker addMarker(TrajectoryEntry entry) {
        Marker marker = addMarker(entry.getLatitude(), entry.getLongitude());
        marker.setSnippet(entry.getTimestamp());
        return marker;
    }

    /**
     * add Markers at the designated position list
     * @param positionList
     */
    public void addMarkers(List<TrajectoryEntry> positionList) {
        for (TrajectoryEntry entry : positionList) {
            addMarker(entry);
        }
    }
    //endregion

    //region CurrentPositionMarker

    /**
     * Draw a marker that represents user's current position with the designated position
     * @param position
     */
    public void updateCurrentPositionMarker(LatLng position) {

        if (mMap == null || mUseMyLocation) {
            return;
        }

        if (position == null) {
            float[] pos = Position.getPosition(mActivity.getApplicationContext());
            position = new LatLng(pos[0], pos[1]);
        }


        if (mCurrentPositionMarker == null) {
            MarkerOptions marker = new MarkerOptions();
            marker.position(position).title(mActivity.getResources().getString(R.string.current_position));
            mCurrentPositionMarker = mMap.addMarker(marker);
            return;
        }

        mCurrentPositionMarker.setPosition(position);
    }

    /**
     * Draw a marker that represents user's current position with the designated position
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
        updateCurrentPositionMarker(null);
    }

    //endregion

    //region Line

    public Polyline drawLine(LatLng from, LatLng to, int color) {

        if (mMap == null) {
            return null;
        }

        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.width(6.0F);
        lineOptions.color(color);
        lineOptions.add(from, to);

        return mMap.addPolyline(lineOptions);
    }

    /**
     * Draw blue line between two designated points
     * @param from
     * @param to
     * @return Polyline Object that is drawn on the map. If map hasn't set, this returns null.
     */
    public Polyline drawLine(LatLng from, LatLng to) {
        return drawLine(from, to, Color.BLUE);
    }

    /**
     * Draw line between two designated points
     * @param fromLat
     * @param fromLng
     * @param toLat
     * @param toLng
     * @return Polyline Object that is drawn on the map. If map hasn't set, this returns null.
     */
    public Polyline drawLine(float fromLat, float fromLng, float toLat, float toLng, int color) {

        LatLng from = new LatLng(fromLat, fromLng);
        LatLng to = new LatLng(toLat, toLng);

        return drawLine(from, to, color);
    }

    /**
     * Draw blue line between two designated points
     * @param fromLat
     * @param fromLng
     * @param toLat
     * @param toLng
     * @return
     */
    public Polyline drawLine(float fromLat, float fromLng, float toLat, float toLng) {
        return drawLine(fromLat, fromLng, toLat, toLng, Color.BLUE);
    }

    /**
     * Draw line connecting LatLngs in designated list
     * @param latLngList
     * @return
     */
    public Polyline drawLine(List<LatLng> latLngList, int color) {

        if (mMap == null) {
            return null;
        }

        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.width(6.0F);
        lineOptions.color(color);
        for (LatLng point : latLngList) {
            lineOptions.add(point);
        }

        return mMap.addPolyline(lineOptions);
    }

    /**
     * Draw blue line connecting LatLngs in designated list
     * @param latLngList
     * @return
     */
    public Polyline drawLine(List<LatLng> latLngList) {
        return drawLine(latLngList, Color.BLUE);
    }

    //endregion

    //region camera

    protected void animateCameraToCurrentPosition() {

        float[] position = Position.getPosition(mActivity.getApplicationContext());
        LatLng moveTo = new LatLng(position[0], position[1]);

        if (moveTo != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(moveTo, 15));
        }
    }

    //endregion

    //region utility

    /**
     * Show MyLocation Button and enable it
     */
    public void enableMyLocation() {

        if (mMap != null) {

            mMap.setMyLocationEnabled(true);

            UiSettings settings = mMap.getUiSettings();
            settings.setCompassEnabled(true);
            settings.setMyLocationButtonEnabled(true);

            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {

                    try {
                        Context context = mActivity.getApplicationContext();
                        Position.savePosition(context, (float)location.getLatitude(), (float)location.getLongitude());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            setMyLocationButtonListener();
            mUseMyLocation = true;
        }
    }

    /**
     * Set MyLocationButtonListener
     */
    private void setMyLocationButtonListener() {
        if (mMap != null) {
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    animateCameraToCurrentPosition();
                    return true;
                }
            });
        }
    }

    /**
     * Set Google Map Object to this instance
     * @param map
     */
    public void setMap(GoogleMap map) {
        this.mMap = map;
    }

    public boolean isMyLocationEnabled() {
        return mUseMyLocation;
    }
    //endregion

    //clear map

    /**
     * Clear the map If Map is not null
     */
    public void clear() {
        if (mMap != null) {
            mMap.clear();
        }
    }

}
