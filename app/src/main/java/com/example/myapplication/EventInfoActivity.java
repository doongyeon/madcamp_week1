package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class EventInfoActivity extends AddContactActivity {
    private List<Contact> contacts;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_info);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView titleTextView = findViewById(R.id.titleText);
        TextView contentTextView = findViewById(R.id.contentText);
        TextView timeTextView = findViewById(R.id.timeText);
        TextView locateTextView = findViewById(R.id.locateText);
        TextView writerTextView = findViewById(R.id.writerText);

        Event event = (Event) getIntent().getSerializableExtra("event");

        if (event != null) {
            titleTextView.setText(event.getTitle());
            contentTextView.setText(event.getContents());
            writerTextView.setText("@" + event.getWriter());
            timeTextView.setText(event.getTime());
            locateTextView.setText(event.getLocation());

            SpannableString content = new SpannableString(writerTextView.getText());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            writerTextView.setText(content);

            loadContactsFromJson();

            writerTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 클릭 시 해당 이름을 가진 Contact를 찾아 ProfileActivity로 이동
                    Contact foundContact = findContactByName(event.getWriter());
                    if (foundContact != null) {
                        Intent intent = new Intent(EventInfoActivity.this, ProfileActivity.class);
                        intent.putExtra("contact", foundContact);
                        startActivity(intent);
                    }
                }
            });

            ImageButton deleteButton = findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog(event);
                }
            });
        }
    }

    private void showDeleteConfirmationDialog(Event event) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_delete, null);
        TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);
        dialogMessage.setText("이 이벤트를 삭제하시겠습니까?");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create();

        ImageButton positiveButton = dialogView.findViewById(R.id.positiveButton);
        ImageButton negativeButton = dialogView.findViewById(R.id.negativeButton);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 연락처 삭제 로직 추가
                deleteEvent(event);
                dialog.dismiss();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // contacts.json 파일에서 Contact 리스트 읽어오기
    private void loadContactsFromJson() {
        try {
            Resources resources = getResources();
            InputStream inputStream = resources.openRawResource(R.raw.contacts);
            Reader reader = new InputStreamReader(inputStream);

            // Gson을 사용하여 JSON 데이터 파싱
            Gson gson = new Gson();
            contacts = gson.fromJson(reader, new TypeToken<List<Contact>>() {}.getType());

            reader.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteEvent(Event event) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("deletedEvent", event);
        setResult(Activity.RESULT_OK, resultIntent);
        finish(); // 현재 액티비티 종료
    }

    // 이름을 기반으로 Contact를 찾는 메서드
    private Contact findContactByName(String name) {
        if (contacts == null) {
            return null;
        }

        for (Contact contact : contacts) {
            if (contact.getName().equals(name)) {
                return contact;
            }
        }

        return null; // 해당하는 Contact를 찾지 못한 경우
    }
}
