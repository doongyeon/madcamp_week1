package com.example.myapplication;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.graphics.Bitmap;
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

import com.example.myapplication.utils.QRCodeUtils;

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
        ImageButton qrCodeButton = findViewById(R.id.qr_code);
        TextView roleTextView = findViewById(R.id.roleTextView);

        Contact contact = (Contact) getIntent().getSerializableExtra("contact");

        if (contact != null) {
            profileTextView.setText(contact.getName() + "님의 프로필");
            nameTextView.setText(contact.getName());
            phoneTextView.setText(contact.getPhone());
            emailTextView.setText(contact.getEmail());
            groupTextView.setText(contact.getGroup());
        }

        if ("해당없음".equals(contact.getGroup())) {
            groupTextView.setVisibility(View.INVISIBLE);
        } else {
            groupTextView.setVisibility(View.VISIBLE);
        }

        if ("1분반".equals(contact.getGroup())) {
            groupTextView.setBackgroundResource(R.drawable.group1_background);
        } else if ("2분반".equals(contact.getGroup())){
            groupTextView.setBackgroundResource(R.drawable.group2_background);
        } else if ("4분반".equals(contact.getGroup())) {
            groupTextView.setBackgroundResource(R.drawable.group4_background);
        } else {
            groupTextView.setBackgroundResource(R.drawable.group_background);
        }

        if ("운영진".equals(contact.getRole())) {
            roleTextView.setVisibility(View.VISIBLE);
        } else {
            roleTextView.setVisibility(View.GONE);
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

        qrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // QR 코드 생성 및 표시
                generateAndShowQRCode(contact);
            }
        });
    }

    private void generateAndShowQRCode(Contact contact) {
        // 현재 프로필 정보를 가져와서 JSON 형식으로 변환
        String qrData = String.format("{\"name\":\"%s\", \"phone\":\"%s\", \"email\":\"%s\", \"group\":\"%s\", \"role\":\"%s\"}",
                contact.getName(), contact.getPhone(), contact.getEmail(), contact.getGroup(), contact.getRole());

        Bitmap qrCodeBitmap = QRCodeUtils.generateQRCode(qrData);

        QRCodeDialog qrCodeDialog = new QRCodeDialog(ProfileActivity.this, qrCodeBitmap);
        qrCodeDialog.show();
    }
}

