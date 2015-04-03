package geologger.saints.com.geologger.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import java.util.List;
import java.util.UUID;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.database.CheckinFreeFormSQLite;
import geologger.saints.com.geologger.database.CheckinSQLite;
import geologger.saints.com.geologger.database.CompanionSQLite;
import geologger.saints.com.geologger.database.TrajectoryPropertySQLite;
import geologger.saints.com.geologger.database.TrajectorySQLite;
import geologger.saints.com.geologger.database.TrajectorySpanSQLite;
import geologger.saints.com.geologger.models.CheckinEntry;
import geologger.saints.com.geologger.models.CheckinFreeFormEntry;
import geologger.saints.com.geologger.models.CompanionEntry;
import geologger.saints.com.geologger.models.TrajectoryEntry;
import geologger.saints.com.geologger.models.TrajectoryPropertyEntry;
import geologger.saints.com.geologger.services.GPSLoggingService;
import geologger.saints.com.geologger.services.GPSLoggingService_;
import geologger.saints.com.geologger.map.MapWorker;

import geologger.saints.com.geologger.services.PositioningService_;
import geologger.saints.com.geologger.utils.EncourageGpsOn;
import geologger.saints.com.geologger.utils.IEncourageGpsOnAlertDialogCallback;
import geologger.saints.com.geologger.sensors.MyLocationListener;
import geologger.saints.com.geologger.utils.Position;
import geologger.saints.com.geologger.utils.ServiceRunningConfirmation;
import geologger.saints.com.geologger.utils.TimestampGenerator;


@EActivity
public class RecordActivity extends FragmentActivity {

    private final String TAG = getClass().getSimpleName();

    private final int POICONFIRMATIONCODE = 1;
    public static final int RECORDNOTIFICATIONCODE = 2;
    private final int BEGINRECORDINGCODE = 3;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String mCurrentTid;

    @SystemService
    LocationManager mLocationManager;

    @Bean
    CompanionSQLite mCompanionDbHandler;

    @Bean
    TrajectorySQLite mTrajectoryDbHandler;

    @Bean
    TrajectorySpanSQLite mTrajectorySpanDbHandler;

    @Bean
    TrajectoryPropertySQLite mTrajectoryPropertyDbHandler;

    @Bean
    CheckinSQLite mCheckinDbHandler;

    @Bean
    CheckinFreeFormSQLite mCheckinFreeFormDbHandler;

    @Bean
    ServiceRunningConfirmation mServiceRunningConfirmation;

    @Bean
    EncourageGpsOn mEncourageGpsOn;

    @Bean
    MapWorker mMapWorker;

    @ViewById(R.id.loggingStartButton)
    Button mLoggingStartButton;

    @ViewById(R.id.loggingStopButton)
    Button mLoggingStopButton;

    @ViewById(R.id.checkInButton)
    Button mCheckinButton;

    //region LifeCycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        setLoggingStateOnView();

        //This activity newly created
        if (savedInstanceState == null) {
            Log.i(TAG, "onCreate: savedInstanceState is null");
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(TrajectoryEntry.TID)) {
            mCurrentTid = savedInstanceState.getString(TrajectoryEntry.TID);
        }

        setUpMapIfNeeded();

    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        setUpMapIfNeeded();
        restartPositioningServiceIfRunning();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();

        if ( mServiceRunningConfirmation.isPositioning() && !mServiceRunningConfirmation.isLogging() ) {
            Intent intent = new Intent(getApplicationContext(), PositioningService_.class);
            stopService(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentTid != null) {
            outState.putString(TrajectoryEntry.TID, mCurrentTid);
            Log.i(TAG, "onSaveInstanceState " + mCurrentTid);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentTid = savedInstanceState.getString(TrajectoryEntry.TID);
        Log.i(TAG, "onRestoreInstanceState " + mCurrentTid);
    }

    //endregion]


    //region Logging

    /**
     * Called when Start Button Clicked
     * startLogginig and update View
     * @param clicked
     */
    @Click(R.id.loggingStartButton)
    public void onLoggingStartButtonClicked(View clicked) {

        Log.i(TAG, "onLoggingStart");

        startLoggingWithEncourageGpsOn();
        mMapWorker.initMap(mMap);
    }

    /**
     * Called When Stop Button Clicked
     * stop logging and update view
     * @param clicked
     */
    @Click(R.id.loggingStopButton)
    public void onLoggingStop(View clicked) {

        Log.i(TAG, "onLoggingStop");

        Intent serviceIntent = new Intent(this.getApplicationContext(), GPSLoggingService_.class);
        stopService(serviceIntent);
        mMapWorker.clearPrevious();

        setLoggingStateOnView();

        String toastMessage = getResources().getString(R.string.stop_logging);
        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }


    /**
     * Encourage to turn on the GPS and execute callback
     */
    private void startLoggingWithEncourageGpsOn() {
        mEncourageGpsOn.encourageGpsOn(new StartLoggingTask(), false);
    }

    //Restart PositioningService if it is running
    private void restartPositioningServiceIfRunning() {

        Intent intent = new Intent(getApplicationContext(), PositioningService_.class);
        if ( mServiceRunningConfirmation.isPositioning() ) {
            stopService(intent);
        }
        startService(intent);

    }

    class StartLoggingTask implements IEncourageGpsOnAlertDialogCallback {

        @Override
        public void executeTaskIfProviderIsEnabled() {

            //Restart PositioningService because provider for the locationListener is possibly changed
            restartPositioningServiceIfRunning();

            Intent intent = new Intent(getApplicationContext(), BeginRecordingActivity_.class);
            startActivityForResult(intent, BEGINRECORDINGCODE);
        }
    }

    @OnActivityResult(BEGINRECORDINGCODE)
    public void onBeginingResult(int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }

        final String title = data.getStringExtra(TrajectoryPropertyEntry.TITLE);
        final String memo = data.getStringExtra(TrajectoryPropertyEntry.DESCRIPTION);
        final String companion = data.getStringExtra(CompanionEntry.COMPANION);

        mCurrentTid = generateUniqueTid();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mCompanionDbHandler.insert(mCurrentTid, companion);
                mTrajectoryPropertyDbHandler.insert(mCurrentTid, title, memo);
            }
        }).start();

        if (mCurrentTid != null && mCurrentTid.length() > 1) {

            //Start GpsLoggingService with TID
            Intent intent = new Intent(getApplicationContext(), GPSLoggingService_.class);
            intent.putExtra(TrajectoryEntry.TID, mCurrentTid);
            startService(intent);

            //Update View and show the message
            setLoggingStateOnView();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.start_logging), Toast.LENGTH_SHORT).show();
        }

    }
    //endregion


    //region ViewControl

    //Change view controls according to Logging State
    private void setLoggingStateOnView() {

        if (mServiceRunningConfirmation.isLogging()) {

            mLoggingStartButton.setVisibility(View.GONE);
            mLoggingStopButton.setVisibility(View.VISIBLE);
            mCheckinButton.setVisibility(View.VISIBLE);

        } else {

            mLoggingStopButton.setVisibility(View.GONE);
            mCheckinButton.setVisibility(View.GONE);
            mLoggingStartButton.setVisibility(View.VISIBLE);

        }
    }

    //endregion


    //region Checkin

    /**
     * Start PoiConfirmationActivity
     * @param cliecked
     */
    @Click(R.id.checkInButton)
    public void onCheckedIn(View cliecked) {

        Log.i(TAG, "onCheckedIn");

        Intent intent = new Intent(getApplicationContext(), PoiConfirmationActivity_.class);
        startActivityForResult(intent, POICONFIRMATIONCODE);
    }

    /**
     * Called when finish executing
     * @param resultCode
     * @param data
     */
    @OnActivityResult(POICONFIRMATIONCODE)
    void onCheckinResult(int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }

        final String tid = mTrajectorySpanDbHandler.getLoggingTid();
        if (tid == null) {
            return;
        }

        float[] position = Position.getPosition(getApplicationContext());
        final float latitude = position[0];
        final float longitude = position[1];

        //Confirm whether result is by free form.
        boolean isFreeform = data.getBooleanExtra("IsFreeForm", false);
        if (!isFreeform) {

            final String placeId = data.getStringExtra(CheckinEntry.PLACEID);
            final String categoryId = data.getStringExtra(CheckinEntry.CATEGORYID);
            final String placeName = data.getStringExtra(CheckinEntry.PLACENAME);

            Toast.makeText(this, "Checkin " + placeName, Toast.LENGTH_SHORT).show();

            final CheckinEntry entry = new CheckinEntry(tid, placeId, categoryId, TimestampGenerator.getTimestamp(), latitude, longitude, placeName);

            new Thread(new Runnable() {

                @Override
                public void run() {
                    mCheckinDbHandler.insert(entry);
                }
            }).start();

            mMapWorker.addCheckinMarker(entry);
        }

        //By Free Form Result
        else {

            final String placeName = data.getStringExtra(CheckinFreeFormEntry.PLACENAME);
            Toast.makeText(this, "Checkin " + placeName, Toast.LENGTH_SHORT).show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mCheckinFreeFormDbHandler.insert(tid, placeName, latitude, longitude);
                }
            }).start();

            CheckinFreeFormEntry entry = new CheckinFreeFormEntry(tid, placeName, TimestampGenerator.getTimestamp(), latitude, longitude);
            mMapWorker.addCheckinMarker(entry);
        }



    }

    //endregion


    //region Map


    /**
     * This is called when a trajectory entry is stored in the database
     * @param intent
     */
    @Receiver(actions = GPSLoggingService.ACTION)
    public void onPositionLogged(Intent intent) {

        if (mMap == null || mMapWorker == null) {
            return;
        }

        float latitude = intent.getFloatExtra(Position.LATITUDE, 0.0f);
        float longitude = intent.getFloatExtra(Position.LONGITUDE, 0.0f);
        mMapWorker.addMarker(latitude, longitude);

    }

    /**
     * This is called when onLocationChanged in LocationListener is called.
     * Update the marker's position that represents user's current position.
     * @param intent
     */
    @Receiver(actions = MyLocationListener.ACTION)
    public void onCurrentPositionUpdated(Intent intent) {

        Log.i(TAG, MyLocationListener.ACTION);

        if (mMap == null || mMapWorker == null) {
            return;
        }

        float latitude = intent.getFloatExtra(Position.LATITUDE, 0.0f);
        float longitude = intent.getFloatExtra(Position.LONGITUDE, 0.0f);
        mMapWorker.updateCurrentPositionMarker(latitude, longitude);

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
    }


    private void setUpMap() {

        if (mMap == null || mMapWorker == null) {
            return;
        }

        mMapWorker.initMap(mMap);

        //restore map state
        String tid = mTrajectorySpanDbHandler.getLoggingTid();
        if (tid != null && mServiceRunningConfirmation.isLogging()) {
            Log.i(TAG, "restore map state");
            List<CheckinEntry> checkinList = mCheckinDbHandler.getCheckinList(tid);
            List<CheckinFreeFormEntry> checkinFreeFormList = mCheckinFreeFormDbHandler.getCheckinFreeFormList(tid);
            List<TrajectoryEntry> positionList = mTrajectoryDbHandler.getTrajectory(tid);
            mMapWorker.addMarkers(positionList);
            mMapWorker.addCheckinMarkers(checkinList);
            mMapWorker.addCheckinMarkers(checkinFreeFormList);
        }

    }

    //endregion

    //region utility
    private String generateUniqueTid() {

        String tidCandidate = null;
        while ( true ) {
            tidCandidate = UUID.randomUUID().toString();
            if (!mTrajectorySpanDbHandler.isExistTid(tidCandidate)) {
                break;
            }
        }

        return tidCandidate;
    }
    //endregion


}
