package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class FirstSplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_splash);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(FirstSplashActivity.this, SecondSplashActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }, 1000); // 0.5초 지연
    }
}

