package com.aau.wimb.whereismybike.BikeOwner;

import com.aau.wimb.whereismybike.Account;

/**
 * Created by konst on 012 12/4/2016.
 */
public class UserAccount implements Account {

    private String uniqueId;
    private String email;
    private String pwd;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String createDate;

    public UserAccount(String uniqueId, String email, String pwd, String firstName, String lastName, String address, String phoneNumber, String createDate) {
        this.uniqueId = uniqueId;
        this.email = email;
        this.pwd = pwd;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.createDate = createDate;
    }

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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    @Override
    public void register() {

    }

    @Override
    public void login() {

    }

    @Override
    public void manageAccount() {

    }

    @Override
    public void trackBike() {

    }

    public void registerDeleteBike(){

    }

    public void lockUnlockBike(){

    }

    public void allowDenyAccess(){

    }

    public void activateAlarmOnOff(){

    }

}
