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
import geologger.saints.com.geologger.activities.PreviewActivity;
import geologger.saints.com.geologger.activities.PreviewActivity_;
import geologger.saints.com.geologger.map.infowindow.PhotoInfoAdapter;
import geologger.saints.com.geologger.models.CheckinEntry;
import geologger.saints.com.geologger.models.CheckinFreeFormEntry;
import geologger.saints.com.geologger.models.PhotoEntry;

/**
 * Created by Mizuno on 2015/01/31.
 * Class for drawing objects on GoogleMapView
 * You MUST call setMap at first of all
 */
@EBean
public class MapWorker extends BaseMapWorker {

    private static final String FOURSQUARE_ROOT = "https://ja.foursquare.com/v/";

    //Map MarkerID to the entry
    private HashMap<String, CheckinEntry> mCheckinMarkerMap;
    private HashMap<String, PhotoEntry> mPhotoEntryMarkerMap;

    public MapWorker() {
        mCheckinMarkerMap = new HashMap<>();
        mPhotoEntryMarkerMap = new HashMap<>();
    }

    //region initialize

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

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (isCheckinMarker(marker)) {
                    onCheckinMarkerClicked(marker);
                } else if (isCameraMarker(marker)) {
                    onCameraMarkerClicked(marker);
                }

                return false;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                if (isCheckinMarker(marker)) {
                    onCheckinInfoClicked(marker);
                } else if (isCameraMarker(marker)) {
                    onCameraInfoClicked(marker);
                }
            }
        });
    }

    //endregion


    //region marker

    //region CheckIn

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
            mCheckinMarkerMap.put(ret.getId(), (CheckinEntry) entry);
        }

        return ret;
    }

    private boolean isCheckinMarker(Marker marker) {
        String id = marker.getId();
        return mCheckinMarkerMap.containsKey(id);
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

    private void onCheckinMarkerClicked(Marker marker) {
        mMap.setInfoWindowAdapter(null);
    }

    private void onCheckinInfoClicked(Marker marker) {
        String placeId = mCheckinMarkerMap.get(marker.getId()).getPlaceId();
        String url = FOURSQUARE_ROOT + placeId;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mActivity.startActivity(intent);
    }

    //endregion

    /**
     * Add Marker that reporesent destination
     * @param position
     * @param placeName
     * @param address
     * @return
     */
    public Marker addDestinationMarker(LatLng position, String placeName, String address) {

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

    //region Camera

    public Marker addCameraMarker(PhotoEntry entry) {

        if (mMap == null || entry == null || entry.getFilePath() == null || entry.getFilePath().length() < 2) {
            return null;
        }

        MarkerOptions marker = new MarkerOptions();
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.camera_icon));
        marker.position(new LatLng(entry.getLatitude(), entry.getLongitude()));

        Marker ret = mMap.addMarker(marker);
        mPhotoEntryMarkerMap.put(ret.getId(), entry);
        return ret;
    }

    public void addCameraMarkers(List<PhotoEntry> photoEntryList) {
        for (PhotoEntry entry : photoEntryList) {
            addCameraMarker(entry);
        }
    }

    private boolean isCameraMarker(Marker marker) {
        return mPhotoEntryMarkerMap.containsKey(marker.getId());
    }

    private void onCameraMarkerClicked(Marker marker) {

        if (mMap == null) {
            return;
        }

        mMap.setInfoWindowAdapter(new PhotoInfoAdapter(mActivity, mPhotoEntryMarkerMap.get(marker.getId())));
    }

    private void onCameraInfoClicked(Marker marker) {

        if (mMap == null) {
            return;
        }

        PhotoEntry entry = mPhotoEntryMarkerMap.get(marker.getId());
        Intent intent = new Intent(mActivity.getApplicationContext(), PreviewActivity_.class);
        intent.putExtra(PhotoEntry.FILEPATH, entry.getFilePath());
        intent.putExtra(PhotoEntry.MEMO, entry.getMemo());
        intent.putExtra(PreviewActivity.ISVIEWMODE , true);
        mActivity.startActivity(intent);

    }

    //endregion

    //endregion

}
