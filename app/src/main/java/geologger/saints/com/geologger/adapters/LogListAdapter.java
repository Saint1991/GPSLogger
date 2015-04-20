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
import geologger.saints.com.geologger.models.LogListEntry;

/**
 * Created by Mizuno on 2015/01/29.
 */
public class LogListAdapter extends ArrayAdapter<LogListEntry> {

    protected List<LogListEntry> mLogList;
    protected Context mContext;
    protected int mEntryResource;

    //For Extended class
    protected LogListAdapter(Context context, int resourceId, List<LogListEntry> datas) {
        super(context, resourceId, datas);
        this.mContext = context;
        this.mLogList = datas;
        this.mEntryResource = resourceId;
    }

    public LogListAdapter(Context context, List<LogListEntry> data) {
        this(context, R.layout.log_list_entry, data);
    }


    @Override
    public int getCount() {
        return mLogList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LogListEntry entry = mLogList.get(position);
        LinearLayout rootView = (LinearLayout)inflater.inflate(this.mEntryResource, null);

        TextView textView = null;

        if (entry.getBeginTimestamp() != null) {
            textView = (TextView)rootView.findViewById(R.id.begin_timestamp);
            textView.setText(entry.getBeginTimestamp());
        }

        if (entry.getEndTimestamp() != null) {
            textView = (TextView)rootView.findViewById(R.id.end_timestamp);
            textView.setText(entry.getEndTimestamp());
        }

        if (entry.getTitle() != null) {
            textView = (TextView)rootView.findViewById(R.id.title);
            textView.setText(entry.getTitle());
        }

        if (entry.getCompanion() != null) {
            textView = (TextView)rootView.findViewById(R.id.companion);
            textView.setText(entry.getCompanion());
        }

        if (entry.getDescription() != null) {
            textView = (TextView)rootView.findViewById(R.id.description);
            textView.setText(entry.getDescription());
        }

        return rootView;
    }


    public LogListAdapter cloneInOtherLayout(int resourceId) {
        return new LogListAdapter(mContext, resourceId, mLogList);
    }

}
