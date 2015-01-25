package geologger.saints.com.geologger.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.services.GPSLoggingService_;


@EActivity
public class MapsActivity extends FragmentActivity {

    private final String TAG = getClass().getSimpleName();
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @ViewById(R.id.loggingStartButton)
    Button mLoggingStartButton;

    @ViewById(R.id.loggingStopButton)
    Button mLoggingStopButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        setUpMapIfNeeded();
    }

    @Click(R.id.loggingStartButton)
    public void onLoggingStart(View clicked) {

        Log.d(TAG, "onLoggingStart");

        Intent serviceIntent = new Intent(this.getApplicationContext(), GPSLoggingService_.class);
        startService(serviceIntent);

        clicked.setVisibility(View.GONE);
        mLoggingStopButton.setVisibility(View.VISIBLE);
    }

    @Click(R.id.loggingStopButton)
    public void onLoggingStop(View clicked) {

        Log.d(TAG, "onLoggingStop");

        Intent serviceIntent = new Intent(this.getApplicationContext(), GPSLoggingService_.class);
        stopService(serviceIntent);

        clicked.setVisibility(View.GONE);
        mLoggingStartButton.setVisibility(View.VISIBLE);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }


}
