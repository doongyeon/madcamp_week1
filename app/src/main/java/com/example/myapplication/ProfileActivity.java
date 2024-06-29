package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티 종료
            }
        });

        TextView profileTextView = findViewById(R.id.profileText);
        TextView nameTextView = findViewById(R.id.nameTextView);
        TextView phoneTextView = findViewById(R.id.phoneTextView);
        TextView emailTextView = findViewById(R.id.emailTextView);
        TextView groupTextView = findViewById(R.id.groupTextView);
        ImageButton callButton = findViewById(R.id.callButton);
        ImageButton messageButton = findViewById(R.id.messageButton);

        Contact contact = (Contact) getIntent().getSerializableExtra("contact");

        if (contact != null) {
            profileTextView.setText(contact.getName() + "님의 프로필");
            nameTextView.setText(contact.getName());
            phoneTextView.setText(contact.getPhone());
            emailTextView.setText(contact.getEmail());
            groupTextView.setText(contact.getGroup());
        }

        LinearLayout callLayout = findViewById(R.id.callLayout);
        LinearLayout messageLayout = findViewById(R.id.messageLayout);

        callLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contact != null) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + contact.getPhone()));
                    startActivity(callIntent);
                }
            }
        });

        messageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contact != null) {
                    Intent messageIntent = new Intent(Intent.ACTION_VIEW);
                    messageIntent.setData(Uri.parse("sms:" + contact.getPhone()));
                    startActivity(messageIntent);
                }
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contact != null) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + contact.getPhone()));
                    startActivity(callIntent);
                }
            }
        });

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contact != null) {
                    Intent messageIntent = new Intent(Intent.ACTION_VIEW);
                    messageIntent.setData(Uri.parse("sms:" + contact.getPhone()));
                    startActivity(messageIntent);
                }
            }
        });
    }
}

