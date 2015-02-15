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

import com.google.android.gms.identity.intents.AddressConstants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import java.util.UUID;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.database.CheckinFreeFormSQLite;
import geologger.saints.com.geologger.database.CheckinSQLite;
import geologger.saints.com.geologger.database.CompanionSQLite;
import geologger.saints.com.geologger.database.TrajectorySpanSQLite;
import geologger.saints.com.geologger.models.CheckinEntry;
import geologger.saints.com.geologger.models.CheckinFreeFormEntry;
import geologger.saints.com.geologger.models.TrajectoryEntry;
import geologger.saints.com.geologger.services.GPSLoggingService;
import geologger.saints.com.geologger.services.GPSLoggingService_;
import geologger.saints.com.geologger.map.MapWorker;

import geologger.saints.com.geologger.services.PositioningService_;
import geologger.saints.com.geologger.uicomponents.FourSquarePhotoLoaderImageView;
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

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @SystemService
    LocationManager mLocationManager;

    @Bean
    CompanionSQLite mCompanionDbHandler;

    @Bean
    TrajectorySpanSQLite mTrajectorySpanDbHandler;

    @Bean
    CheckinSQLite mCheckinSQLite;

    @Bean
    CheckinFreeFormSQLite mCheckinFreeFormSQLite;

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
        setUpMapIfNeeded();
        setLoggingStateOnView();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        setUpMapIfNeeded();
        restartPositioningServiceIfRunning();
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

            //Definitions of Companion Selection Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this, AddressConstants.Themes.THEME_DARK);
            builder.setTitle(getResources().getString(R.string.companion));

            final String[] candidates = getResources().getStringArray(R.array.companion_candidate_list);
            final boolean[] checks = new boolean[candidates.length];
            checks[0] = true;
            builder.setMultiChoiceItems(R.array.companion_candidate_list, checks, new DialogInterface.OnMultiChoiceClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    checks[which] = isChecked;
                }

            });


            //Define the process when OK is clicked
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Resources resources = getResources();

                    //make companions string
                    boolean isValid = false;
                    final StringBuilder companions = new StringBuilder();
                    for (int i = 0; i < checks.length; i++) {
                        if (checks[i]) {
                            isValid = true;
                            companions.append(candidates[i] + ",");
                        }
                    }

                    // If no option is selected, show Toast to alert and finish
                    if (!isValid) {
                        Toast.makeText(getApplicationContext(), resources.getString(R.string.companion_alert), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //Make Unique TID
                    String tidCandidate = null;
                    while ( true ) {
                        tidCandidate = UUID.randomUUID().toString();
                        if (!mTrajectorySpanDbHandler.isExistTid(tidCandidate)) {
                            break;
                        }
                    }


                    //Insert companions into DB in the other thread
                    final String tid = tidCandidate;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mCompanionDbHandler.insert(tid, companions.substring(0, companions.length() - 1));
                        }
                    }).start();

                    //Start GpsLoggingService with TID
                    Intent intent = new Intent(getApplicationContext(), GPSLoggingService_.class);
                    intent.putExtra(TrajectoryEntry.TID, tid);
                    startService(intent);

                    //Update View and show the message
                    setLoggingStateOnView();
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.start_logging), Toast.LENGTH_SHORT).show();
                }

            });

            builder.show();
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
                    mCheckinSQLite.insert(entry);
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
                    mCheckinFreeFormSQLite.insert(tid, placeName, latitude, longitude);
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

        if (mMap == null || mMapWorker == null) {
            return;
        }
        mMapWorker.initMap(mMap);
    }

    //endregion

}
