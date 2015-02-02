package geologger.saints.com.geologger.map;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import geologger.saints.com.geologger.utils.TimestampGenerator;

/**
 * Created by Mizuno on 2015/01/31.
 * GoogleMapへの書き込みを管理するクラス
 */
public class MapWorker {

    private GoogleMap mMap;
    private List<MarkerOptions> mMarkers;
    private Marker mCurrentPosition;

    public MapWorker(GoogleMap map, Marker current) {

        this.mMap = map;
        this.mMarkers = new ArrayList<MarkerOptions>();
        this.mCurrentPosition = current;

        if (this.mMap != null) {
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    marker.showInfoWindow();
                    return false;
                }
            });

        }

        LatLng position = mCurrentPosition.getPosition();
        updateCurrentMarkerPosition((float)position.latitude, (float)position.longitude);
    }

    public void addMarker(float latitude, float longitude) {

        try {
            MarkerOptions marker = new MarkerOptions();
            marker.position(new LatLng(latitude, longitude));
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            marker.title("Timestamp");
            marker.snippet(TimestampGenerator.getTimestamp());

            mMarkers.add(marker);
            mMap.addMarker(marker);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateCurrentMarkerPosition(float latitude, float longitude) {

        if (mMap == null) {
            return;
        }

        try {
            MarkerOptions option = new MarkerOptions();
            option.position(mCurrentPosition.getPosition()).title(mCurrentPosition.getTitle());
            this.mCurrentPosition.remove();
            this.mCurrentPosition = this.mMap.addMarker(option);
        } catch (Exception e) {
          e.printStackTrace();
        }

    }

}
