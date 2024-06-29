package com.example.myapplication.model;

public class Event {
    private String title;
    private String contents;
    private String writer;
    private String location;
    private String time;
    private String date;
    private String type;

    public Event(String title, String contents, String writer, String location, String time, String date, String type) {
        this.title = title;
        this.contents = contents;
        this.writer = writer;
        this.location = location;
        this.time = time;
        this.date = date;
        this.type = type;
    }

    // Getter methods
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

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }
}
