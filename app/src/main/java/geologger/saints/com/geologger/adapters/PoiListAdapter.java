package geologger.saints.com.geologger.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.foursquare.FourSquareLocation;
import geologger.saints.com.geologger.foursquare.FourSquarePoiCategory;
import geologger.saints.com.geologger.foursquare.FourSquarePoiStates;
import geologger.saints.com.geologger.foursquare.Poi;

/**
 * Created by Mizuno on 2015/01/27.
 */
public class PoiListAdapter extends ArrayAdapter<Poi> {

    private List<Poi> mPois;
    private Context mContext;


    public PoiListAdapter(Context context, List<Poi> datas) {
        super(context, R.layout.poi_list_entry, datas);
        mContext = context;
        mPois = datas;
    }

    @Override
    public int getCount() {
        return mPois.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout poiListEntry = (LinearLayout)inflater.inflate(R.layout.poi_list_entry, null);

        Poi poi = mPois.get(position);
        ((TextView)poiListEntry.findViewById(R.id.poi_name)).setText(poi.getName());

        StringBuilder categoryStr = new StringBuilder();
        FourSquarePoiCategory[] categories = poi.getCategories();
        for (FourSquarePoiCategory category : categories) {
            categoryStr.append(category.getName() + ", ");
        }

        String setCategoryStr = categoryStr.length() > 2 ? categoryStr.substring(0, categoryStr.length() - 2) : "";
        ((TextView)poiListEntry.findViewById(R.id.poi_category)).setText(setCategoryStr);

        FourSquarePoiStates states = poi.getStats();
        ((TextView)poiListEntry.findViewById(R.id.poi_user_count)).setText(states.getUsersCount() + " visited");

        FourSquareLocation location = poi.getLocation();
        String[] formattedAddress = location.getFormattedAddress();
        StringBuilder address = new StringBuilder();
        for (String add : formattedAddress) {
            address.append(add + " ");
        }
        ((TextView) poiListEntry.findViewById(R.id.poi_address)).setText(address.toString());

        ((TextView) poiListEntry.findViewById(R.id.poi_distance)).setText(location.getDistance() + "m");

        return poiListEntry;
    }

}

