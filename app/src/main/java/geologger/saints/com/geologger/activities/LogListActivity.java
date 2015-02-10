package geologger.saints.com.geologger.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import geologger.saints.com.geologger.database.IRemoveByTid;
import geologger.saints.com.geologger.database.SentTrajectorySQLite;
import geologger.saints.com.geologger.database.TrajectorySQLite;
import geologger.saints.com.geologger.database.TrajectorySpanSQLite;
import geologger.saints.com.geologger.models.TrajectorySpanEntry;

@EActivity
public class LogListActivity extends ActionBarActivity {

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_list);

        //ListViewを作成
        //データが空の場合は空である旨を表示するTextViewのみを描画する
        List<TrajectorySpanEntry> spanList = mTrajectorySpanDbHandler.getSpanList();
        if (spanList == null || spanList.size() == 0) {
            TextView messageView = new TextView(this);
            messageView.setText("No Record");
            mLogList.setVisibility(View.GONE);
            mTextNoRecord.setVisibility(View.VISIBLE);
            return;
        }

        LogListAdapter adapter = new LogListAdapter(getApplicationContext(), spanList);
        mLogList.setAdapter(adapter);

        //クリックイベントを登録
        //対応するtidをインテントに渡してLogActivityを起動
        mLogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //DELETEモードの場合はチェックのみ入れて終了し，アクティビティ遷移は行わない
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

        //長押しイベントを登録
        mLogList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                switchMode(MODE.DELETE);
                return true;
            }
        });
    }

    @Click(R.id.log_delete_cancel_button)
    public void cancelLogDeleteMode() {
        switchMode(MODE.NORMAL);
    }

    @Click(R.id.log_delete_button)
    public void deleteSelectedLog() {

        //DELETEモードでない場合は終了
        if (!mode.equals(MODE.DELETE) || mLogList == null) {
            return;
        }

        //チェックされている行を取得する
        SparseBooleanArray checkedPositions = mLogList.getCheckedItemPositions();
        if (checkedPositions == null) {
            return;
        }

        //チェックされている要素をDBとアダプタから削除する
        //後ろから探索するのは，削除に伴ってインデックスがずれるのを防ぐため
        LogListAdapter adapter = (LogListAdapter)mLogList.getAdapter();
        int removedItemCount = 0;
        for (int position = mLogList.getCount() - 1; 0 <= position; position--) {

            //選択されていない行はスキップ
            if (!checkedPositions.get(position)) {
                continue;
            }

            //選択されている行のTidを取得
            TrajectorySpanEntry entry = adapter.getItem(position);
            String tid = entry.getTid();
            Log.i(TAG, tid);

            //DBとアダプタから削除
            mTrajectoryDbHander.removeByTid(tid);
            mCheckinFreeFormDbHandler.removeByTid(tid);
            mCheckinDbHandler.removeByTid(tid);
            mCompanionDbHandler.removeByTid(tid);
            mSentTrajectoryDbHandler.removeByTid(tid);
            if (mTrajectorySpanDbHandler.removeByTid(tid) < 1) {
                Toast.makeText(getApplicationContext(), "Couldn't Remove", Toast.LENGTH_SHORT).show();
                return;
            }

            removedItemCount++;
            adapter.remove(entry);

        }

        //Adapter内のデータの変更をViewに反映
        adapter.notifyDataSetChanged();

        Toast.makeText(getApplicationContext(), removedItemCount + "items removed", Toast.LENGTH_SHORT).show();
        switchMode(MODE.NORMAL);

        //空になった場合はその旨を通知するTextViewを表示
        int visibility = mLogList.getCount() == 0 ? View.VISIBLE : View.GONE;
        mTextNoRecord.setVisibility(visibility);
    }

    //モード切替時の処理
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_list, menu);
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
