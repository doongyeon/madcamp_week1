package com.example.myapplication.utils;

import com.example.myapplication.Contact;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ContactUtils {
    public static List<Contact> parseContacts(String json) {
        List<Contact> contacts = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String phone = jsonObject.getString("phone");
                String email = jsonObject.getString("email");
                String group = jsonObject.getString("group");
                contacts.add(new Contact(name, phone, email, group));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contacts;
    }
}

