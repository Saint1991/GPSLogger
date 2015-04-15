package geologger.saints.com.geologger.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.models.PhotoEntry;
import geologger.saints.com.geologger.uicomponents.ScalableImageView;
import geologger.saints.com.geologger.utils.Position;
import geologger.saints.com.geologger.utils.TimestampUtil;

@EActivity
public class PreviewActivity extends Activity {

    public static final String ISVIEWMODE = "IsViewMode";

    @SystemService
    WindowManager mWindowManager;

    @ViewById(R.id.preview_image)
    ScalableImageView mPreviewImage;

    @ViewById(R.id.memo_text)
    EditText mMemoText;

    @ViewById(R.id.save_button)
    Button mSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        initialize();
    }

    private void initialize() {

        Intent data = getIntent();
        boolean isViewMode = data.getBooleanExtra(ISVIEWMODE, false);
        String filePath = data.getStringExtra(PhotoEntry.FILEPATH);
        mPreviewImage.setImageBitmap(filePath);

        if (isViewMode) {

            String memo = data.getStringExtra(PhotoEntry.MEMO);
            mSaveButton.setVisibility(View.GONE);
            mMemoText.setEnabled(false);
            mMemoText.setFocusable(false);
            mMemoText.setFocusableInTouchMode(false);
            mMemoText.setBackgroundResource(R.drawable.only_border);
            mMemoText.setTextColor(getResources().getColor(R.color.white));
            mMemoText.setPadding(10, 10, 10, 10);

            if (memo != null) {
                mMemoText.setText(memo);
            }

        } else {

        }

    }

    @Click(R.id.save_button)
    protected void onSaveButtonClicked() {

        String memo = mMemoText.getText().toString();
        float[] position = Position.getPosition(getApplicationContext());

        Intent retIntent = new Intent();
        retIntent.putExtra(PhotoEntry.FILEPATH, getIntent().getStringExtra(PhotoEntry.FILEPATH));
        retIntent.putExtra(PhotoEntry.MEMO, memo);
        retIntent.putExtra(PhotoEntry.LATITUDE, position[0]);
        retIntent.putExtra(PhotoEntry.LONGITUDE, position[1]);
        retIntent.putExtra(PhotoEntry.TIMESTAMP, TimestampUtil.getTimestamp());
        setResult(RESULT_OK, retIntent);

        Toast.makeText(getApplicationContext(), getResources().getString(R.string.photo_saved), Toast.LENGTH_SHORT).show();
        finish();
    }

}
