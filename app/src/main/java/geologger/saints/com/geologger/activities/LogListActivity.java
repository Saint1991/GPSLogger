package geologger.saints.com.geologger.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.adapters.LogListAdapter;
import geologger.saints.com.geologger.database.CheckinFreeFormSQLite;
import geologger.saints.com.geologger.database.CheckinSQLite;
import geologger.saints.com.geologger.database.CompanionSQLite;
import geologger.saints.com.geologger.database.SentTrajectorySQLite;
import geologger.saints.com.geologger.database.TrajectorySQLite;
import geologger.saints.com.geologger.database.TrajectorySpanSQLite;
import geologger.saints.com.geologger.models.TrajectorySpanEntry;

import static android.view.Window.FEATURE_NO_TITLE;

@EActivity
public class LogListActivity extends Activity {

    private final String TAG = getClass().getSimpleName();

    private enum MODE {NORMAL, DELETE};
    private MODE mode = MODE.NORMAL;

    @Bean
    TrajectorySpanSQLite mTrajectorySpanDbHandler;

    @Bean
    TrajectorySQLite mTrajectoryDbHander;

    @Bean
    CheckinFreeFormSQLite mCheckinFreeFormDbHandler;

    @Bean
    CheckinSQLite mCheckinDbHandler;

    @Bean
    CompanionSQLite mCompanionDbHandler;

    @Bean
    SentTrajectorySQLite mSentTrajectoryDbHandler;


    @ViewById(R.id.log_list)
    ListView mLogList;

    @ViewById(R.id.text_no_record)
    TextView mTextNoRecord;

    @ViewById(R.id.log_delete_button)
    Button mLogDeleteButton;

    @ViewById(R.id.log_delete_cancel_button)
    Button mLogDeleteCancelButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");
        requestWindowFeature(FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_list);

        initLogList();
    }

    //region Init

    // Initializing LogList
    private void initLogList() {

        List<TrajectorySpanEntry> spanList = mTrajectorySpanDbHandler.getSpanList();

        // If there is no entry, show the message "No Record"
        if (spanList == null || spanList.size() == 0) {
            TextView messageView = new TextView(this);
            messageView.setText("No Record");
            mLogList.setVisibility(View.GONE);
            mTextNoRecord.setVisibility(View.VISIBLE);
            return;
        }

        //Registering Items to ListView
        LogListAdapter adapter = new LogListAdapter(getApplicationContext(), spanList);
        mLogList.setAdapter(adapter);

        // Click Event
        // In the Delete mode, only check the entry
        // In the Normal mode, start Log Activity with corresponding TID to clicked entry
        mLogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mode.equals(MODE.DELETE)) {
                    return;
                }

                TrajectorySpanEntry entry = (TrajectorySpanEntry)mLogList.getAdapter().getItem(position);
                String tid = entry.getTid();

                Intent intent = new Intent(getApplicationContext(), LogActivity_.class);
                intent.putExtra(TrajectorySpanEntry.TID, tid);
                startActivity(intent);
            }
        });

        // Long Click Event
        // Switch to Delete mode
        mLogList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                switchMode(MODE.DELETE);
                return true;
            }
        });
    }

    //endregion

    //region DELETE

    //This is called when the DELETE Button is Clicked
    //Remove Selected entries from view and DB
    @Click(R.id.log_delete_button)
    public void deleteSelectedLog() {

        //If not DELETE mode, finish
        if (!mode.equals(MODE.DELETE) || mLogList == null) {
            return;
        }

        //Getting Checked entries
        SparseBooleanArray checkedPositions = mLogList.getCheckedItemPositions();
        if (checkedPositions == null) {
            return;
        }

        String loggingTid = mTrajectorySpanDbHandler.getLoggingTid();

        //Remove Item From End.
        LogListAdapter adapter = (LogListAdapter)mLogList.getAdapter();
        int removedItemCount = 0;
        for (int position = mLogList.getCount() - 1; 0 <= position; position--) {

            if (!checkedPositions.get(position)) {
                continue;
            }

            //Getting corresponding TID to the entry
            TrajectorySpanEntry entry = adapter.getItem(position);
            String tid = entry.getTid();

            if (loggingTid != null && loggingTid.equals(tid)) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.could_not_remove_entry_message) , Toast.LENGTH_SHORT).show();
                continue;
            }

            //Removing from DB and View
            mTrajectoryDbHander.removeByTid(tid);
            mCheckinFreeFormDbHandler.removeByTid(tid);
            mCheckinDbHandler.removeByTid(tid);
            mCompanionDbHandler.removeByTid(tid);
            mSentTrajectoryDbHandler.removeByTid(tid);
            if (mTrajectorySpanDbHandler.removeByTid(tid) < 1) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.could_not_remove), Toast.LENGTH_SHORT).show();
                return;
            }

            removedItemCount++;
            adapter.remove(entry);
        }

        adapter.notifyDataSetChanged();

        Toast.makeText(getApplicationContext(), removedItemCount + getResources().getString(R.string.items_removed), Toast.LENGTH_SHORT).show();
        switchMode(MODE.NORMAL);

        //If List becomes empty, show message
        int visibility = mLogList.getCount() == 0 ? View.VISIBLE : View.GONE;
        mTextNoRecord.setVisibility(visibility);
    }


    @Click(R.id.log_delete_cancel_button)
    public void cancelLogDeleteMode() {
        switchMode(MODE.NORMAL);
    }

    //endregion

    //region ModeChange

    //Switch Delete Mode and Normal Mode
    private void switchMode(MODE to) {

        if (mLogList == null) {
            return;
        }

        switch(to) {

            case NORMAL:

                LogListAdapter adapter = ((LogListAdapter)mLogList.getAdapter()).cloneInOtherLayout(R.layout.log_list_entry);
                mLogList.setAdapter(adapter);
                mLogList.setChoiceMode(ListView.CHOICE_MODE_NONE);

                mLogDeleteButton.setVisibility(View.GONE);
                mLogDeleteCancelButton.setVisibility(View.GONE);

                this.mode = MODE.NORMAL;
                break;

            case DELETE:

                LogListAdapter adapterWithCheckBox = ((LogListAdapter)mLogList.getAdapter()).cloneInOtherLayout(R.layout.log_list_entry_with_checkbox);
                mLogList.setAdapter(adapterWithCheckBox);
                mLogList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                mLogDeleteButton.setVisibility(View.VISIBLE);
                mLogDeleteCancelButton.setVisibility(View.VISIBLE);

                this.mode = MODE.DELETE;
                break;
        }
    }


    //endregion

}
