package geologger.saints.com.geologger.uicomponents;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.map.MapWorker;
import geologger.saints.com.geologger.models.CheckinEntry;

@EFragment
public class PlayMapFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    public static final String LATLNGLIST = "LatLngList";
    public static final String TIMESTAMPLIST = "TimestampList";
    public static final String CHECKINLIST = "CheckinList";

    private List<LatLng> mLatLngList = null;
    private List<String> mTimestampList = null;
    private List<CheckinEntry> mCheckinEntryList = null;

    private final long PLAYINTERVAL = 700L;
    private Timer mTimer = null;
    private boolean mIsPlaying = false;

    private GoogleMap mMap;

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

    //factory method
    public static PlayMapFragment newInstance(ArrayList<LatLng> latLngList, ArrayList<String> timestampList, ArrayList<CheckinEntry> checkinList) {
        PlayMapFragment fragment = new PlayMapFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(LATLNGLIST, latLngList);
        args.putStringArrayList(TIMESTAMPLIST, timestampList);
        args.putSerializable(CHECKINLIST, checkinList);
        fragment.setArguments(args);
        return fragment;
    }

    public PlayMapFragment() {
        // Required empty public constructor
    }

    //region lifecycle

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mLatLngList = args.getParcelableArrayList(LATLNGLIST);
            mTimestampList = args.getStringArrayList(TIMESTAMPLIST);
            mCheckinEntryList = (ArrayList<CheckinEntry>)args.getSerializable(CHECKINLIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_play_map, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        //Setup MapView
        setUpMapIfNeeded();

    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        pause();
    }

    //endregion

    //region initialize

    private void setUpMapIfNeeded() {

        if (mMap == null) {

            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            SupportMapFragment mapFragment = new SupportMapFragment() {

                @Override
                public void onActivityCreated(Bundle savedInstanceState) {

                    Log.i("mapFragment", "onActivityCreated");
                    super.onActivityCreated(savedInstanceState);

                    mMap = getMap();
                    if (mMap != null) {
                        setUpMap();
                    }

                    //Initialize PlaySlider
                    initSlider();
                }
            };

            transaction.add(R.id.map, mapFragment);
            transaction.commit();

        }
    }

    private void setUpMap() {
        LatLng firstPosition = mLatLngList.get(0);
        mMapWorker.initMap(mMap, firstPosition, BitmapDescriptorFactory.HUE_BLUE, 0.4F);
        mMapWorker.drawLine(mLatLngList);
        mMapWorker.addCheckinMarkers(mCheckinEntryList);
    }

    private void initSlider() {

        //register event listener to the slider
        mPlaySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LatLng position = mLatLngList.get(progress);
                mMapWorker.updateCurrentPositionMarker(position);
                setTimestamp(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

        });

        mPlaySlider.setMax(mTimestampList.size() - 1);
        mPlaySlider.setProgress(0);
    }

    //endregion

    //region PlayerControls

    @Click(R.id.toBeginButton)
    public void toBegin() {
        mPlaySlider.setProgress(0);
    }

    @Click(R.id.pauseButton)
    public void pause() {

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        mIsPlaying = false;
        switchPlayerButton();
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

        mIsPlaying = true;
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
            if (mIsPlaying) {
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

}
