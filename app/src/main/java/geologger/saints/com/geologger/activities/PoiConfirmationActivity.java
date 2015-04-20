package geologger.saints.com.geologger.activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
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
import geologger.saints.com.geologger.utils.ProgressDialogUtility;

@EActivity
public class PoiConfirmationActivity extends FragmentActivity implements PoiListFragment.OnFragmentInteractionListener {

    private final String TAG = getClass().getSimpleName();

    private List<FourSquarePoi> mFourSquarePoiList;

    @Bean
    FourSquareClient mFourSquareClient;

    @Bean
    ProgressDialogUtility mProgressUtility;


    //region lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_confirmation);

        mProgressUtility.showProgress(getResources().getString(R.string.searching));

        new Thread(new Runnable() {

            @Override
            public void run() {
                mFourSquareClient.searchPoi(null, new FourSquareClient.IPoiSearchResultCallback() {
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

    //endregion

    //region initialize

    @UiThread
    protected void updateListView() {

        if (mFourSquarePoiList == null || mFourSquarePoiList.size() == 0) {
            return;
        }

        FragmentManager fManager = PoiConfirmationActivity.this.getFragmentManager();
        PoiListFragment fragment = (PoiListFragment)fManager.findFragmentById(R.id.poi_candidates);
        if (fragment == null) {
            return;
        }

        ListView poiList = fragment.getListView();
        if (poiList == null) {
            return;
        }

        PoiListAdapter adapter = (PoiListAdapter)poiList.getAdapter();
        adapter.addAll(mFourSquarePoiList);
        poiList.setAdapter(adapter);
    }

    //endregion

    //region CheckInItemSelected

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
        String checkin = getResources().getString(R.string.check_in);
        confirmationDialog.setTitle(checkin);
        confirmationDialog.setMessage(checkin + " " + placeName + "?");

        //Set Information and finish Activity
        confirmationDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent();
                intent.putExtra(CheckinEntry.PLACEID, placeId);
                intent.putExtra(CheckinEntry.PLACENAME, placeName);
                if (categoryId != null && categoryId.length() > 1) {
                    intent.putExtra(CheckinEntry.CATEGORYID, categoryId.substring(0, categoryId.length() - 1));
                }

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

    //region FreeFormSelected

    @Click(R.id.button_to_free_form)
    protected void showFreeFormDialog() {

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
        dialog.setTitle(getResources().getString(R.string.free_form));
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

}
