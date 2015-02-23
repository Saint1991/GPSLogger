package geologger.saints.com.geologger.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.LocationManager;
import android.provider.Settings;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;

import geologger.saints.com.geologger.R;

/**
 * Created by Mizuno on 2015/02/12.
 */
@EBean
public class EncourageGpsOn {

    @RootContext
    Activity mActivity;

    @SystemService
    LocationManager mLocationManager;

    /**
     * Encourage to turn on the GPS by dialog
     * @param callback The task executed after selecting dialog
     * @param mustGpsOn Is GPS forced to be ON to execute callback
     */
    @UiThread
    public void encourageGpsOn(IEncourageGpsOnAlertDialogCallback callback, boolean mustGpsOn) {

        boolean isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGpsEnabled) {
            callback.executeTaskIfProviderIsEnabled();
            return;
        }

        AlertDialog dialog = buildEncouragingDialog(callback, isNetworkEnabled, mustGpsOn);
        dialog.show();

    }

    // Build dialog
    private AlertDialog buildEncouragingDialog(final IEncourageGpsOnAlertDialogCallback callback, boolean isNetworkEnabled, final boolean mustGpsOn) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);

        Resources resources = mActivity.getResources();
        dialog.setTitle(resources.getString(R.string.gps_confirmation));
        dialog.setMessage(resources.getString(R.string.gps_confirmation_message));

        String positiveButtonMessage = isNetworkEnabled ? resources.getString(R.string.yes) : resources.getString(R.string.ok);
        dialog.setPositiveButton(positiveButtonMessage, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mActivity.startActivity(intent);
            }
        });

        if (isNetworkEnabled) {
            dialog.setNegativeButton(resources.getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    if (!mustGpsOn) {
                        callback.executeTaskIfProviderIsEnabled();
                    }
                }
            });
        }

        return dialog.create();
    }
}
