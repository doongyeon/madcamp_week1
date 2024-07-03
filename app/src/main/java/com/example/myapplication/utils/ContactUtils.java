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
                String role = jsonObject.getString("role");
                contacts.add(new Contact(name, phone, email, group, role));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    public static String contactsToJson(List<Contact> contacts) {
        JSONArray jsonArray = new JSONArray();
        for (Contact contact : contacts) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", contact.getName());
                jsonObject.put("phone", contact.getPhone());
                jsonObject.put("email", contact.getEmail());
                jsonObject.put("group", contact.getGroup());
                jsonObject.put("role", contact.getRole());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }
}

