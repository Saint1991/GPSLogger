package geologger.saints.com.geologger.map;

import android.app.Activity;
import android.content.Context;
import android.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.database.CheckinFreeFormSQLite;
import geologger.saints.com.geologger.database.CheckinSQLite;
import geologger.saints.com.geologger.database.IRemoveBy;
import geologger.saints.com.geologger.uicomponents.FourSquarePhotoLoaderImageView_;

/**
 * Created by Mizuno on 2015/02/13.
 */
public class CheckinInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity mActivity;
    private String mPlaceId;
    private boolean mIsCheckInMarkerDeletable;

    public CheckinInfoWindowAdapter(Activity activity, String placeId, boolean isCheckInMarkerDeletable) {
        mActivity = activity;
        mPlaceId = placeId;
        mIsCheckInMarkerDeletable = isCheckInMarkerDeletable;
    }

    @Override
    public View getInfoWindow(final Marker marker) {

        View ret = null;

        if (mActivity == null) {
            return null;
        }

        ret = mActivity.getLayoutInflater().inflate(R.layout.checkin_infowindow, null);

        TextView placeNameView = (TextView)ret.findViewById(R.id.place_name);
        placeNameView.setText(marker.getTitle());

        TextView timestampView = (TextView)ret.findViewById(R.id.timestamp);
        final String timestamp = marker.getSnippet();
        timestampView.setText(timestamp);

        Button removeButton = (Button)ret.findViewById(R.id.removeButton);
        if (!mIsCheckInMarkerDeletable) {
            removeButton.setVisibility(View.GONE);
        } else {

            removeButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Context context = mActivity.getApplicationContext();
                    IRemoveBy db = mPlaceId == null ? new CheckinFreeFormSQLite(context) : new CheckinSQLite(context);
                    db.removeByTimestamp(timestamp);
                    marker.remove();
                    Log.i("InfoWindowAdapter", "remove");
                }

            });

        }

        if (mPlaceId != null) {

            FourSquarePhotoLoaderImageView_ placePhoto = (FourSquarePhotoLoaderImageView_)ret.findViewById(R.id.place_icon);
            placePhoto.setPlaceId(mPlaceId);

            Loader loader = mActivity.getLoaderManager().initLoader(0, null, placePhoto);
            loader.forceLoad();
        }

        return ret;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
