package geologger.saints.com.geologger.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.database.CheckinFreeFormSQLite;
import geologger.saints.com.geologger.database.CheckinSQLite;
import geologger.saints.com.geologger.database.TrajectorySQLite;
import geologger.saints.com.geologger.map.MapWorker;
import geologger.saints.com.geologger.models.CheckinEntry;
import geologger.saints.com.geologger.models.CheckinFreeFormEntry;
import geologger.saints.com.geologger.models.TrajectoryEntry;
import geologger.saints.com.geologger.models.TrajectorySpanEntry;
import geologger.saints.com.geologger.uicomponents.FourSquarePhotoLoaderImageView;

@EActivity
public class LogActivity extends FragmentActivity {

    private final String TAG = getClass().getSimpleName();
    private final long PLAYINTERVAL = 700L;


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Timer mTimer;
    private ProgressDialog mProgress;
    private boolean isPlaying = false;

    private List<TrajectoryEntry> mTrajectoryEntryList;
    private List<CheckinEntry> mCheckinEntryList;
    private List<CheckinFreeFormEntry> mCheckinFreeFormEntryList;
    private List<LatLng> mLatLngList;
    private List<String> mTimestampList;

    @Bean
    CheckinSQLite mCheckinDbHandler;

    @Bean
    CheckinFreeFormSQLite mCheckinFreeFormDbHandler;

    @Bean
    TrajectorySQLite mTrajectoryDbHandler;

    @Bean
    MapWorker mMapWorker;


    @ViewById(R.id.playButton)
    Button mPlayButton;

    @ViewById(R.id.pauseButton)
    Button mPauseButton;

    @ViewById(R.id.playSlider)
    SeekBar mPlaySlider;

    @ViewById(R.id.timestampIndicator)
    TextView mTimestampIndicator;


    //region PlayerControls

    @Click(R.id.toBeginButton)
    public void toBegin() {
        mPlaySlider.setProgress(0);
    }

    @Click(R.id.playButton)
    public void play() {

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {

                try {

                    int progress = mPlaySlider.getProgress();
                    progress++;

                    if (mPlaySlider.getMax() < progress) {
                        pause();
                        return;
                    }


                    mPlaySlider.setProgress(progress);

                } catch (Exception e) {
                    e.printStackTrace();
                    pause();
                    toBegin();
                }

            }

        }, 0L, PLAYINTERVAL);

        isPlaying = true;
        switchPlayerButton();
    }

    @Click(R.id.pauseButton)
        public void pause() {

            if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        isPlaying = false;
        switchPlayerButton();
    }

    @UiThread
    void setTimestamp(int progress) {
        String timestamp = mTimestampList.get(progress);
        mTimestampIndicator.setText(timestamp);
    }

    @UiThread
    void switchPlayerButton() {

        try {

            if (isPlaying) {

                mPauseButton.setVisibility(View.VISIBLE);
                mPlayButton.setVisibility(View.GONE);

            } else {

                mPlayButton.setVisibility(View.VISIBLE);
                mPauseButton.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //endregion


    private boolean loadDatas() {

        Intent intent = getIntent();
        String tid = intent.getStringExtra(TrajectorySpanEntry.TID);

        mTrajectoryEntryList = mTrajectoryDbHandler.getTrajectory(tid);
        mCheckinEntryList = mCheckinDbHandler.getCheckinList(tid);
        mCheckinFreeFormEntryList = mCheckinFreeFormDbHandler.getCheckinFreeFormList(tid);

        mLatLngList = new ArrayList<LatLng>();
        mTimestampList = new ArrayList<String>();
        for (TrajectoryEntry entry : mTrajectoryEntryList) {
            LatLng position = new LatLng(entry.getLatitude(), entry.getLongitude());
            mLatLngList.add(position);
            mTimestampList.add(entry.getTimestamp());
        }

        return (mTimestampList.size() == mLatLngList.size() && mLatLngList.size() > 0);

    }

    private void initSlider() {
        mPlaySlider.setMax(mTrajectoryEntryList.size() - 1);
        mPlaySlider.setProgress(0);
    }

    @UiThread
    public void dismissProgress() {
        if (mProgress != null) {
            mProgress.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_log);


        mProgress = new ProgressDialog(this);
        mProgress.setMessage(getResources().getString(R.string.loading));
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.show();

        if (!loadDatas()) {
            dismissProgress();
            return;
        }

        mPlaySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                LatLng position = mLatLngList.get(progress);
                mMapWorker.updateCurrentPositionMarker(position);
                setTimestamp(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pause();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
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


    private void setUpMap() {

        LatLng firstPosition = mLatLngList.get(0);
        mMapWorker.initMap(mMap, firstPosition, BitmapDescriptorFactory.HUE_BLUE, 0.4F);
        mMapWorker.drawLine(mLatLngList);
        mMapWorker.addCheckinMarkers(mCheckinEntryList);

        initSlider();
        dismissProgress();
    }
}
