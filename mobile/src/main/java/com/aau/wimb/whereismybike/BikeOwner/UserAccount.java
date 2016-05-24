package com.aau.wimb.whereismybike.BikeOwner;
import android.os.Parcel;
import android.os.Parcelable;

import com.aau.wimb.whereismybike.Account;

/**
 * Created by konst on 012 12/4/2016.
 */
public class UserAccount implements Account, Parcelable {

    private String uniqueId;
    private String email;
    private String pwd;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;

    // Facebook
    private String profileId;
    private String profileLink;
    private String profilePic;

    public UserAccount() {
    }

    public UserAccount(String uniqueId, String email, String pwd, String firstName, String lastName, String address, String phoneNumber, String createDate) {
        this.uniqueId = uniqueId;
        this.email = email;
        this.pwd = pwd;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    protected UserAccount(Parcel in) {
        uniqueId = in.readString();
        email = in.readString();
        pwd = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        address = in.readString();
        phoneNumber = in.readString();
        profileId = in.readString();
        profileLink = in.readString();
        profilePic = in.readString();
    }

    public static final Creator<UserAccount> CREATOR = new Creator<UserAccount>() {
        @Override
        public UserAccount createFromParcel(Parcel in) {
            return new UserAccount(in);
        }

        @Override
        public UserAccount[] newArray(int size) {
            return new UserAccount[size];
        }
    };

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

//    @Override
//    public void register{
//    }
//
//    @Override
//    public void login() {
//
//    }

    @Override
    public void manageAccount() {

    }

    @Override
    public void trackBike() {

    }

    public void addDeleteBike(){

    }

    public void lockUnlockBike(){

    }

    public void allowDenyAccess(){

    }

    public void alarmOnOff(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uniqueId);
        dest.writeString(email);
        dest.writeString(pwd);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(address);
        dest.writeString(phoneNumber);
        dest.writeString(profileId);
        dest.writeString(profileLink);
        dest.writeString(profilePic);
    }
}
