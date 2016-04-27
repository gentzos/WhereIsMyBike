package com.aau.wimb.whereismybike.Police;

import com.aau.wimb.whereismybike.Account;

/**
 * Created by konst on 012 12/4/2016.
 */
public class PoliceAccount implements Account {

    private int policeId;
    private int policemanId;
    private String email;
    private String pwd;
    private String Address;
    private String phoneNumber;

    public PoliceAccount(int policeId, int policemanId, String email, String pwd, String address, String phoneNumber) {
        this.policeId = policeId;
        this.policemanId = policemanId;
        this.email = email;
        this.pwd = pwd;
        Address = address;
        this.phoneNumber = phoneNumber;
    }

    public int getPoliceId() {
        return policeId;
    }

    public void setPoliceId(int policeId) {
        this.policeId = policeId;
    }

    public int getPolicemanId() {
        return policemanId;
    }

    public void setPolicemanId(int policemanId) {
        this.policemanId = policemanId;
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

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

//    @Override
//    public void register() {
//
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

    public void identifyBikes(){

    }

    public void findStolenBikes(){

    }

}
