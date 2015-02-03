package geologger.saints.com.geologger.activities;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import java.util.List;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.adapters.PoiListAdapter;
import geologger.saints.com.geologger.foursquare.FourSquareClient;
import geologger.saints.com.geologger.foursquare.models.FourSquarePoi;
import geologger.saints.com.geologger.foursquare.models.FourSquarePoiCategory;
import geologger.saints.com.geologger.models.CheckinEntry;
import geologger.saints.com.geologger.uicomponents.PoiListFragment;
import geologger.saints.com.geologger.utils.TimestampGenerator;

@EActivity
public class PoiConfirmationActivity extends FragmentActivity implements PoiListFragment.OnFragmentInteractionListener {

    private final String TAG = getClass().getSimpleName();
    private Handler mHandler;
    private ProgressDialog mProgress;

    private PoiListAdapter mAdapter;
    private List<FourSquarePoi> mFourSquarePoiList;

    @Bean
    FourSquareClient mFourSquareClient;

    @Override
    public void onFragmentInteraction(ListView parent, View called, int position, long id) {

        FourSquarePoi entry = (FourSquarePoi)parent.getAdapter().getItem(position);

        String placeId = entry.getId();
        FourSquarePoiCategory[] categories = entry.getCategories();
        String timestamp = TimestampGenerator.getTimestamp();
        StringBuilder categoryId = new StringBuilder();
        for (FourSquarePoiCategory category : categories) {
            categoryId.append(category.getId() + ",");
        }

        Intent intent = new Intent();
        intent.putExtra(CheckinEntry.PLACEID, placeId);
        intent.putExtra(CheckinEntry.CATEGORYID, categoryId.substring(0, categoryId.length() - 1));

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_confirmation);


        mHandler = new Handler() {

            @Override
            public void handleMessage(Message message) {

                switch (message.what) {

                    case 0:
                        if (mProgress != null) {
                            mProgress.dismiss();
                        }
                        break;

                    case 1:

                        FragmentManager fManager = PoiConfirmationActivity.this.getFragmentManager();
                        PoiListFragment fragment = (PoiListFragment)fManager.findFragmentById(R.id.poi_candidates);
                        ListView poiList = fragment.getListView();
                        Log.i(TAG, mFourSquarePoiList.toString());
                        mAdapter = (PoiListAdapter)poiList.getAdapter();
                        mAdapter.addAll(mFourSquarePoiList);
                        poiList.setAdapter(mAdapter);
                        break;

                    default:
                        if (mProgress != null) {
                            mProgress.dismiss();
                        }
                        break;
                }
            }
        };

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("searching...");
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.show();

        new Thread(new Runnable() {

            @Override
            public void run() {

                mFourSquarePoiList = mFourSquareClient.searchPoi(null);
                if (mFourSquarePoiList == null || mFourSquarePoiList.size() == 0) {
                    mHandler.sendEmptyMessage(0);
                    Log.i(TAG, "emptyResult");
                    return;
                }

                mHandler.sendEmptyMessage(1);
                mHandler.sendEmptyMessage(0);
            }

        }).start();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_poi_confirmation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
