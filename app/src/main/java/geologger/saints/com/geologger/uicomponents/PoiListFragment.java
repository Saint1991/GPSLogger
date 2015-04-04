package geologger.saints.com.geologger.uicomponents;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.androidannotations.annotations.EFragment;


import java.util.ArrayList;

import geologger.saints.com.geologger.R;

import geologger.saints.com.geologger.adapters.PoiListAdapter;
import geologger.saints.com.geologger.foursquare.models.FourSquarePoi;


@EFragment
public class PoiListFragment extends ListFragment {

    private final String TAG = getClass().getSimpleName();

    private OnFragmentInteractionListener mListener;

    public static PoiListFragment newInstance() {
        PoiListFragment fragment = new PoiListFragment();
        return fragment;
    }

    public PoiListFragment() {}

    //region lifecycle

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setListAdapter(new PoiListAdapter(getActivity(), new ArrayList<FourSquarePoi>()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle state) {
        Log.i(TAG, "onCreateView");
        View fragmentView = inflater.inflate(R.layout.fragment_poi_list, null);
        return fragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //endregion

    // region ClickEvent

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

        if (mListener != null) {
            mListener.onFragmentInteraction(l, v, position, id);
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(ListView parent, View called, int position, long id);
    }

    //endregion

}
