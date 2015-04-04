package geologger.saints.com.geologger.uicomponents;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.EFragment;

import geologger.saints.com.geologger.R;

@EFragment
public class StatisticFragment extends Fragment {

    public final String TAG = getClass().getSimpleName();


    public static StatisticFragment newInstance(String param1, String param2) {
        StatisticFragment fragment = new StatisticFragment();
        return fragment;
    }

    public StatisticFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistic, container, false);
    }



}
