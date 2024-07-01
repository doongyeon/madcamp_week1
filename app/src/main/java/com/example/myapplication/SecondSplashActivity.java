package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SecondSplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_splash);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SecondSplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 1000); // 0.5초 지연
    }
}
