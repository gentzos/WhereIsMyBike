package com.aau.wimb.whereismybike.BikeOwner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aau.wimb.whereismybike.Bike;
import com.aau.wimb.whereismybike.MapsActivity;
import com.aau.wimb.whereismybike.R;

import java.util.ArrayList;

/**
 * Created by Konstantinos on 026 26 4 2016.
 */
public class MyAdapter extends RecyclerView
        .Adapter<MyAdapter
        .DataObjectHolder> {

    public static final String NEW_BIKE = "newBike";
    public static final String TRACK_BIKE = "trackBike";
    private static String LOG_TAG = "MyAdapter";

    private ArrayList<Bike> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        private TextView vin;
        private TextView brand;
        private TextView color;
        private TextView lock;
        private TextView status;
        private TextView access;
        private double latitude;
        private double longitude;

        public DataObjectHolder(View itemView) {
            super(itemView);
            vin = (TextView) itemView.findViewById(R.id.bikeVinChange);
            brand = (TextView) itemView.findViewById(R.id.bikeBrandChange);
            color = (TextView) itemView.findViewById(R.id.bikeColorChange);
            lock = (TextView) itemView.findViewById(R.id.bikeLockChange);
            status = (TextView) itemView.findViewById(R.id.bikeStatusChange);
            access = (TextView) itemView.findViewById(R.id.bikeAccessChange);
            Log.i(LOG_TAG, "Adding Listener");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(v.getContext(),UserBikeActivity.class);
                    myIntent.putExtra(NEW_BIKE, vin.getText() + "-" + brand.getText() + "-" + color.getText()
                            + "-" + lock.getText() + "-" + status.getText() + "-" + access.getText() + "-" + latitude + "-" + longitude);
                    v.getContext().startActivity(myIntent);
                }
            });
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyAdapter(ArrayList<Bike> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row, parent, false);

        final DataObjectHolder dataObjectHolder = new DataObjectHolder(view);

        Button mTrackButton = (Button) view.findViewById(R.id.track_button);
        mTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(),MapsActivity.class);
                myIntent.putExtra(TRACK_BIKE, dataObjectHolder.vin.getText() + "-" + dataObjectHolder.latitude + "-" + dataObjectHolder.longitude);
                v.getContext().startActivity(myIntent);
//                Snackbar.make(v, "Replace with your own action " + dataObjectHolder.latitude + " " + dataObjectHolder.longitude, Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.vin.setText(mDataset.get(position).getVin());
        holder.brand.setText(mDataset.get(position).getBrand());
        holder.color.setText(mDataset.get(position).getColor());

        if (mDataset.get(position).isLock() == true){
            holder.lock.setText("Locked");
            holder.lock.setTextColor(holder.itemView.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.lock.setText("Unlocked");
            holder.lock.setTextColor(holder.itemView.getResources().getColor(R.color.colorAccent));
        }

        if (mDataset.get(position).isStatus() == true){
            holder.status.setText("Stolen");
            holder.status.setTextColor(holder.itemView.getResources().getColor(R.color.colorAccent));
        } else {
            holder.status.setText("Safe");
            holder.status.setTextColor(holder.itemView.getResources().getColor(R.color.colorPrimary));
        }

        if (mDataset.get(position).getAccess().equals("none")){
            holder.access.setText("None");
            holder.access.setTextColor(holder.itemView.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.access.setText("Someone");
            holder.access.setTextColor(holder.itemView.getResources().getColor(R.color.colorAccent));
        }

        holder.latitude = mDataset.get(position).getLatitude();
        holder.longitude = mDataset.get(position).getLongitude();
    }

    public void addItem(Bike dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

}
