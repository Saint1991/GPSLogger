package geologger.saints.com.geologger.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.adapters.LogListAdapter;
import geologger.saints.com.geologger.database.CheckinFreeFormSQLite;
import geologger.saints.com.geologger.database.CheckinSQLite;
import geologger.saints.com.geologger.database.CompanionSQLite;
import geologger.saints.com.geologger.database.SentTrajectorySQLite;
import geologger.saints.com.geologger.database.TrajectoryPropertySQLite;
import geologger.saints.com.geologger.database.TrajectorySQLite;
import geologger.saints.com.geologger.database.TrajectorySpanSQLite;
import geologger.saints.com.geologger.models.CompanionEntry;
import geologger.saints.com.geologger.models.LogListEntry;
import geologger.saints.com.geologger.models.TrajectoryPropertyEntry;
import geologger.saints.com.geologger.models.TrajectorySpanEntry;
import geologger.saints.com.geologger.utils.ProgressDialogUtility;
import geologger.saints.com.geologger.utils.SendDataTask;


@EActivity
public class LogListActivity extends Activity {

    private final String TAG = getClass().getSimpleName();

    private enum MODE {NORMAL, SELECTION};
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

    @Bean
    TrajectoryPropertySQLite mTrajectoryPropertyDbHandler;

    @Bean
    SendDataTask mSendDataTask;

    @Bean
    ProgressDialogUtility mProgressUtility;


    @ViewById(R.id.log_list)
    ListView mLogList;

    @ViewById(R.id.text_no_record)
    TextView mTextNoRecord;

    @ViewById(R.id.log_delete_button)
    Button mLogDeleteButton;

    @ViewById(R.id.data_resend_button)
    Button mSendDataButton;

    @ViewById(R.id.log_delete_cancel_button)
    Button mLogDeleteCancelButton;


    //region lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_list);

        mProgressUtility.showProgress(getResources().getString(R.string.initializing));
        new Thread(new Runnable() {
            @Override
            public void run() {
                initLogList();
                mProgressUtility.dismissProgress();
            }
        }).run();
    }

    //endregion

    //region Initialize

    /**
     * Initialize ListView
     */
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
        List<LogListEntry> logList = new ArrayList<>();
        for (TrajectorySpanEntry spanEntry : spanList) {
            String tid = spanEntry.getTid();

            CompanionEntry companionEntry = mCompanionDbHandler.getCompanion(tid);
            TrajectoryPropertyEntry trajectoryPropertyEntry = mTrajectoryPropertyDbHandler.getEntry(tid);

            String companion = companionEntry == null ? null : companionEntry.getCompanion();
            String title = trajectoryPropertyEntry == null ? null : trajectoryPropertyEntry.getTitle();
            String description = trajectoryPropertyEntry == null ? null : trajectoryPropertyEntry.getDescription();

            logList.add(new LogListEntry(tid, title, spanEntry.getBegin(), spanEntry.getEnd(), companion, description));
        }
        LogListAdapter adapter = new LogListAdapter(getApplicationContext(), logList);
        mLogList.setAdapter(adapter);

        // Click Event
        // In the Delete mode, only check the entry
        // In the Normal mode, start Log Activity with corresponding TID to clicked entry
        mLogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mode.equals(MODE.SELECTION)) {
                    CheckBox checkBox = (CheckBox)view.findViewById(R.id.selected);
                    checkBox.setChecked(mLogList.getCheckedItemPositions().get(position));
                    return;
                }

                LogListEntry entry = (LogListEntry)mLogList.getAdapter().getItem(position);
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
                switchMode(MODE.SELECTION);
                return true;
            }
        });

    }

    //endregion

    //region ModeChange

    /**
     * Switch Normal to SELECT and vice versa.
     * @param to
     */
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
                mSendDataButton.setVisibility(View.GONE);

                this.mode = MODE.NORMAL;
                break;

            case SELECTION:

                LogListAdapter adapterWithCheckBox = ((LogListAdapter)mLogList.getAdapter()).cloneInOtherLayout(R.layout.log_list_entry_with_checkbox);
                mLogList.setAdapter(adapterWithCheckBox);
                mLogList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                mLogDeleteButton.setVisibility(View.VISIBLE);
                mLogDeleteCancelButton.setVisibility(View.VISIBLE);
                mSendDataButton.setVisibility(View.VISIBLE);

                this.mode = MODE.SELECTION;
                break;
        }
    }

    //endregion

    //region SelectionMode

    //This is called when the SELECTION Button is Clicked
    //Remove Selected entries from view and DB
    @Click(R.id.log_delete_button)
    protected void deleteSelectedLog() {

        //If not SELECTION mode, finish
        if (!mode.equals(MODE.SELECTION) || mLogList == null) {
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
            LogListEntry entry = adapter.getItem(position);
            String tid = entry.getTid();

            if (loggingTid != null && loggingTid.equals(tid)) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.could_not_remove_entry_message) , Toast.LENGTH_SHORT).show();
                continue;
            }

            //Removing from DB and View
            if (!removeDatasFromDB(tid)) {
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

    @Click(R.id.data_resend_button)
    protected void sendData() {

        //If not SELECTION mode, finish
        if (!mode.equals(MODE.SELECTION) || mLogList == null) {
            return;
        }

        //Getting Checked entries
        SparseBooleanArray checkedPositions = mLogList.getCheckedItemPositions();
        if (checkedPositions == null) {
            return;
        }

        //Remove Item From End.
        LogListAdapter adapter = (LogListAdapter)mLogList.getAdapter();
        final List<String> tidList = new ArrayList<>();
        for (int position = mLogList.getCount() - 1; 0 <= position; position--) {

            if (!checkedPositions.get(position)) {
                continue;
            }

            //Getting corresponding TID to the entry
            LogListEntry entry = adapter.getItem(position);
            String tid = entry.getTid();

            tidList.add(tid);
        }


        //Sending Data in the other thread
        mSendDataTask.setTidList(tidList);
        new Thread(mSendDataTask).start();

        Toast.makeText(getApplicationContext(), getResources().getString(R.string.data_sent), Toast.LENGTH_SHORT).show();
        switchMode(MODE.NORMAL);

        //If List becomes empty, show message
        int visibility = mLogList.getCount() == 0 ? View.VISIBLE : View.GONE;
        mTextNoRecord.setVisibility(visibility);
    }

    @Click(R.id.log_delete_cancel_button)
    protected void cancelLogDeleteMode() {
        switchMode(MODE.NORMAL);
    }

    //endregion

    //region utility

    private boolean removeDatasFromDB(String tid) {

        mTrajectoryDbHander.removeByTid(tid);
        mCheckinFreeFormDbHandler.removeByTid(tid);
        mCheckinDbHandler.removeByTid(tid);
        mCompanionDbHandler.removeByTid(tid);
        mSentTrajectoryDbHandler.removeByTid(tid);
        mTrajectoryPropertyDbHandler.removeByTid(tid);

        return mTrajectorySpanDbHandler.removeByTid(tid) > 0;
    }

    //endregion
}
