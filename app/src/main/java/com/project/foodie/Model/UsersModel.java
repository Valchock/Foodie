package com.project.foodie.Model;

public class UsersModel {

    private String name;
    private String address;
    private String phone;
    private String uid;

    public UsersModel() {
    }

    public UsersModel(String name, String address, String phone, String uid) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}
