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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aau.wimb.whereismybike.Bike;
import com.aau.wimb.whereismybike.OnFragmentInteractionListener;
import com.aau.wimb.whereismybike.R;

import java.util.ArrayList;

/**
 * A simple { Fragment} subclass.
 * Activities that contain this fragment must implement the
 * { UserBikesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the { UserBikesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserBikesFragment extends Fragment {

    // Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
    private UserAccount user;
    private ArrayList bikes = new ArrayList<Bike>();
//    private CallbackManager callbackManager;
//    private String accessToken;

    private OnFragmentInteractionListener mListener;

    public UserBikesFragment() {
        // Required empty public constructor
    }

    private static String LOG_TAG = "CardViewActivity";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * param1 Parameter 1.
     * param2 Parameter 2.
     * A new instance of fragment UserBikesFragment.
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
            user = getArguments().getParcelable("USER_ACCOUNT");
            bikes = getArguments().getParcelableArrayList("USER_BIKES");
        }
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

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        TextView mNoBikes = (TextView) view.findViewById(R.id.noBikes);
        mNoBikes.setText("You have no bikes registered to your account yet. \n Press the plus \"+\" button to add one.");

        if (bikes.size() == 0) {
            Log.e("BIKES", bikes + " NULL");
            mRecyclerView.setVisibility(View.INVISIBLE);

        } else {
            Log.e("BIKES", bikes + " NOT NULL");
            mNoBikes.setVisibility(View.INVISIBLE);


        }

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        RecyclerView.Adapter mAdapter = new MyAdapter(bikes);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton newBike = (FloatingActionButton) view.findViewById(R.id.new_bike);
        newBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bundle mBundle = new Bundle();
//                mBundle.putString(USER_ID, user.getUniqueId());
                UserMainActivity activity = (UserMainActivity) getActivity();
                activity.registerNewBike();


//                Intent myIntent = new Intent(getActivity(), UserNewBikeActivity.class);
////                startActivity(myIntent);
//                ((UserMainActivity) getActivity()).startActivity(myIntent);
            }
        });
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
}
