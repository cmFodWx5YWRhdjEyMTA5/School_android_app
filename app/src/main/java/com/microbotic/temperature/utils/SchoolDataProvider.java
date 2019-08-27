package com.microbotic.temperature.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.microbotic.temperature.app.Config;
import com.microbotic.temperature.model.School;


public class SchoolDataProvider {

    private SharedPreferences preferences;

    public SchoolDataProvider(Context context) {
        preferences = context.getSharedPreferences(Config.APP_COPE, Context.MODE_PRIVATE);
    }

    public void saveSchool(String username, String password, School school) {
        SharedPreferences.Editor editor = preferences.edit();


        editor.putString(Config.ACCOUNT_ID, school.getId());
        editor.putString(Config.USERNAME, username);
        editor.putString(Config.PASSWORD, password);
        editor.putString(Config.NAME, school.getName());
        editor.putString(Config.ADDRESS, school.getAddress());
        editor.putString(Config.PHONE, school.getPhone());
        editor.putString(Config.EMAIL, school.getEmail());
        editor.putString(Config.LOGO, school.getLogo());
        editor.putString(Config.ROLE, school.getRole());

        editor.putBoolean("login", true);

        editor.apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean("login", false);
    }

    public String getSchoolName() {
        return preferences.getString(Config.NAME, "");
    }

    public String getUsername() {
        return preferences.getString(Config.USERNAME, "");
    }

    public String getPassword() {
        return preferences.getString(Config.PASSWORD, "");
    }

    public String getAddress() {
        return preferences.getString(Config.ADDRESS, "");
    }

    public String getName() {
        return preferences.getString(Config.NAME, "");
    }

    public String getId() {
        return preferences.getString(Config.ACCOUNT_ID, "");
    }

    public String getLogo() {
        return preferences.getString(Config.LOGO, "");
    }

    public String getEmail() {
        return preferences.getString(Config.EMAIL, "");
    }

    public String getPhone() {
        return preferences.getString(Config.PHONE, "");
    }


    public void logOut() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public boolean isPermissionAsked() {
        return preferences.getBoolean("permissionsAsked", false);
    }

    public void dontAskPermission() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("permissionsAsked",true);
        editor.apply();
    }
}
