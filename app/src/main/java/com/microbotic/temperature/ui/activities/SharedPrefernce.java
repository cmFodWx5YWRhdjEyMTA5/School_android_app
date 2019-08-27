package com.microbotic.temperature.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefernce {

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;


    public static void setValue(Context context, String key, String value) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
        editor.apply();
    }


    public static String getValue(Context context, String key) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, null);

    }


    public static void setIntValue(Context context, String key, int value) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
        editor.apply();
    }

    public static int getIntValue(Context context, String key) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(key, 0);
    }


    public static void removeUsername(Context context, String key) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putString(key, null);
        editor.commit();
        editor.apply();

    }


}
