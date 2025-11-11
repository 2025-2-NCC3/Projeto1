package com.example.ifoodclone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.ifoodclone.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openAuthentication();
            }
        }, 3000);
    }
    private void openAuthentication(){
        Intent i = new Intent(SplashActivity.this, AuthenticationActivity.class);
        startActivity(i);
        finish();
    }
}