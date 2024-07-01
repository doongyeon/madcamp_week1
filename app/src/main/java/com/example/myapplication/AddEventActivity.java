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

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티 종료
            }
        });

        writerEditText = findViewById(R.id.writerEditText);
        titleEditText = findViewById(R.id.titleEditText);
        contentsEditText = findViewById(R.id.contentsEditText);
        locationEditText = findViewById(R.id.locationEditText);
        timeEditText = findViewById(R.id.timeEditText);
        saveButton = findViewById(R.id.saveButton);

        // Intent로부터 날짜 정보 받기
        selectedDate = getIntent().getStringExtra("selectedDate");

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
            }
        });
    }

    private void saveEvent() {
        String writer = writerEditText.getText().toString();
        String title = titleEditText.getText().toString();
        String contents = contentsEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String time = timeEditText.getText().toString();

        Event newEvent = new Event(title, contents, writer, location, selectedDate, time, "private", false);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("newEvent", newEvent);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
