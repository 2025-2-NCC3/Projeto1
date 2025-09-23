package com.example.ifoodclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.ifoodclone.R;

public class DashboardActivity extends AppCompatActivity {

    private TextView textViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        textViewTitle.setText("Tela de Dashboard");
    }
}
