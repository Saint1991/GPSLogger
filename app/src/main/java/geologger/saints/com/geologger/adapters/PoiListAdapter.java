package geologger.saints.com.geologger.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.foursquare.FourSquareClient;
import geologger.saints.com.geologger.foursquare.models.FourSquareLocation;
import geologger.saints.com.geologger.foursquare.models.FourSquarePoi;
import geologger.saints.com.geologger.foursquare.models.FourSquarePoiCategory;
import geologger.saints.com.geologger.foursquare.models.FourSquarePoiStates;
import geologger.saints.com.geologger.http.AppController;

/**
 * Created by Mizuno on 2015/01/27.
 */
public class PoiListAdapter extends ArrayAdapter<FourSquarePoi> {

    private FourSquareClient mClient;
    private List<FourSquarePoi> mFourSquarePois;
    private Context mContext;

    public PoiListAdapter(Context context, List<FourSquarePoi> datas) {
        super(context, R.layout.poi_list_entry, datas);
        mContext = context;
        mFourSquarePois = datas;
        mClient = new FourSquareClient(context);
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

        final int PHOTOWIDTH = 50;
        final int PHOTOHEIGHT = 50;

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

        final NetworkImageView photoView = (NetworkImageView)poiListEntry.findViewById(R.id.poi_photo);
        mClient.searchPhoto(fourSquarePoi.getId(), PHOTOWIDTH, PHOTOHEIGHT, new FourSquareClient.IPhotoSearchResult() {
            @Override
            public void onSearchResult(List<String> urlList) {

                if (urlList == null || urlList.size() < 1) {
                    return;
                }
                ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                photoView.setImageUrl(urlList.get(0), imageLoader);
            }
        });

        return poiListEntry;
    }

}

