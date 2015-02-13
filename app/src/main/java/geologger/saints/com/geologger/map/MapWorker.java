package geologger.saints.com.geologger.map;


import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.foursquare.FourSquareClient;
import geologger.saints.com.geologger.models.CheckinEntry;
import geologger.saints.com.geologger.models.CheckinFreeFormEntry;

/**
 * Created by Mizuno on 2015/01/31.
 * Class for drawing objects on GoogleMapView
 * You MUST call setMap at first of all
 */
@EBean
public class MapWorker extends BaseMapWorker {

    private LatLng mPreviousPosition = null;
    private Marker mCurrentInfoOpenMarker = null;
    private String mUpdateFinishInfoOpenMarkerId = null;
    private HashMap<String, String> mCheckinMarkerIdMap;
    private List<String> mCheckinFreeFormMarkerIdList;

    public MapWorker() {
        mCheckinMarkerIdMap = new HashMap<String, String>();
        mCheckinFreeFormMarkerIdList = new ArrayList<String>();
    }

    /**
     * Initializing map and set Click Event to infowindow of Checkin marker
     * @param map
     * @param firstPosition
     * @param markerColor
     * @param alpha
     */
    public void initMap(GoogleMap map, LatLng firstPosition, float markerColor, float alpha, boolean isCheckinMarkerDeletable) {
        super.initMap(map, firstPosition, markerColor, alpha);
        clearCheckinList();
        clearPrevious();
        setMapListeners(isCheckinMarkerDeletable);
    }

    public void initMap(GoogleMap map, float markerColor, float alpha, boolean isCheckinMarkerDeletable) {
        super.initMap(map, markerColor, alpha);
        clearCheckinList();
        clearPrevious();
        setMapListeners(isCheckinMarkerDeletable);
    }

    public void initMap(GoogleMap map, boolean isCheckInMarkerDeletable) {
        super.initMap(map);
        clearCheckinList();
        clearPrevious();
        setMapListeners(isCheckInMarkerDeletable);
    }

    private void setMapListeners(final boolean isCheckinMarkerDeletable) {

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {

                String id = marker.getId();
                String placeId = isCheckinMarker(id) ? mCheckinMarkerIdMap.get(id) : null;

                if (!isNormalMarker(id)) {
                    mMap.setInfoWindowAdapter(new CheckinInfoWindowAdapter(mActivity, null, isCheckinMarkerDeletable));
                }

                mCurrentInfoOpenMarker = marker;
                marker.showInfoWindow();

                return true;
            }
        });

        if (!isCheckinMarkerDeletable) {
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    String id = marker.getId();
                    if (isCheckinMarker(id)) {
                        String placeId = mCheckinMarkerIdMap.get(id);
                        String url = FourSquareClient.FOURSQUARE_ROOT + placeId;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        mActivity.startActivity(intent);
                    }
                }

            });
        }

    }

    /**
     *
     */
    public void reRenderInfoWindowIfNeeded(String placeId) {

        boolean isUpdateNeeded = !mCurrentInfoOpenMarker.getId().equals(mUpdateFinishInfoOpenMarkerId);

        if (isUpdateNeeded) {
            mCurrentInfoOpenMarker.hideInfoWindow();
            mCurrentInfoOpenMarker.showInfoWindow();
            mUpdateFinishInfoOpenMarkerId = mCurrentInfoOpenMarker.getId();
        }
    }



    /**
     * Clear Checkin Marker's ID List
     */
    public void clearCheckinList() {
        mCheckinMarkerIdMap.clear();
        mCheckinFreeFormMarkerIdList.clear();
    }

    /**
     * Clear recorded previous position.
     */
    public void clearPrevious() {
        mPreviousPosition = null;
    }

    //region Marker

    public boolean isCheckinMarker(String markerId) {
        return mCheckinMarkerIdMap.containsKey(markerId);
    }

    public boolean isNormalMarker(String markerId) {
        return !(isCheckinMarker(markerId) || mCheckinFreeFormMarkerIdList.contains(markerId));
    }

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

    /**
     * Add Checkin Marker at the corresponding point to the entry
     * @param entry
     * @return
     */
    public Marker addCheckinMarker(CheckinFreeFormEntry entry) {

        MarkerOptions marker = new MarkerOptions();
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.checkin_pin));
        marker.anchor(0.0F, 1.0F);
        marker.position(new LatLng(entry.getLatitude(), entry.getLongitude()));
        marker.title(entry.getPlaceName());

        marker.snippet(entry.getTimestamp());

        Marker ret = mMap.addMarker(marker);
        if (entry instanceof CheckinEntry) {
            mCheckinMarkerIdMap.put(ret.getId(), ((CheckinEntry) entry).getPlaceId());
        } else if (entry instanceof CheckinFreeFormEntry) {
            mCheckinFreeFormMarkerIdList.add(ret.getId());
        }

        return ret;
    }

    /**
     * Add Checkin Markers at the corresponding points to the entry List
     * @param checkinEntryList
     */
    public void addCheckinMarkers(List<? extends CheckinFreeFormEntry> checkinEntryList) {

        for (CheckinFreeFormEntry entry : checkinEntryList) {
            addCheckinMarker(entry);
        }

    }

    //endregion

}
