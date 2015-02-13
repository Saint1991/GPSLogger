package geologger.saints.com.geologger.activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import java.util.List;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.adapters.PoiListAdapter;
import geologger.saints.com.geologger.foursquare.FourSquareClient;
import geologger.saints.com.geologger.foursquare.models.FourSquarePoi;
import geologger.saints.com.geologger.foursquare.models.FourSquarePoiCategory;
import geologger.saints.com.geologger.models.CheckinEntry;
import geologger.saints.com.geologger.models.CheckinFreeFormEntry;
import geologger.saints.com.geologger.uicomponents.PoiListFragment;

@EActivity
public class PoiConfirmationActivity extends FragmentActivity implements PoiListFragment.OnFragmentInteractionListener {

    private final String TAG = getClass().getSimpleName();
    private ProgressDialog mProgress;

    private List<FourSquarePoi> mFourSquarePoiList;

    @Bean
    FourSquareClient mFourSquareClient;


    //region SelectFromList
    @Override
    public void onFragmentInteraction(ListView parent, View called, int position, long id) {

        PoiListAdapter adapter = (PoiListAdapter)parent.getAdapter();
        if (adapter == null || adapter.getCount() < 1) {
            return;
        }

        FourSquarePoi entry = adapter.getItem(position);
        final String placeId = entry.getId();
        final String placeName = entry.getName();
        FourSquarePoiCategory[] categories = entry.getCategories();

        //Make Category String
        final StringBuilder categoryId = new StringBuilder();
        for (FourSquarePoiCategory category : categories) {
            categoryId.append(category.getId() + ",");
        }

        //Make Confirmation Dialog
        AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(this);
        String checkin = getResources().getString(R.string.checkin);
        confirmationDialog.setTitle(checkin);
        confirmationDialog.setMessage(checkin + " " + placeName + "?");

        //Set Information and finish Activity
        confirmationDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent();
                intent.putExtra(CheckinEntry.PLACEID, placeId);
                intent.putExtra(CheckinEntry.CATEGORYID, categoryId.substring(0, categoryId.length() - 1));
                intent.putExtra(CheckinEntry.PLACENAME, placeName);

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        confirmationDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        confirmationDialog.show();
    }

    //endregion


    //region FreeForm

    //Show Dialog for Free Form input
    @Click(R.id.buttonToFreeForm)
    public void showFreeFormDialog() {

        final EditText inputForm = new EditText(this);
        inputForm.requestFocus();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent resultIntent = new Intent();

                SpannableStringBuilder stringBuilder = (SpannableStringBuilder) inputForm.getText();
                resultIntent.putExtra(CheckinFreeFormEntry.PLACENAME, stringBuilder.toString());
                resultIntent.putExtra("IsFreeForm", true);

                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setTitle(getResources().getString(R.string.freeform));


        dialog.setView(inputForm, 5, 30, 5, 30);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(inputForm, 0);
            }
        });

        dialog.show();
    }

    //endregion



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_confirmation);


        mProgress = new ProgressDialog(this);
        mProgress.setMessage(getResources().getString(R.string.searching));
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.show();

        new Thread(new Runnable() {

            @Override
            public void run() {
                mFourSquarePoiList = mFourSquareClient.searchPoi(null);
                initListView();
                dismissProgress();
            }

        }).start();

    }

    @UiThread
    public void initListView() {

        if (mFourSquarePoiList == null || mFourSquarePoiList.size() == 0) {
            return;
        }

        FragmentManager fManager = PoiConfirmationActivity.this.getFragmentManager();
        PoiListFragment fragment = (PoiListFragment)fManager.findFragmentById(R.id.poi_candidates);
        ListView poiList = fragment.getListView();
        PoiListAdapter adapter = (PoiListAdapter)poiList.getAdapter();
        adapter.addAll(mFourSquarePoiList);
        poiList.setAdapter(adapter);

    }

    @UiThread
    public void dismissProgress() {
        if (mProgress != null) {
            mProgress.dismiss();
        }
    }


}
