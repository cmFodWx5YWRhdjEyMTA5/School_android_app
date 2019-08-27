package com.microbotic.temperature.model;

import com.google.gson.annotations.SerializedName;
import com.microbotic.temperature.app.Config;

public class School {

    @SerializedName(Config.ID)
    private String id;
    @SerializedName(Config.NAME)
    private String name;
    @SerializedName(Config.ADDRESS)
    private String address;
    @SerializedName(Config.PHONE)
    private String phone;
    @SerializedName(Config.EMAIL)
    private String email;
    @SerializedName(Config.STATUS)
    private String status;
    @SerializedName(Config.LOGO)
    private String logo;
    @SerializedName(Config.ROLE)
    private String role;
    @SerializedName(Config.USERNAME)
    private String username;
    @SerializedName(Config.PASSWORD)
    private String password;


    public School(String id, String name, String address, String phone, String email, String status, String logo, String role) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.status = status;
        this.logo = logo;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public String getLogo() {
        return logo;
    }
}
