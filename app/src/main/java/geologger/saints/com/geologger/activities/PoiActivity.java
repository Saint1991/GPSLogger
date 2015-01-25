package geologger.saints.com.geologger.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.foursquare.FourSquareClient;
import geologger.saints.com.geologger.foursquare.Poi;

@EActivity
public class PoiActivity extends Activity {

    private final String TAG = getClass().getSimpleName();
    private Handler mHandler;
    private ProgressDialog mProgress;

    @Bean
    FourSquareClient mFourSquareClient;

    @ViewById(R.id.search_text)
    EditText mSearchText;

    @ViewById(R.id.search_submit)
    Button mSearchButton;

    @ViewById(R.id.poi_search_result)
    ListView mResultList;

    @Click(R.id.search_submit)
    void searchPoiButtonClicked() {

        final String query = mSearchText.getText().toString();

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("searching...");
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.show();

        Thread thread = new Thread(new Runnable() {
           public void run() {

               List<Poi> pois = mFourSquareClient.searchPoi(query);
               if (pois == null || pois.size() == 0) {
                   mHandler.sendEmptyMessage(0);
                   return;
               }

               //TODO List Viewに反映する処理


               mHandler.sendEmptyMessage(0);
           }
        });
        thread.start();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {

                switch (message.what) {

                    case 0:
                        mProgress.dismiss();
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
