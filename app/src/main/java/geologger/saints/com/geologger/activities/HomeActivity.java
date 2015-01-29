package geologger.saints.com.geologger.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.activities.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
@EActivity
public class HomeActivity extends Activity {

    @ViewById(R.id.button_record)
    Button mButtonRecord;

    @ViewById(R.id.button_log)
    Button mButtonLog;

    @ViewById(R.id.button_POI)
    Button mButtonPoi;

    @ViewById(R.id.button_settings)
    Button mButtonSettings;


    @Click(R.id.button_record)
    void recordButtonClicked() {
        Intent intent = new Intent(getApplicationContext(), RecordActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.button_log)
    void logButtonClicked() {
        Intent intent = new Intent(getApplicationContext(), LogListActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.button_POI)
    void poiButtonClicked() {
        Intent intent = new Intent(getApplicationContext(), PoiActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.button_settings)
    void settingsButtonClicked() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity_.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}
