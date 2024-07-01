package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

public class AddContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티 종료
            }
        });

        EditText nameEditText = findViewById(R.id.nameEditText);
        EditText phoneEditText = findViewById(R.id.phoneEditText);
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText groupEditText = findViewById(R.id.groupEditText);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        Button saveButton = findViewById(R.id.saveButton);
        TextView addByQR =findViewById(R.id.addByQR);

        SpannableString content = new SpannableString("QR 코드로 추가하기");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        addByQR.setText(content);

        addByQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(AddContactActivity.this).initiateScan(); // QR 코드 스캐너 호출
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String group = groupEditText.getText().toString();

                int selectedId = radioGroup.getCheckedRadioButtonId();
                String role = "";
                if (selectedId == R.id.radioAdmin) {
                    role = "admin";
                } else if (selectedId == R.id.radioParticipant) {
                    role = "participant";
                }

                Contact newContact = new Contact(name, phone, email, group, role);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("newContact", newContact);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                // QR 코드 스캔 결과 처리
                String qrData = result.getContents();
                // QR 코드에서 연락처 정보를 파싱하고, EditText에 설정하는 로직 추가
                // 예: JSON 형식으로 파싱
                try {
                    JSONObject jsonObject = new JSONObject(qrData);
                    EditText nameEditText = findViewById(R.id.nameEditText);
                    EditText phoneEditText = findViewById(R.id.phoneEditText);
                    EditText emailEditText = findViewById(R.id.emailEditText);
                    EditText groupEditText = findViewById(R.id.groupEditText);

                    nameEditText.setText(jsonObject.getString("name"));
                    phoneEditText.setText(jsonObject.getString("phone"));
                    emailEditText.setText(jsonObject.getString("email"));
                    groupEditText.setText(jsonObject.getString("group"));

                    String role = jsonObject.getString("role");
                    RadioGroup radioGroup = findViewById(R.id.radioGroup);
                    if ("admin".equals(role)) {
                        radioGroup.check(R.id.radioAdmin);
                    } else if ("participant".equals(role)) {
                        radioGroup.check(R.id.radioParticipant);
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "QR 코드 스캔에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "QR 코드 스캔이 취소되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
