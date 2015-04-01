package geologger.saints.com.geologger.map;


import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.androidannotations.annotations.EBean;

import java.util.HashMap;
import java.util.List;

import geologger.saints.com.geologger.R;
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
    private HashMap<String, String> mCheckinMarkerIdMap;

    private static final String FOURSQUARE_ROOT = "https://ja.foursquare.com/v/";

    public MapWorker() {
        mCheckinMarkerIdMap = new HashMap<String, String>();
    }

    /**
     * Initializing map and set Click Event to infowindow of Checkin marker
     * @param map
     * @param firstPosition
     * @param markerColor
     * @param alpha
     */
    @Override
    public void initMap(GoogleMap map, LatLng firstPosition, float markerColor, float alpha) {

        super.initMap(map, firstPosition, markerColor, alpha);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                String id = marker.getId();
                if (mCheckinMarkerIdMap.containsKey(id)) {
                    String placeId = mCheckinMarkerIdMap.get(id);
                    String url = FOURSQUARE_ROOT + placeId;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    mActivity.startActivity(intent);
                }
            }
        });
    }

    /**
     * Clear recorded previous position.
     */
    public void clearPrevious() {
        mPreviousPosition = null;
    }

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

    /**
     * Add Checkin Marker at the corresponding point to the entry
     * @param entry
     * @return
     */
    public Marker addCheckinMarker(CheckinFreeFormEntry entry) {

        LatLng position = new LatLng(entry.getLatitude(), entry.getLongitude());

        MarkerOptions marker = new MarkerOptions();
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.checkin_pin));
        marker.anchor(0.0F, 1.0F);
        marker.position(position);
        marker.title(entry.getPlaceName());

        marker.snippet("time: " + entry.getTimestamp());

        Marker ret = mMap.addMarker(marker);
        if (entry instanceof CheckinEntry) {
            mCheckinMarkerIdMap.put(ret.getId(), ((CheckinEntry) entry).getPlaceId());
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

    public Marker addDistinationMarker(LatLng position, String placeName, String address) {

        if (position == null) {
            return null;
        }

        MarkerOptions marker = new MarkerOptions();
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        marker.position(position);
        if (placeName != null && address != null) {
            marker.title(placeName);
            marker.snippet(address);
        }

        Marker ret = mMap.addMarker(marker);
        return ret;
    }

    //endregion

}
