package geologger.saints.com.geologger.map.infowindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.models.PhotoEntry;
import geologger.saints.com.geologger.uicomponents.ScalableImageView;

/**
 * Created by Mizuno on 2015/04/15.
 */
public class PhotoInfoAdapter implements GoogleMap.InfoWindowAdapter {

    private Context mContext;
    private PhotoEntry mEntry;

    public PhotoInfoAdapter(Context context, PhotoEntry entry) {
        mContext = context;
        mEntry = entry;
    }

    @Override
    public View getInfoWindow(Marker marker) {

        if (mContext == null || mEntry == null) {
            return null;
        }

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View ret = inflater.inflate(R.layout.photo_info, null);

        if (mEntry.getTimestamp() != null && mEntry.getTimestamp().length() > 2) {
            TextView timestampText = (TextView)ret.findViewById(R.id.timestamp);
            timestampText.setText(mEntry.getTimestamp());
        }

        if (mEntry.getMemo() != null) {
            TextView memoText = (TextView)ret.findViewById(R.id.memo);
            memoText.setText(mEntry.getMemo());
        }


        if (mEntry.getFilePath() != null) {
            ScalableImageView photoImage = (ScalableImageView)ret.findViewById(R.id.photo);
            photoImage.setImageBitmap(mEntry.getFilePath(), 180, 150);
        }



        return ret;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
