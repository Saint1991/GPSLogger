package geologger.saints.com.geologger.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.models.CompanionEntry;
import geologger.saints.com.geologger.models.TrajectoryPropertyEntry;

@EActivity
public class BeginRecordingActivity extends Activity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin_recording);

        initCompanionList();
    }

    private void initCompanionList() {

        if (mCompanionList == null) {
            mCompanionList = (ListView)findViewById(R.id.companion_list);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, getResources().getStringArray(R.array.companion_candidate_list));
        mCompanionList.setAdapter(adapter);
    }

    @CheckedChange({R.id.no_companion, R.id.not_alone})
    public void onCompanionRadioCheckChanged(CompoundButton Checked) {
        if (mRadioNoCompanion.isChecked()) {
            mCompanionList.setVisibility(View.GONE);
        } else if (mRadioNotAlone.isChecked()) {
            mCompanionList.setVisibility(View.VISIBLE);
        }
    }

    @Click(R.id.ok_button)
    public void onOkButtonClicked(View clicked) {

        Intent retIntent = new Intent();

        String title = mTitleText.getText().toString();
        String memo = mMemoText.getText().toString();

        if (title == null || title.length() < 1) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.title_alert), Toast.LENGTH_SHORT).show();
            return;
        }

        retIntent.putExtra(TrajectoryPropertyEntry.TITLE, title);
        retIntent.putExtra(TrajectoryPropertyEntry.DESCRIPTION, memo);

        if (mRadioNoCompanion.isChecked() || mCompanionList == null) {

            retIntent.putExtra(CompanionEntry.COMPANION, getResources().getString(R.string.no_companion));

        } else {

            StringBuilder companion = new StringBuilder();
            SparseBooleanArray checkedPositions = mCompanionList.getCheckedItemPositions();
            ArrayAdapter<String> adapter = (ArrayAdapter)mCompanionList.getAdapter();
            boolean isValid = false;
            for (int i = 0; i < checkedPositions.size(); i++) {
                if (checkedPositions.get(i)) {
                    companion.append(adapter.getItem(i) + ",");
                    isValid = true;
                }
            }

            if (!isValid) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.companion_alert), Toast.LENGTH_SHORT).show();
                return;
            }

            retIntent.putExtra(CompanionEntry.COMPANION, companion.substring(0, companion.length() - 1));
        }

        setResult(RESULT_OK, retIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_begin_recording, menu);
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
