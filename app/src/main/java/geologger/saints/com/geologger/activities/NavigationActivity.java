package geologger.saints.com.geologger.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.map.MapWorker;
import geologger.saints.com.geologger.models.TrajectoryEntry;
import geologger.saints.com.geologger.mapsapi.MapApiClient;
import geologger.saints.com.geologger.utils.Position;

@EActivity
public class NavigationActivity extends FragmentActivity {

    private final String TAG = getClass().getSimpleName();
    private ProgressDialog mProgress;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng mDestination;

    @Bean
    MapWorker mMapWorker;

    @Bean
    MapApiClient mMapApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage(getResources().getString(R.string.position_updating));
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.show();


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


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



        new Thread(new Runnable() {
            @Override
            public void run() {

                float[] position = Position.getPosition(getApplicationContext());
                LatLng origin = new LatLng(position[0], position[1]);
                Intent intent = getIntent();
                mDestination = new LatLng(intent.getDoubleExtra(TrajectoryEntry.LATITUDE, 0.0), intent.getDoubleExtra(TrajectoryEntry.LONGITUDE, 0.0));

                String response = mMapApiClient.query(origin, mDestination);


                dismissProgress();
            }
        }).start();

        setUpMapIfNeeded();
    }

    private void setUpMap() {

        if (mMap == null || mMapWorker == null) {
            return;
        }

        mMapWorker.initMap(mMap);


    }


    @UiThread
    private void dismissProgress() {
        if (mProgress != null) {
            mProgress.dismiss();
        }
    }
}
