package com.example.myapplication;

import java.io.Serializable;

public class Event implements Serializable {
    private String title;
    private String contents;
    private String writer;
    private String location;
    private String date;
    private String time;
    private String type;
    private Boolean isFavorite;

    public Event(String title, String contents, String writer, String location, String date, String time, String type, Boolean isFavorite) {
        this.title = title;
        this.contents = contents;
        this.writer = writer;
        this.location = location;
        this.date = date;
        this.time = time;
        this.type = type;
        this.isFavorite = isFavorite;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public String getWriter() {
        return writer;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public Boolean getIsFavorite() { return isFavorite; }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}

