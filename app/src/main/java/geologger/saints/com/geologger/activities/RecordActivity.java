package geologger.saints.com.geologger.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.database.CheckinFreeFormSQLite;
import geologger.saints.com.geologger.database.CheckinSQLite;
import geologger.saints.com.geologger.database.CompanionSQLite;
import geologger.saints.com.geologger.database.PhotoSQLite;
import geologger.saints.com.geologger.database.TrajectoryPropertySQLite;
import geologger.saints.com.geologger.database.TrajectorySQLite;
import geologger.saints.com.geologger.database.TrajectorySpanSQLite;
import geologger.saints.com.geologger.models.CheckinEntry;
import geologger.saints.com.geologger.models.CheckinFreeFormEntry;
import geologger.saints.com.geologger.models.CompanionEntry;
import geologger.saints.com.geologger.models.PhotoEntry;
import geologger.saints.com.geologger.models.TrajectoryEntry;
import geologger.saints.com.geologger.models.TrajectoryPropertyEntry;
import geologger.saints.com.geologger.services.GPSLoggingService;
import geologger.saints.com.geologger.services.GPSLoggingService_;
import geologger.saints.com.geologger.map.MapWorker;

import geologger.saints.com.geologger.services.PositioningService_;
import geologger.saints.com.geologger.utils.EncourageGpsOn;
import geologger.saints.com.geologger.sensors.MyLocationListener;
import geologger.saints.com.geologger.utils.Position;
import geologger.saints.com.geologger.utils.ProgressDialogUtility;
import geologger.saints.com.geologger.utils.ServiceRunningConfirmation;
import geologger.saints.com.geologger.utils.TidGenerator;
import geologger.saints.com.geologger.utils.TimestampUtil;


@EActivity
public class RecordActivity extends FragmentActivity {

    private final String TAG = getClass().getSimpleName();

    private final int POICONFIRMATIONCODE = 1;
    public static final int RECORDNOTIFICATIONCODE = 2;
    private final int BEGINRECORDINGCODE = 3;
    private final int CAMERACODE = 4;
    private final int PREVIEWCODE = 5;

    private GoogleMap mMap;
    private String mCurrentTid;
    private String mCurrentPhotoPath;

    private BroadcastReceiver mPositionLoggedReceiver = null;
    private BroadcastReceiver mPositionUpdatedReceiver = null;

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
    PhotoSQLite mPhotoDbHandler;

    @Bean
    ServiceRunningConfirmation mServiceRunningConfirmation;

    @Bean
    EncourageGpsOn mEncourageGpsOn;

    @Bean
    MapWorker mMapWorker;

    @Bean
    ProgressDialogUtility mProgressUtility;

    @Bean
    TidGenerator mTidGenerator;


    @ViewById(R.id.logging_start_button)
    Button mLoggingStartButton;

    @ViewById(R.id.logging_stop_button)
    Button mLoggingStopButton;

    @ViewById(R.id.check_in_button)
    Button mCheckinButton;

    @ViewById(R.id.camera_button)
    ImageButton mCameraButton;

    //region LifeCycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        //Change Buttons considering if logging is going
        setLoggingStateOnView();

        //Get TID from savedInstance state if available
        if (savedInstanceState != null && savedInstanceState.containsKey(TrajectoryEntry.TID)) {
            mCurrentTid = savedInstanceState.getString(TrajectoryEntry.TID);
        }

        setUpMapIfNeeded();
        registerBroadcastReceivers();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        setUpMapIfNeeded();
        restartPositioningService();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();

        if ( mServiceRunningConfirmation.isPositioning() && !mServiceRunningConfirmation.isLogging() ) {
            Intent intent = new Intent(getApplicationContext(), PositioningService_.class);
            stopService(intent);
        }
        unregisterBroadcastReceivers();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentTid != null) {
            outState.putString(TrajectoryEntry.TID, mCurrentTid);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentTid = savedInstanceState.getString(TrajectoryEntry.TID);
    }

    //endregion

    //region initialize

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

        //restore map state
        String tid = mTrajectorySpanDbHandler.getLoggingTid();
        if (tid != null && mServiceRunningConfirmation.isLogging()) {
            List<CheckinEntry> checkinList = mCheckinDbHandler.getCheckinList(tid);
            List<CheckinFreeFormEntry> checkinFreeFormList = mCheckinFreeFormDbHandler.getCheckinFreeFormList(tid);
            List<TrajectoryEntry> positionList = mTrajectoryDbHandler.getTrajectory(tid);
            mMapWorker.addMarkers(positionList);
            mMapWorker.addCheckinMarkers(checkinList);
            mMapWorker.addCheckinMarkers(checkinFreeFormList);
        }

    }
    //endregion

    //region Logging

    /**
     * Called when Start Button Clicked
     * startLogginig and update View
     * @param clicked
     */
    @Click(R.id.logging_start_button)
    protected void onLoggingStartButtonClicked(View clicked) {
        startLoggingWithEncourageGpsOn();
        mMapWorker.initMap(mMap, true);
    }

    /**
     * Called When Stop Button Clicked
     * stop logging and update view
     * @param clicked
     */
    @Click(R.id.logging_stop_button)
    protected void onLoggingStopButtonClicked(View clicked) {

        Intent serviceIntent = new Intent(this.getApplicationContext(), GPSLoggingService_.class);
        stopService(serviceIntent);

        setLoggingStateOnView();

        Toast.makeText(getApplicationContext(), getResources().getString(R.string.stop_logging), Toast.LENGTH_SHORT).show();
    }


    /**
     * Encourage to turn on the GPS and start Logging Service
     */
    private void startLoggingWithEncourageGpsOn() {

        mEncourageGpsOn.encourageGpsOn(new EncourageGpsOn.IEncourageGpsOnAlertDialogCallback() {

            @Override
            public void executeTaskIfProviderIsEnabled() {

                //Restart PositioningService because provider for the locationListener is possibly changed
                restartPositioningService();

                Intent intent = new Intent(getApplicationContext(), BeginRecordingActivity_.class);
                startActivityForResult(intent, BEGINRECORDINGCODE);
            }

        }, false);
    }

    //endregion

    //region InputTrajectoryDetails

    @OnActivityResult(BEGINRECORDINGCODE)
    protected void onBeginingResult(int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }

        final String title = data.getStringExtra(TrajectoryPropertyEntry.TITLE);
        final String memo = data.getStringExtra(TrajectoryPropertyEntry.DESCRIPTION);
        final String companion = data.getStringExtra(CompanionEntry.COMPANION);

        mCurrentTid = mTidGenerator.generateUniqueTid();
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

    //region Checkin

    /**
     * Start PoiConfirmationActivity
     * @param cliecked
     */
    @Click(R.id.check_in_button)
    protected void onCheckedIn(View cliecked) {
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
        if (tid != null) {
            mCurrentTid = tid;
        }

        if (mCurrentTid == null) {
            return;
        }

        float[] position = Position.getPosition(getApplicationContext());
        final float latitude = position[0];
        final float longitude = position[1];

        //Confirm whether result is by free form.
        boolean isFreeform = data.getBooleanExtra("IsFreeForm", false);
        if (!isFreeform) {

            String placeId = data.getStringExtra(CheckinEntry.PLACEID);
            String categoryId = data.getStringExtra(CheckinEntry.CATEGORYID);
            String placeName = data.getStringExtra(CheckinEntry.PLACENAME);
            final CheckinEntry entry = new CheckinEntry(mCurrentTid, placeId, categoryId, TimestampUtil.getTimestamp(), latitude, longitude, placeName);

            new Thread(new Runnable() {

                @Override
                public void run() {
                    mCheckinDbHandler.insert(entry);
                }
            }).start();

            mMapWorker.addCheckinMarker(entry);
            Toast.makeText(this, "Checkin " + placeName, Toast.LENGTH_SHORT).show();
        }

        //By Free Form Result
        else {

            String placeName = data.getStringExtra(CheckinFreeFormEntry.PLACENAME);
            final CheckinFreeFormEntry entry = new CheckinFreeFormEntry(tid, placeName, TimestampUtil.getTimestamp(), latitude, longitude);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mCheckinFreeFormDbHandler.insert(entry);
                }
            }).start();

            mMapWorker.addCheckinMarker(entry);
            Toast.makeText(this, "Checkin " + placeName, Toast.LENGTH_SHORT).show();
        }

    }


    //endregion

    //region Photo

    @Click(R.id.camera_button)
    protected void onCameraButtonClicked() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {

            cameraIntent.addCategory(Intent.CATEGORY_DEFAULT);
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch(IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, CAMERACODE);
            }
        }
    }

    @OnActivityResult(CAMERACODE)
    protected void onTookAPhoto(int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }

        Intent previewIntent = new Intent(getApplicationContext(), PreviewActivity_.class);
        previewIntent.putExtra(PhotoEntry.FILEPATH, mCurrentPhotoPath);
        startActivityForResult(previewIntent, PREVIEWCODE);
    }

    @OnActivityResult(PREVIEWCODE)
    protected void onPreviewResult(int resultCode, final Intent data) {

        if (resultCode != RESULT_OK) {
            deleteTempPhoto();
            return;
        }

        addToGallery();

        final float latitude = data.getFloatExtra(PhotoEntry.LATITUDE, 0.0F);
        final float longitude = data.getFloatExtra(PhotoEntry.LONGITUDE, 0.0F);
        final String timestamp = data.getStringExtra(PhotoEntry.TIMESTAMP);
        final String filePath = data.getStringExtra(PhotoEntry.FILEPATH);
        final String memo = data.getStringExtra(PhotoEntry.MEMO);
        PhotoEntry entry = new PhotoEntry(mCurrentTid, latitude, longitude, timestamp, filePath, memo);

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mCurrentTid == null) {
                    mCurrentTid = mTrajectorySpanDbHandler.getLoggingTid();
                }

                mPhotoDbHandler.insert(mCurrentTid, latitude, longitude, timestamp, filePath, memo);

            }
        }).run();

        //Add Marker to the map
        mMapWorker.addCameraMarker(entry);
    }

    private File createImageFile() throws IOException {

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void addToGallery() {

        if (mCurrentPhotoPath == null) {
            return;
        }

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    private void deleteTempPhoto() {

        if (mCurrentPhotoPath != null) {

            try {
                File picture = new File(mCurrentPhotoPath);
                if (picture.exists()) {
                    picture.delete();
                    Log.i(TAG, "Delete a temp Photo");
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }

        }
    }

    //endregion

    //region MapEvent

    /**
     * This is called when a trajectory entry is stored in the database
     */
    class PositionLoggedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (!intent.getAction().equals(GPSLoggingService.ACTION) || mMap == null || mMapWorker == null) {
                return;
            }

            float latitude = intent.getFloatExtra(Position.LATITUDE, 0.0f);
            float longitude = intent.getFloatExtra(Position.LONGITUDE, 0.0f);
            mMapWorker.addMarker(latitude, longitude);
        }
    }


    /**
     * This is called when onLocationChanged in LocationListener is called.
     * Update the marker's position that represents user's current position.
     */
    class PositionUpdatedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (!intent.getAction().equals(MyLocationListener.ACTION) || mMap == null || mMapWorker == null || mMapWorker.isMyLocationEnabled()) {
                return;
            }

            Log.i(TAG, MyLocationListener.ACTION);

            float latitude = intent.getFloatExtra(Position.LATITUDE, 0.0f);
            float longitude = intent.getFloatExtra(Position.LONGITUDE, 0.0f);
            mMapWorker.updateCurrentPositionMarker(latitude, longitude);
        }
    }

    //endregion

    //region utility

    /**
     * Restart PositioningService
     */
    private void restartPositioningService() {

        Intent intent = new Intent(getApplicationContext(), PositioningService_.class);
        if ( mServiceRunningConfirmation.isPositioning() ) {
            stopService(intent);
        }
        startService(intent);
    }

    /**
     * Change view controls according to Logging State
     */
    private void setLoggingStateOnView() {

        if (mServiceRunningConfirmation.isLogging()) {
            mLoggingStartButton.setVisibility(View.GONE);
            mLoggingStopButton.setVisibility(View.VISIBLE);
            mCheckinButton.setVisibility(View.VISIBLE);
            mCameraButton.setVisibility(View.VISIBLE);
        } else {
            mLoggingStopButton.setVisibility(View.GONE);
            mCheckinButton.setVisibility(View.GONE);
            mCameraButton.setVisibility(View.GONE);
            mLoggingStartButton.setVisibility(View.VISIBLE);
        }
    }

    private void registerBroadcastReceivers() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        mPositionLoggedReceiver = new PositionLoggedBroadcastReceiver();
        mPositionUpdatedReceiver = new PositionUpdatedBroadcastReceiver();
        manager.registerReceiver(mPositionLoggedReceiver, new IntentFilter(GPSLoggingService.ACTION));
        manager.registerReceiver(mPositionUpdatedReceiver, new IntentFilter(MyLocationListener.ACTION));
    }

    private void unregisterBroadcastReceivers() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        if (mPositionUpdatedReceiver != null) {
            manager.unregisterReceiver(mPositionUpdatedReceiver);
        }
        if (mPositionLoggedReceiver != null) {
            manager.unregisterReceiver(mPositionLoggedReceiver);
        }
    }

    //endregion

}
