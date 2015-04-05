package geologger.saints.com.geologger.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.foursquare.models.FourSquareLocation;
import geologger.saints.com.geologger.foursquare.models.FourSquarePoi;
import geologger.saints.com.geologger.foursquare.models.FourSquarePoiCategory;
import geologger.saints.com.geologger.foursquare.models.FourSquarePoiStates;
import geologger.saints.com.geologger.uicomponents.FourSquarePhotoLoaderImageView_;

/**
 * Created by Mizuno on 2015/01/27.
 */
public class PoiListAdapter extends ArrayAdapter<FourSquarePoi> {

    private List<FourSquarePoi> mFourSquarePois;
    private Context mContext;

    public PoiListAdapter(Context context, List<FourSquarePoi> datas) {
        super(context, R.layout.poi_list_entry, datas);
        mContext = context;
        mFourSquarePois = datas;
    }

    @Override
    public int getCount() {
        return mFourSquarePois.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout poiListEntry = (LinearLayout)inflater.inflate(R.layout.poi_list_entry, null);

        FourSquarePoi fourSquarePoi = mFourSquarePois.get(position);
        ((TextView)poiListEntry.findViewById(R.id.poi_name)).setText(fourSquarePoi.getName());

        StringBuilder categoryStr = new StringBuilder();
        FourSquarePoiCategory[] categories = fourSquarePoi.getCategories();

        for (FourSquarePoiCategory category : categories) {
            categoryStr.append(category.getName() + ", ");
        }

        String setCategoryStr = categoryStr.length() > 2 ? categoryStr.substring(0, categoryStr.length() - 2) : "";
        ((TextView)poiListEntry.findViewById(R.id.poi_category)).setText(setCategoryStr);

        FourSquarePoiStates states = fourSquarePoi.getStats();
        ((TextView)poiListEntry.findViewById(R.id.poi_user_count)).setText(states.getUsersCount() + " visited");

        FourSquareLocation location = fourSquarePoi.getLocation();
        String[] formattedAddress = location.getFormattedAddress();
        StringBuilder address = new StringBuilder();
        for (String add : formattedAddress) {
            address.append(add + " ");
        }
        ((TextView) poiListEntry.findViewById(R.id.poi_address)).setText(address.toString());

        ((TextView) poiListEntry.findViewById(R.id.poi_distance)).setText(location.getDistance() + "m");

        FourSquarePhotoLoaderImageView_ photo =  (FourSquarePhotoLoaderImageView_)poiListEntry.findViewById(R.id.poi_photo);
        photo.setPlaceId(fourSquarePoi.getId());

        if (mContext instanceof Activity) {
            Loader loader = ((Activity)mContext).getLoaderManager().initLoader(position, null, photo);
            loader.forceLoad();
        }

        return poiListEntry;
    }

}

