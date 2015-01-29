package geologger.saints.com.geologger.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.adapters.LogListAdapter;
import geologger.saints.com.geologger.database.SQLiteModelDefinition;
import geologger.saints.com.geologger.database.TrajectorySpanSQLite;
import geologger.saints.com.geologger.models.TrajectorySpanEntry;

@EActivity
public class LogListActivity extends ActionBarActivity {

    private final String TAG = getClass().getSimpleName();

    @Bean
    TrajectorySpanSQLite mDbHandler;

    @ViewById(R.id.log_list)
    ListView mLogList;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_list);

        //ListViewを作成
        //データが空の場合はスキップする
        List<TrajectorySpanEntry> spanList = mDbHandler.getSpanList();
        LogListAdapter adapter = new LogListAdapter(getApplicationContext(), spanList);
        mLogList.setAdapter(adapter);

        //クリックイベントを登録
        //対応するtidをインテントに渡してLogActivityを起動
        mLogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
                mLogList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                return true;
            }
        });
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
