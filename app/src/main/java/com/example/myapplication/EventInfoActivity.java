package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.myapplication.utils.StorageUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class EventInfoActivity extends Activity {
    private List<Contact> contacts;
    private AutoCompleteTextView nameInput;

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

        ImageButton shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNameInputDialog();
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

            String jsonContacts = StorageUtils.loadContacts(this);
            contacts = new Gson().fromJson(jsonContacts, new TypeToken<List<Contact>>() {}.getType());

            writerTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

    private void showNameInputDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_input_name, null);

        nameInput = dialogView.findViewById(R.id.nameInput);
        ImageButton positiveButton = dialogView.findViewById(R.id.positiveButton);
        ImageButton negativeButton = dialogView.findViewById(R.id.negativeButton);

        List<String> contactNames = new ArrayList<>();
        for (Contact contact : contacts) {
            contactNames.add(contact.getName());
        }

        CustomArrayAdapter adapter = new CustomArrayAdapter(this, contactNames);
        nameInput.setAdapter(adapter);
        nameInput.setThreshold(1); // 한 글자만 입력해도 자동완성 제안이 나오도록 설정

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getText().toString();
                if (!name.isEmpty()) {
                    Contact contact = findContactByName(name);
                    if (contact != null) {
                        shareEventInfo(contact.getPhone());
                    } else {
                        Toast.makeText(EventInfoActivity.this, "연락처를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EventInfoActivity.this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
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

    private void shareEventInfo(String phoneNumber) {
        Event event = (Event) getIntent().getSerializableExtra("event");
        if (event != null) {
            String eventDetails =
                    "*MADCAMP-CONNECT에서 온 초대장입니다!*" + "\n" + "\n" +
                            "[" + event.getTitle() + "]" + "에 당신을 초대합니다" + "\n" +
                            "시간: " + event.getTime() + "\n" +
                            "장소: " + event.getLocation();

            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setData(Uri.parse("sms:" + phoneNumber));
            smsIntent.putExtra("sms_body", eventDetails);

            startActivity(smsIntent);
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
//                deleteEvent(event);
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

    private Contact findContactByName(String name) {
        if (contacts == null) {
            return null;
        }

        for (Contact contact : contacts) {
            if (contact.getName().equals(name)) {
                return contact;
            }
        }

        return null;
    }
}
