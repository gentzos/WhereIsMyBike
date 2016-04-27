package com.aau.wimb.whereismybike;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Konstantinos on 026 26 4 2016.
 */
public class Bike implements Parcelable {

    private String name;
    private String id;
    private String status;
    private String access;
    private String latitude;
    private String longitude;

    public Bike(String name, String id, String status, String access, String latitude, String longitude) {
        this.name = name;
        this.id = id;
        this.status = status;
        this.access = access;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Bike(Parcel in) {
        name = in.readString();
        id = in.readString();
        status = in.readString();
        access = in.readString();
        latitude = in.readString();
        longitude = in.readString();
    }

    public static final Creator<Bike> CREATOR = new Creator<Bike>() {
        @Override
        public Bike createFromParcel(Parcel in) {
            return new Bike(in);
        }

        @Override
        public Bike[] newArray(int size) {
            return new Bike[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(status);
        dest.writeString(access);
        dest.writeString(latitude);
        dest.writeString(longitude);
    }

}
