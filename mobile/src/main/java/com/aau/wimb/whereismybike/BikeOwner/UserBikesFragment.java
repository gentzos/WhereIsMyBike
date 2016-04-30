package com.aau.wimb.whereismybike.BikeOwner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.aau.wimb.whereismybike.Bike;
import com.aau.wimb.whereismybike.OnFragmentInteractionListener;
import com.aau.wimb.whereismybike.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserBikesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserBikesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserBikesFragment extends Fragment {

    public static final String NEW_BIKE = "newBike";
    public static final String USER_ACCOUNT = "userAccount";
    public static final String USER_BIKES = "bikes";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private UserAccount user;
    private ArrayList bikes = new ArrayList<Bike>();
//    private CallbackManager callbackManager;
//    private String accessToken;

    private OnFragmentInteractionListener mListener;

    public UserBikesFragment() {
        // Required empty public constructor
    }

    // For the list of bikes.
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserBikesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserBikesFragment newInstance(UserAccount user, ArrayList bikes) {
        UserBikesFragment fragment = new UserBikesFragment();
        Bundle args = new Bundle();
        args.putParcelable("USER_ACCOUNT", user);
        args.putParcelableArrayList("USER_BIKES", bikes);
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//            user = getArguments().
            user = getArguments().getParcelable("USER_ACCOUNT");
            bikes = getArguments().getParcelableArrayList("USER_BIKES");
        }

//        if (savedInstanceState != null) {
//            // Restore last state for checked position.
//            bikes = savedInstanceState.getParcelableArrayList("BIKES");
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_bikes, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        mAdapter = new MyAdapter(bikes);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton newBike = (FloatingActionButton) view.findViewById(R.id.new_bike);
        newBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(),UserBikeActivity.class);
                myIntent.putExtra(NEW_BIKE, "true");
                startActivity(myIntent);

//                Bike obj = new Bike("Black Panther ", "OD2F894NCUJEHDJM", "Locked", "None");
//                bikes.add(0, obj);
//                mAdapter.notifyDataSetChanged();

//                Snackbar.make(view, "Replace with your own action " + user.getLastName(), Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        // Set the click listener to edit appointments when a row is clicked.
//        lv.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View v, int pos,
//                                    long id) {
//                subj = aAdpt.getItem(pos).getSubject();
//                descr = aAdpt.getItem(pos).getDescr();
//                loc = aAdpt.getItem(pos).getLocation();
//                year = aAdpt.getItem(pos).getYear();
//                month = aAdpt.getItem(pos).getMonth();
//                day = aAdpt.getItem(pos).getDay();
//                strtHour = aAdpt.getItem(pos).getStartHour();
//                parseInfo = subj + "-" + descr + "-" + loc + "-" + year + "-"
//                        + month + "-" + day + "-" + strtHour + "-" + pos;
//                Intent intent = new Intent(getApplicationContext(),
//                        SetAppointmentActivity.class);
//                intent.putExtra("infoParse", parseInfo);
//                startActivity(intent);
//            }
//        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putParcelableArrayList("BIKES", bikes);
//    }

    private ArrayList<Bike> getDataSet() {
        ArrayList results = new ArrayList<Bike>();
        for (int index = 0; index < 1; index++) {
            Bike obj = new Bike("OD2F894NCUJEHDJM", "csd", "csd", true, true, "None", 55.650299, 12.540938);
            results.add(index, obj);
        }
        return results;
    }
}
