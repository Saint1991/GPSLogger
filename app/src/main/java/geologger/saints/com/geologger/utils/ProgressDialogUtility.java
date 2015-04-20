package geologger.saints.com.geologger.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

/**
 * Created by Mizuno on 2015/04/04.
 */
@EBean
public class ProgressDialogUtility {

    @RootContext
    Activity mActivity;

    private ProgressDialog mProgress;

    public ProgressDialogUtility() {}

    @UiThread
    public void showProgress(Context context, String message) {
        if (context instanceof Activity) {
            mActivity = (Activity)context;
        }

        showProgress(message);
    }

    @UiThread
    public void showProgress(String message) {

        if (mProgress != null && mProgress.isShowing()) {
            mProgress.hide();
            mProgress = null;
        }

        if (mProgress == null && mActivity != null) {
            mProgress = new ProgressDialog(mActivity);
        }

        mProgress.setMessage(message);
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.show();
    }

    @UiThread
    public void dismissProgress() {

        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }

}
