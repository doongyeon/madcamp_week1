package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddEventActivity extends AppCompatActivity {

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

        Intent resultIntent = new Intent();
        resultIntent.putExtra("newEvent", newEvent);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
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
