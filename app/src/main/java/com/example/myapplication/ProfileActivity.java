package com.example.myapplication;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.utils.QRCodeUtils;

public class ProfileActivity extends AppCompatActivity {

    private Bitmap qrCodeBitmap;
    private Contact contact;

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
        ImageButton deleteButton = findViewById(R.id.deleteButton);
        TextView roleTextView = findViewById(R.id.roleTextView);

        contact = (Contact) getIntent().getSerializableExtra("contact");

        if (contact != null) {
            profileTextView.setText(contact.getName() + "님의 프로필 ⭐");
            nameTextView.setText(contact.getName());
            phoneTextView.setText(contact.getPhone());
            emailTextView.setText(contact.getEmail());
            groupTextView.setText(contact.getGroup());

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

            // QR 코드 미리 생성
            new GenerateQRCodeTask().execute(contact);
        }

        LinearLayout callLayout = findViewById(R.id.callLayout);
        LinearLayout messageLayout = findViewById(R.id.messageLayout);

        callLayout.setOnClickListener(v -> {
            if (contact != null) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + contact.getPhone()));
                startActivity(callIntent);
            }
        });

        messageLayout.setOnClickListener(v -> {
            if (contact != null) {
                Intent messageIntent = new Intent(Intent.ACTION_VIEW);
                messageIntent.setData(Uri.parse("sms:" + contact.getPhone()));
                startActivity(messageIntent);
            }
        });

        callButton.setOnClickListener(v -> {
            if (contact != null) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + contact.getPhone()));
                startActivity(callIntent);
            }
        });

        messageButton.setOnClickListener(v -> {
            if (contact != null) {
                Intent messageIntent = new Intent(Intent.ACTION_VIEW);
                messageIntent.setData(Uri.parse("sms:" + contact.getPhone()));
                startActivity(messageIntent);
            }
        });

        qrCodeButton.setOnClickListener(v -> {
            // 미리 생성된 QR 코드 표시
            showQRCode();
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(contact);
            }
        });
    }

    private void showQRCode() {
        if (qrCodeBitmap != null) {
            QRCodeDialog qrCodeDialog = new QRCodeDialog(ProfileActivity.this, qrCodeBitmap);
            qrCodeDialog.show();
        } else {
            showCustomToast("QR 생성 중입니다. 잠시 후 다시 시도하세요.");
        }
    }

    private void showCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, null);

        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }


    private void showDeleteConfirmationDialog(Contact contact) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_delete, null);
        TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);
        dialogMessage.setText(contact.getName() + "님의 연락처를 삭제하시겠습니까?");

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
                deleteContact(contact);
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

    private void deleteContact(Contact contact) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("deletedContact", contact);
        setResult(RESULT_OK, resultIntent);
        finish(); // 현재 액티비티 종료
    }

    private class GenerateQRCodeTask extends AsyncTask<Contact, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Contact... contacts) {
            Contact contact = contacts[0];
            String qrData = String.format("{\"name\":\"%s\", \"phone\":\"%s\", \"email\":\"%s\", \"group\":\"%s\", \"role\":\"%s\"}",
                    contact.getName(), contact.getPhone(), contact.getEmail(), contact.getGroup(), contact.getRole());
            return QRCodeUtils.generateQRCode(qrData);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            qrCodeBitmap = bitmap;
        }
    }
}
