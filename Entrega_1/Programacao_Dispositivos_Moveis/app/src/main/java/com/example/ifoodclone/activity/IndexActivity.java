package com.example.ifoodclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.ifoodclone.R;

public class IndexActivity extends AppCompatActivity {

    private Button buttonEnter, buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        buttonEnter = findViewById(R.id.buttonEnter);

        buttonEnter.setOnClickListener(v -> {
            Intent intent = new Intent(IndexActivity.this, AuthenticationActivity.class);
            startActivity(intent);
        });

        buttonRegister = findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(v -> {
            Intent intent = new Intent(IndexActivity.this, AuthenticationActivity.class);
            intent.putExtra("MODE", "REGISTER");
            startActivity(intent);
        });


    }
}
