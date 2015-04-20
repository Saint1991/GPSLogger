package geologger.saints.com.geologger.activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.adapters.PoiListAdapter;
import geologger.saints.com.geologger.foursquare.FourSquareClient;
import geologger.saints.com.geologger.foursquare.models.FourSquarePoi;
import geologger.saints.com.geologger.models.CheckinEntry;
import geologger.saints.com.geologger.models.TrajectoryEntry;
import geologger.saints.com.geologger.sensors.MyLocationListener;
import geologger.saints.com.geologger.services.PositioningService_;
import geologger.saints.com.geologger.uicomponents.PoiListFragment;
import geologger.saints.com.geologger.utils.Position;
import geologger.saints.com.geologger.utils.ProgressDialogUtility;
import geologger.saints.com.geologger.utils.ServiceRunningConfirmation;

@EActivity
public class PoiActivity extends FragmentActivity implements PoiListFragment.OnFragmentInteractionListener{

    private final String TAG = getClass().getSimpleName();

    private List<FourSquarePoi> mFourSquarePoiList;
    private boolean mIsPositionUpdated = false;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mIsPositionUpdated) {
                mProgressUtility.dismissProgress();
                mIsPositionUpdated = true;
            }
        }
    };

    @Bean
    ProgressDialogUtility mProgressUtility;

    @Bean
    FourSquareClient mFourSquareClient;

    @Bean
    ServiceRunningConfirmation mServiceRunningConfirmation;

    @ViewById(R.id.search_text)
    EditText mSearchText;

    @ViewById(R.id.search_submit)
    Button mSearchButton;

    //region lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi);

        long passedFromLastPositionUpdate = Position.getPassedTimeFromLastUpdate(getApplicationContext());
        if (passedFromLastPositionUpdate == -1L || 300000 < passedFromLastPositionUpdate) {
            mProgressUtility.showProgress(getResources().getString(R.string.position_updating));
        } else {
            mIsPositionUpdated = true;
        }

        restartPositioningService();
        registerBroadcastReceiver();
    }

    @Override
    protected void onDestroy() {

        Log.i(TAG, "onDestroy");
        super.onDestroy();

        if ( mServiceRunningConfirmation.isPositioning() && !mServiceRunningConfirmation.isLogging() ) {
            Intent intent = new Intent(getApplicationContext(), PositioningService_.class);
            stopService(intent);
        }

        unregisterBroadcastReceiver();
    }

    //endregion

    //region search

    /**
     * This is called when search button is clicked
     * Start searching POI with showing ProgressBar
     */
    @Click(R.id.search_submit)
    void searchPoiButtonClicked() {

        final String query = mSearchText.getText().toString();
        mProgressUtility.showProgress(getResources().getString(R.string.searching));

        new Thread(new Runnable() {
           public void run() {
               mFourSquareClient.searchPoi(query, new FourSquareClient.IPoiSearchResultCallback() {
                   @Override
                   public void onSearchResult(List<FourSquarePoi> result) {
                       mFourSquarePoiList = result;
                       updateListView();
                       mProgressUtility.dismissProgress();
                   }
                   @Override
                   public void onErrorResult() {
                       mProgressUtility.dismissProgress();
                   }
               });

           }
        }).start();
    }

    /**
     * Updating ListView
     * Set mFourSquarePoiList to the view
     */
    @UiThread
    protected void updateListView() {

        if (mFourSquarePoiList == null || mFourSquarePoiList.size() == 0) {
            return;
        }

        FragmentManager fManager = PoiActivity.this.getFragmentManager();
        PoiListFragment fragment = (PoiListFragment)fManager.findFragmentById(R.id.poi_search_result);
        ListView poiList = fragment.getListView();
        if (poiList == null) {
            return;
        }

        PoiListAdapter adapter = (PoiListAdapter)poiList.getAdapter();
        adapter.clear();
        adapter.addAll(mFourSquarePoiList);
        poiList.setAdapter(adapter);
    }

    //endregion

    //region ItemClicked

    @Override
    public void onFragmentInteraction(ListView parent, View called, int position, long id) {

        final FourSquarePoi entry = (FourSquarePoi)parent.getAdapter().getItem(position);

        //ListView For Dialog
        ListView selectActionView = new ListView(this);
        String[] selection = new String[]{getResources().getString(R.string.go_here), getResources().getString(R.string.detail)};
        selectActionView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, selection));
        selectActionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {

                    case 0:
                        double latitude = entry.getLocation().getLat();
                        double longitude = entry.getLocation().getLng();
                        Intent navigatorIntent = new Intent(getApplicationContext(), NavigationActivity_.class);
                        navigatorIntent.putExtra(TrajectoryEntry.LATITUDE, latitude);
                        navigatorIntent.putExtra(TrajectoryEntry.LONGITUDE, longitude);
                        navigatorIntent.putExtra(CheckinEntry.PLACENAME, entry.getName());
                        navigatorIntent.putExtra("Address", entry.getLocation().getAddress());
                        startActivity(navigatorIntent);
                        break;

                    case 1:
                        String url = FourSquareClient.FOURSQUARE_ROOT + entry.getId();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                        break;
                }
            }
        });

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.action_selection));
        dialog.setView(selectActionView);
        dialog.show();

    }

    //endregion

    //region utility

    private void restartPositioningService() {
        Intent intent = new Intent(getApplicationContext(), PositioningService_.class);
        if ( mServiceRunningConfirmation.isPositioning() ) {
            stopService(intent);
        }
        startService(intent);
    }

    private void registerBroadcastReceiver() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mReceiver, new IntentFilter(MyLocationListener.ACTION));
    }

    private void unregisterBroadcastReceiver() {
        if (mReceiver != null) {
            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
            manager.unregisterReceiver(mReceiver);
        }
    }

    //endregion

}
