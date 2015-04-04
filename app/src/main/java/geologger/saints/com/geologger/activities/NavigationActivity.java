package geologger.saints.com.geologger.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.map.MapWorker;
import geologger.saints.com.geologger.mapsapi.MapsApiParser;
import geologger.saints.com.geologger.mapsapi.models.MapRouteSearchResult;
import geologger.saints.com.geologger.models.CheckinEntry;
import geologger.saints.com.geologger.models.TrajectoryEntry;
import geologger.saints.com.geologger.mapsapi.MapsApiClient;
import geologger.saints.com.geologger.utils.Position;
import geologger.saints.com.geologger.utils.ProgressDialogUtility;

@EActivity
public class NavigationActivity extends FragmentActivity {

    private final String TAG = getClass().getSimpleName();
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private LatLng mDestination;
    private String mPlaceName = null;
    private String mAddress = null;

    private MapRouteSearchResult mSearchResult = null;

    @Bean
    MapWorker mMapWorker;

    @Bean
    MapsApiClient mMapApiClient;

    @Bean
    ProgressDialogUtility mProgressUtility;

    @ViewById(R.id.destination)
    TextView mDestinationText;

    @ViewById(R.id.distance)
    TextView mDistanceText;

    @ViewById(R.id.duration)
    TextView mDurationText;

    @ViewById(R.id.instruction_button)
    ImageButton mInstructioinButton;


    //region lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        initialize();
    }

    //endregion

    //region initialize

    private void initialize() {

        //Show Progress Dialog
        mProgressUtility.showProgress(getResources().getString(R.string.position_updating));

        //Get Arguments from intent
        Intent intent = getIntent();
        mDestination = new LatLng(intent.getDoubleExtra(TrajectoryEntry.LATITUDE, 0.0), intent.getDoubleExtra(TrajectoryEntry.LONGITUDE, 0.0));
        mPlaceName = intent.getStringExtra(CheckinEntry.PLACENAME);
        mAddress = intent.getStringExtra("Address");

        //Initializing the Map
        setUpMapIfNeeded();

        //Start searching based on the arguments
        startSearching();

        //Dismiss Progress Dialog
        mProgressUtility.dismissProgress();
    }

    private void setUpMapIfNeeded() {

        if (mMap == null) {

            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        if (mMap == null || mMapWorker == null) {
            return;
        }

        mMapWorker.initMap(mMap, true);
        mMapWorker.addDestinationMarker(mDestination, mPlaceName, mAddress);
    }

    //endregion

    //region searching

    private void startSearching() {

        new Thread(new Runnable() {

            @Override
            public void run() {

                if (mDestination == null) {
                    return;
                }

                float[] position = Position.getPosition(getApplicationContext());
                LatLng origin = new LatLng(position[0], position[1]);

                String response = mMapApiClient.query(origin, mDestination, getResources().getConfiguration().locale.getLanguage());
                if (response == null) {
                    showAlertMessage();
                    mProgressUtility.dismissProgress();
                } else {

                    //ここでガイドのルートを描画する処理を記述
                    mSearchResult = new MapRouteSearchResult(MapsApiParser.parseRoute(response));
                    if (mSearchResult != null) {
                        afterSearching();
                    }
                }
            }

        }).start();
    }

    //endregion

    //region updateview

    @UiThread
    public void afterSearching() {
        drawNavigationLine();
        setDestinationText();
        setDistanceText();
        setDurationText();
    }

    @UiThread
    public void drawNavigationLine() {
        if (mMap != null && mMapWorker != null && mSearchResult != null) {
            mMapWorker.drawLine(mSearchResult.getPolyLinePoints());
        }
    }

    @UiThread
    public void setDestinationText() {
        String destination = mSearchResult.getDestination();
        if (destination != null) {
            mDestinationText.setText(mSearchResult.getDestination());
        }
    }

    @UiThread
    public void setDistanceText() {
        int distance = mSearchResult.getTotalDistance();
        String distanceStr = distance > 1000 ? distance + " m" : ((double)distance / 1000.0) + " km";
        mDistanceText.setText(distanceStr);
    }

    @UiThread
    public void setDurationText() {
        int minutes = mSearchResult.getTotalMinutes();
        mDurationText.setText(minutes + " min");
    }

    //endregion

    //region navigationDialog

    @Click(R.id.instruction_button)
    public void instructionButtonClicked() {

        if (mSearchResult == null) {
            return;
        }

        StringBuilder body = new StringBuilder();
        List<String> instructionList = mSearchResult.getInstructionList();
        int index = 1;
        for (String instruction : instructionList) {
            body.append(Html.fromHtml(index++ + ". " + instruction + "<br>"));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.guide));
        builder.setMessage(body.toString());
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    //endregion

    //region utility

    @UiThread
    public void showAlertMessage() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_connectivity_alert), Toast.LENGTH_SHORT).show();
    }

    //endregion
}
