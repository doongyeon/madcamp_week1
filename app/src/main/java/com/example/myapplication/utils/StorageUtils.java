package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class StorageUtils {

    private static final String PREFS_NAME = "contacts_prefs";
    private static final String CONTACTS_KEY = "contacts_key";

    public static void saveContacts(Context context, String jsonContacts) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CONTACTS_KEY, jsonContacts);
        editor.apply();
    }

    public static String loadContacts(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(CONTACTS_KEY, null);
    }
}