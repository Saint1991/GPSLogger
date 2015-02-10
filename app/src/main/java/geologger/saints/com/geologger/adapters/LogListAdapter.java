package geologger.saints.com.geologger.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.models.TrajectorySpanEntry;

/**
 * Created by Mizuno on 2015/01/29.
 */
public class LogListAdapter extends ArrayAdapter<TrajectorySpanEntry> {

    protected List<TrajectorySpanEntry> mTrajectorySpanList;
    protected Context mContext;
    protected int entryResource;

    //For Extended class
    protected LogListAdapter(Context context, int resourceId, List<TrajectorySpanEntry> datas) {
        super(context, resourceId, datas);
        this.mContext = context;
        this.mTrajectorySpanList = datas;
        this.entryResource = resourceId;
    }

    public LogListAdapter(Context context, List<TrajectorySpanEntry> data) {
        this(context, R.layout.log_list_entry, data);
    }


    @Override
    public int getCount() {
        return mTrajectorySpanList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TrajectorySpanEntry entry = mTrajectorySpanList.get(position);
        TextView entryText = (TextView)inflater.inflate(this.entryResource, null);
        entryText.setText(entry.getBegin());

        return entryText;
    }


    public LogListAdapter cloneInOtherLayout(int resourceId) {
        return new LogListAdapter(mContext, resourceId, mTrajectorySpanList);
    }

}
