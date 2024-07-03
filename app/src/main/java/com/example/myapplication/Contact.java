package com.example.myapplication;

import java.io.Serializable;

public class Contact implements Serializable{
    private String name;
    private String phone;
    private String email;
    private String group;
    private String role;

    public Contact(String name, String phone, String email, String group, String role) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.group = group;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getGroup() { return group; }

    public String getRole() { return role; }
}
