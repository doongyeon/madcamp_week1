package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AddEventActivity extends AppCompatActivity {

    private static final String SHARED_PREFS_NAME = "calendar_events";
    private static final String EVENTS_KEY = "events";

    private EditText writerEditText, titleEditText, contentsEditText, locationEditText, timeEditText;
    private Button saveButton;
    private String selectedDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        initializeViews();
        setupBackButton();
        setupSaveButton();
        retrieveSelectedDate();
    }

    private void initializeViews() {
        writerEditText = findViewById(R.id.writerEditText);
        titleEditText = findViewById(R.id.titleEditText);
        contentsEditText = findViewById(R.id.contentsEditText);
        locationEditText = findViewById(R.id.locationEditText);
        timeEditText = findViewById(R.id.timeEditText);
        saveButton = findViewById(R.id.saveButton);
    }

    private void setupBackButton() {
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티 종료
            }
        });
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
            }
        });
    }

    private void retrieveSelectedDate() {
        // Intent로부터 날짜 정보 받기
        selectedDate = getIntent().getStringExtra("selectedDate");
    }

    private void saveEvent() {
        String writer = writerEditText.getText().toString();
        String title = titleEditText.getText().toString();
        String contents = contentsEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String time = formatTime(timeEditText.getText().toString());

        Event newEvent = new Event(title, contents, writer, location, selectedDate, time, "private", false);

        // SharedPreferences에 저장된 이벤트 업데이트
        storeNewEvent(newEvent);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("newEvent", newEvent);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void storeNewEvent(Event newEvent) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String eventsJson = prefs.getString(EVENTS_KEY, null);
        Gson gson = new Gson();
        List<Event> events;

        if (eventsJson != null) {
            Type eventType = new TypeToken<ArrayList<Event>>() {}.getType();
            events = gson.fromJson(eventsJson, eventType);
        } else {
            events = new ArrayList<>();
        }

        events.add(newEvent);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(EVENTS_KEY, gson.toJson(events));
        editor.apply();
    }

    // 시간을 두 자리 숫자로 포맷팅하는 메서드
    private String formatTime(String time) {
        if (time.length() == 5) {
            return time;
        } else if (time.length() == 4) {
            return "0" + time;
        } else {
            return time;
        }
    }
}
