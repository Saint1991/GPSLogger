package geologger.saints.com.geologger.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.widget.TabHost;

import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.database.CheckinFreeFormSQLite;
import geologger.saints.com.geologger.database.CheckinSQLite;
import geologger.saints.com.geologger.database.TrajectorySQLite;
import geologger.saints.com.geologger.models.CheckinEntry;
import geologger.saints.com.geologger.models.TrajectoryEntry;
import geologger.saints.com.geologger.models.TrajectorySpanEntry;
import geologger.saints.com.geologger.models.TrajectoryStatisticalEntry;
import geologger.saints.com.geologger.uicomponents.PlayMapFragment;
import geologger.saints.com.geologger.uicomponents.PlayMapFragment_;
import geologger.saints.com.geologger.uicomponents.StatisticFragment_;
import geologger.saints.com.geologger.utils.ProgressDialogUtility;

@EActivity
public class LogActivity extends FragmentActivity implements FragmentTabHost.OnTabChangeListener {

    private final String TAG = getClass().getSimpleName();
    private final String MAP = "map";
    private final String STATISTICS = "statistics";

    private List<TrajectoryEntry> mTrajectoryEntryList;
    private ArrayList<CheckinEntry> mCheckinEntryList;

    @Bean
    CheckinSQLite mCheckinDbHandler;

    @Bean
    CheckinFreeFormSQLite mCheckinFreeFormDbHandler;

    @Bean
    TrajectorySQLite mTrajectoryDbHandler;

    @Bean
    ProgressDialogUtility mProgressUtility;

    @ViewById(android.R.id.tabhost)
    FragmentTabHost mTabHost;

    //region lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        //Show Progress Dialog
        mProgressUtility.showProgress(getResources().getString(R.string.loading));

        //Load Trajectory Data from Database
        //If missed to load dismiss progress and end
        if (!loadDatas()) {
            mProgressUtility.dismissProgress();
            return;
        }

        initializeTabHost();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (mTrajectoryEntryList == null || mCheckinEntryList == null) {
            loadDatas();
        }

        //Dismiss Progress Dialog
        mProgressUtility.dismissProgress();
    }

    //endregion

    //region initialize

    private boolean loadDatas() {

        Intent intent = getIntent();
        String tid = intent.getStringExtra(TrajectorySpanEntry.TID);

        mTrajectoryEntryList = mTrajectoryDbHandler.getTrajectory(tid);
        mCheckinEntryList = mCheckinDbHandler.getCheckinArrayList(tid);

        return mTrajectoryEntryList.size() > 0;
    }

    private void initializeTabHost() {

        if (mTabHost == null) {
            mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        }
        mTabHost.setup(this, getSupportFragmentManager(), R.id.container);

        Resources resources = getResources();

        TabHost.TabSpec mapSpec = mTabHost.newTabSpec(MAP);
        mapSpec.setIndicator(resources.getString(R.string.map));
        Bundle mapBundle = new Bundle();
        mapBundle.putParcelableArrayList(PlayMapFragment.LATLNGLIST, createLatLngList());
        mapBundle.putStringArrayList(PlayMapFragment.TIMESTAMPLIST, createTimestampList());
        mapBundle.putSerializable(PlayMapFragment.CHECKINLIST, mCheckinEntryList);

        TabHost.TabSpec statisticsSpec = mTabHost.newTabSpec(STATISTICS);
        statisticsSpec.setIndicator(resources.getString(R.string.statistics));
        Intent intent = getIntent();
        Bundle statisticsBundle = new Bundle();
        if (intent != null) {
            statisticsBundle.putString(TrajectoryStatisticalEntry.TID, intent.getStringExtra(TrajectoryStatisticalEntry.TID));
        }

        mTabHost.addTab(mapSpec, PlayMapFragment_.class, mapBundle);
        mTabHost.addTab(statisticsSpec, StatisticFragment_.class, statisticsBundle);

        mTabHost.setOnTabChangedListener(this);
    }

    //endregion

    //region tab

    @Override
    public void onTabChanged(String tabId) {

        switch(tabId) {

            case MAP:

                break;

            case STATISTICS:
                break;

            default:
                break;
        }

    }

    //endregion

    //region utility

    private ArrayList<LatLng> createLatLngList() {

        ArrayList<LatLng> ret = new ArrayList<>();
        for (TrajectoryEntry entry : mTrajectoryEntryList) {
            LatLng position = new LatLng(entry.getLatitude(), entry.getLongitude());
            ret.add(position);
        }

        return ret;
    }

    private ArrayList<String> createTimestampList() {

        ArrayList<String> ret = new ArrayList<>();
        for (TrajectoryEntry entry : mTrajectoryEntryList) {
            ret.add(entry.getTimestamp());
        }

        return ret;
    }

    //endregion
}
