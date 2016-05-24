package com.aau.wimb.whereismybike;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Konstantinos on 026 26 4 2016.
 */
public class Bike implements Parcelable {

    private String id;
    private String vin;
    private String brand;
    private String color;
    private String symetricKey;
    private boolean lock;
    private boolean status;
    private String access;
    private double latitude;
    private double longitude;

    public Bike(){

    }

    public Bike(String id, String vin, String brand, String color, String symetricKey, boolean lock, boolean status, String access, double latitude, double longitude) {
        this.id = id;
        this.vin = vin;
        this.brand = brand;
        this.color = color;
        this.symetricKey = symetricKey;
        this.lock = lock;
        this.status = status;
        this.access = access;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Bike(Parcel in) {
        id = in.readString();
        vin = in.readString();
        brand = in.readString();
        color = in.readString();
        symetricKey = in.readString();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getSymetricKey() {
        return symetricKey;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setSymetricKey(String symetricKey) {
        this.symetricKey = symetricKey;
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
        dest.writeString(id);
        dest.writeString(vin);
        dest.writeString(brand);
        dest.writeString(color);
        dest.writeString(symetricKey);
        dest.writeByte((byte) (lock ? 1 : 0));
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeString(access);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

//    @Override
//    public int hashCode() {
//        final int prime = 31;
//        int result = 1;
//        result = prime * result + ((id == null) ? 0 : id.hashCode());
//        return result;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj)
//            return true;
//        if (obj == null)
//            return false;
//        if (getClass() != obj.getClass())
//            return false;
//        Bike other = (Bike) obj;
//        if (id == null) {
//            if (other.id != null)
//                return false;
//        } else if (!id.equals(other.id))
//            return false;
//        return true;
//    }
}
