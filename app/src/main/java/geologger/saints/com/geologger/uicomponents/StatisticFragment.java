package geologger.saints.com.geologger.uicomponents;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.database.TrajectorySpanSQLite;
import geologger.saints.com.geologger.database.TrajectoryStatisticalInformationSQLite;
import geologger.saints.com.geologger.models.TrajectoryStatisticalEntry;
import geologger.saints.com.geologger.utils.ProgressDialogUtility;
import geologger.saints.com.geologger.utils.TimestampUtil;

@EFragment
public class StatisticFragment extends Fragment {

    public final String TAG = getClass().getSimpleName();

    private String mTid = null;
    private List<Float> mSpeedList = null;
    private float mMaxSpeed = -1.0f;
    private float mAvgSpeed = -1.0f;
    private float mTotalDuration = -1.0F;
    private float mTotalDistance = -1.0f;

    private LineGraphView mGraph;

    @Bean
    TrajectoryStatisticalInformationSQLite mTrajectoryStatisticalInformationDbHandler;

    @Bean
    TrajectorySpanSQLite mTrajectorySpanDbHandler;

    @Bean
    ProgressDialogUtility mProgressUtil;

    @ViewById(R.id.total_time)
    TextView mTotalTimeText;

    @ViewById(R.id.track_length)
    TextView mTrackLengthText;

    @ViewById(R.id.avg_speed)
    TextView mAvgSpeedText;

    @ViewById(R.id.max_speed)
    TextView mMaxSpeedText;

    @ViewById(R.id.graph)
    FrameLayout mGraphPlaceHolder;

    public static StatisticFragment newInstance(String tid) {
        StatisticFragment fragment = new StatisticFragment();
        Bundle arguments = new Bundle();
        arguments.putString(TrajectoryStatisticalEntry.TID, tid);
        fragment.setArguments(arguments);
        return fragment;
    }

    public StatisticFragment() {
        // Required empty public constructor
    }

    //region lifecycle

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        //Get Tid from Arguments
        Bundle args = getArguments();
        if (args == null || args.getString(TrajectoryStatisticalEntry.TID) == null) {
            return;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        //Initialize
        mTid = getArguments().getString(TrajectoryStatisticalEntry.TID);
        initialize();

        return inflater.inflate(R.layout.fragment_statistic, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    //endregion

    //region initialize

    private void initialize() {

        //Show Progress Bar
        mProgressUtil.showProgress(getActivity(), getResources().getString(R.string.initializing));

        //Load Data connects to statistical information
        loadData(mTid);
    }

    private void loadData(final String tid) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                mSpeedList = mTrajectoryStatisticalInformationDbHandler.getSpeedList(tid);
                mMaxSpeed = mTrajectoryStatisticalInformationDbHandler.getMaxSpeed(tid);
                mAvgSpeed = mTrajectoryStatisticalInformationDbHandler.getAverageSpeed(tid);
                mTotalDistance = mTrajectoryStatisticalInformationDbHandler.getTotalDistance(tid);

                String[] timestamps = mTrajectorySpanDbHandler.getStartAndEndTimestamp(tid);
                if (timestamps != null) {
                    mTotalDuration = TimestampUtil.calcPassedSec(timestamps[0], timestamps[1]) * 1000.0F;
                } else {
                    mTotalDuration = mTrajectoryStatisticalInformationDbHandler.getTotalDuration(tid);
                }

                afterLoading();
            }
        }).start();

    }

    @UiThread
    protected void afterLoading() {
        setUpValueTexts();
        setUpGraphIfNeeded();
        mProgressUtil.dismissProgress();
    }

    protected void setUpValueTexts() {

        Log.i(TAG, "duration: " + mTotalDuration + " distance: " + mTotalDistance + " avgsp: " + mAvgSpeed + " maxsp: " + mMaxSpeed);
        final DecimalFormat formatter = new DecimalFormat("#0.#");

        if (mTotalDuration != -1.0F) {
            mTotalTimeText.setText(formatter.format(mTotalDuration / 60000.0F));
        }

        if (mTotalDistance != -1.0F) {
            mTrackLengthText.setText(formatter.format(mTotalDistance / 1000.0F));
        }

        if (mAvgSpeed != -1.0F) {
            mAvgSpeedText.setText(formatter.format(mAvgSpeed * 3600.0f));
        }

        if (mMaxSpeed != -1.0F) {
            mMaxSpeedText.setText(formatter.format(mMaxSpeed * 3600.0f));
        }
    }

    //endregion

    //region graph

    protected void setUpGraphIfNeeded() {

        if (mGraphPlaceHolder != null) {
            mGraph = new LineGraphView(this.getActivity(), getResources().getString(R.string.speed_chart));
            mGraphPlaceHolder.addView(mGraph);
        }

        if (mGraph != null) {
            setUpGraph();
        }
    }

    private void setUpGraph() {

        if (mSpeedList == null || mSpeedList.size() < 1) {
            return;
        }

        int appColor = getResources().getColor(R.color.app_color);
        GraphViewStyle style = mGraph.getGraphViewStyle();
        style.setGridColor(appColor);
        style.setHorizontalLabelsColor(appColor);
        style.setVerticalLabelsColor(appColor);
        style.setNumHorizontalLabels(15);
        style.setNumVerticalLabels(15);

        final int thickness = (int)getResources().getDimension(R.dimen.graph_thickness);
        GraphViewSeries.GraphViewSeriesStyle seriesStyle = new GraphViewSeries.GraphViewSeriesStyle(getResources().getColor(R.color.app_color), thickness);
        GraphView.GraphViewData[] data = createGraphViewData();
        mGraph.addSeries(new GraphViewSeries("speed", seriesStyle, data));

    }
    //endregion

    //region utility
    private GraphView.GraphViewData[] createGraphViewData() {

        ArrayList<GraphView.GraphViewData> data = new ArrayList<>();

        for (int index = 0; index < mSpeedList.size(); index++) {
            data.add(new GraphView.GraphViewData(index, mSpeedList.get(index) * 3600.0f));
        }

        return data.toArray(new GraphView.GraphViewData[0]);
    }


    //endregion

}
