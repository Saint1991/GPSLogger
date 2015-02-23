package geologger.saints.com.geologger.activities;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.View;
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
import geologger.saints.com.geologger.uicomponents.PoiListFragment;

@EActivity(R.layout.activity_poi)
public class PoiActivity extends FragmentActivity implements PoiListFragment.OnFragmentInteractionListener{

    private final String TAG = getClass().getSimpleName();
    private ProgressDialog mProgress;

    private List<FourSquarePoi> mFourSquarePoiList;

    @Bean
    FourSquareClient mFourSquareClient;

    @ViewById(R.id.search_text)
    EditText mSearchText;

    @ViewById(R.id.search_submit)
    Button mSearchButton;


    // This is called when search button is clicked
    // Start searching POI with showing ProgressBar
    @Click(R.id.search_submit)
    void searchPoiButtonClicked() {

        final String query = mSearchText.getText().toString();

        mProgress = new ProgressDialog(this);
        mProgress.setMessage(getResources().getString(R.string.searching));
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.show();

        new Thread(new Runnable() {
           public void run() {
               mFourSquarePoiList = mFourSquareClient.searchPoi(query);
               initListView();
               dismissProgress();
           }
        }).start();

    }

    //Initializing ListView
    //Set mFourSquarePoiList to the view
    @UiThread
    public void initListView() {

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

    //close progress window
    @UiThread
    public void dismissProgress() {
        if (mProgress != null) {
            mProgress.dismiss();
        }
    }

    //Click Event
    //Show Corresponding POI Detail to clicked Entry By Browser
    @Override
    public void onFragmentInteraction(ListView parent, View called, int position, long id) {
        FourSquarePoi entry = (FourSquarePoi)parent.getAdapter().getItem(position);
        String url = FourSquareClient.FOURSQUARE_ROOT + entry.getId();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

}
