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

    private List<TrajectorySpanEntry> mTrajectorySpanList;
    private Context mContext;

    public LogListAdapter(Context context, List<TrajectorySpanEntry> datas) {
        super(context, R.layout.log_list_entry, datas);
        this.mContext = context;
        this.mTrajectorySpanList = datas;
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
        LinearLayout logListEntry = (LinearLayout)inflater.inflate(R.layout.log_list_entry, null);

        TrajectorySpanEntry entry = mTrajectorySpanList.get(position);
        ((TextView)logListEntry.findViewById(R.id.begin_timestamp)).setText(entry.getBegin());

        return logListEntry;
    }
}
