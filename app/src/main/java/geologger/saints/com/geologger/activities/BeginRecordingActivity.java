package geologger.saints.com.geologger.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.database.TrajectorySpanSQLite;
import geologger.saints.com.geologger.models.CompanionEntry;
import geologger.saints.com.geologger.models.TrajectoryPropertyEntry;
import geologger.saints.com.geologger.utils.CollectionUtil;

@EActivity
public class BeginRecordingActivity extends Activity {

    @Bean
    TrajectorySpanSQLite mTrajectorySpanSQLite;

    @ViewById(R.id.title)
    EditText mTitleText;

    @ViewById(R.id.memo)
    EditText mMemoText;

    @ViewById(R.id.has_companion)
    RadioGroup mCompanionRadio;

    @ViewById(R.id.companion_list)
    ListView mCompanionList;

    @ViewById(R.id.no_companion)
    RadioButton mRadioNoCompanion;

    @ViewById(R.id.not_alone)
    RadioButton mRadioNotAlone;

    @ViewById(R.id.ok_button)
    Button mOkButton;


    //region lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin_recording);

        initCompanionList();
    }

    //endregion

    //region initialize

    /**
     * Initializing the ListView
     */
    private void initCompanionList() {

        if (mCompanionList == null) {
            mCompanionList = (ListView)findViewById(R.id.companion_list);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, getResources().getStringArray(R.array.companion_candidate_list)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                CheckedTextView text = (CheckedTextView)view.findViewById(android.R.id.text1);
                ViewGroup.LayoutParams params = text.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15.0F);
                return view;
            }
        };
        mCompanionList.setAdapter(adapter);
    }

    //endregion

    //region EventHandlers

    /**
     * Change Visibility of ListView along with the check state
     * @param Checked
     */
    @CheckedChange({R.id.no_companion, R.id.not_alone})
    protected void onCompanionRadioCheckChanged(CompoundButton Checked) {

        if (mRadioNoCompanion.isChecked()) {
            mCompanionList.setVisibility(View.GONE);
        } else if (mRadioNotAlone.isChecked()) {
            mCompanionList.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Complete and submit the form
     * @param clicked
     */
    @Click(R.id.ok_button)
    protected void onOkButtonClicked(View clicked) {

        String title = mTitleText.getText().toString();
        final String memo = mMemoText.getText().toString();

        String companion = getCompanionInput();
        if (companion == null) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.companion_alert), Toast.LENGTH_SHORT).show();
            return;
        }

        if (title == null || title.length() < 1) {
            int logCount = mTrajectorySpanSQLite.getLogCount();
            title = "log" + ++logCount;
        }

        Intent retIntent = new Intent();
        retIntent.putExtra(TrajectoryPropertyEntry.TITLE, title);
        retIntent.putExtra(TrajectoryPropertyEntry.DESCRIPTION, memo);
        retIntent.putExtra(CompanionEntry.COMPANION, companion);

        setResult(RESULT_OK, retIntent);
        finish();
    }

    //endregion

    //region utility

    /**
     * return "No Companion" if No Companion is checked
     * Else joint of checked companions with "," as their delimiter will be returned
     * @return
     */
    private String getCompanionInput() {

        String ret = null;

        if (mRadioNoCompanion.isChecked() || mCompanionList == null) {

            ret = getResources().getString(R.string.no_companion);

        } else {

            StringBuilder companionBuilder = new StringBuilder();
            SparseBooleanArray checkedPositions = mCompanionList.getCheckedItemPositions();
            List<Integer> checkedIndexList = CollectionUtil.convertToCheckedIndexList(checkedPositions);
            ArrayAdapter<String> adapter = (ArrayAdapter)mCompanionList.getAdapter();
            for (int index : checkedIndexList) {
                companionBuilder.append(adapter.getItem(index) + ",");
            }

            if (companionBuilder.length() > 1) {
                ret = companionBuilder.substring(0, companionBuilder.length() - 1);
            }

        }

        return ret;
    }

    //endregion

}
