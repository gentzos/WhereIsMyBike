package com.aau.wimb.whereismybike;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Konstantinos on 026 26 4 2016.
 */
public class Bike implements Parcelable {

    private String vin;
    private String brand;
    private String color;
    private boolean lock;
    private boolean status;
    private String access;
    private double latitude;
    private double longitude;

    public Bike(String vin, String brand, String color, boolean lock, boolean status, String access, double latitude, double longitude) {
        this.vin = vin;
        this.brand = brand;
        this.color = color;
        this.lock = lock;
        this.status = status;
        this.access = access;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Bike(Parcel in) {
        vin = in.readString();
        brand = in.readString();
        color = in.readString();
        lock = in.readByte() != 0;
        status = in.readByte() != 0;
        access = in.readString();
        latitude = in.readFloat();
        longitude = in.readFloat();
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

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(vin);
        dest.writeString(brand);
        dest.writeString(color);
        dest.writeByte((byte) (lock ? 1 : 0));
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeString(access);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
