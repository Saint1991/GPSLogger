package geologger.saints.com.geologger.activities;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.adapters.PoiListAdapter;
import geologger.saints.com.geologger.foursquare.FourSquareClient;
import geologger.saints.com.geologger.foursquare.models.FourSquarePoi;
import geologger.saints.com.geologger.uicomponents.PoiListFragment;

@EActivity
public class PoiActivity extends FragmentActivity implements PoiListFragment.OnFragmentInteractionListener{

    private final String TAG = getClass().getSimpleName();
    private static final String FOURSQUARE_ROOT = "https://ja.foursquare.com/v/";
    private Handler mHandler;
    private ProgressDialog mProgress;

    private PoiListAdapter mAdapter;
    private List<FourSquarePoi> mFourSquarePoiList;

    @Bean
    FourSquareClient mFourSquareClient;

    @ViewById(R.id.search_text)
    EditText mSearchText;

    @ViewById(R.id.search_submit)
    Button mSearchButton;

    @Click(R.id.search_submit)
    void searchPoiButtonClicked() {

        final String query = mSearchText.getText().toString();

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("searching...");
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.show();

        Thread thread = new Thread(new Runnable() {
           public void run() {

               List<FourSquarePoi> fourSquarePois = mFourSquareClient.searchPoi(query);
               Log.i(TAG, query);
               if (fourSquarePois == null || fourSquarePois.size() == 0) {
                   mHandler.sendEmptyMessage(0);
                   return;
               }

               mFourSquarePoiList = fourSquarePois;
               mHandler.sendEmptyMessage(1);

               mHandler.sendEmptyMessage(0);
           }
        });
        thread.start();

    }

    @Override
    public void onFragmentInteraction(ListView parent, View called, int position, long id) {
        FourSquarePoi entry = (FourSquarePoi)parent.getAdapter().getItem(position);
        String url = FOURSQUARE_ROOT + entry.getId();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi);

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

                        FragmentManager fManager = PoiActivity.this.getFragmentManager();
                        PoiListFragment fragment = (PoiListFragment)fManager.findFragmentById(R.id.poi_search_result);
                        ListView poiList = fragment.getListView();
                        mAdapter = (PoiListAdapter)poiList.getAdapter();
                        mAdapter.addAll(mFourSquarePoiList);
                        poiList.setAdapter(mAdapter);
                        break;

                    default:
                        mProgress.dismiss();
                        break;
                }
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_poi, menu);
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
