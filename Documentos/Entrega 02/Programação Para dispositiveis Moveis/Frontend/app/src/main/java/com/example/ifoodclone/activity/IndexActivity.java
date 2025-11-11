package com.example.ifoodclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ifoodclone.model.UserDto;
import com.example.ifoodclone.net.ApiClient;
import com.example.ifoodclone.net.ApiService;
import com.example.ifoodclone.util.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean forceLogin = getIntent().getBooleanExtra("force_login", false);
        if (forceLogin) {
            TokenManager.clear(this);
            startActivity(new Intent(this, AuthenticationActivity.class));
            finish();
            return;
        }

        if (!TokenManager.hasToken(this)) {
            startActivity(new Intent(this, AuthenticationActivity.class));
            finish();
            return;
        }

        String bearer = "Bearer " + TokenManager.getToken(this);
        ApiService api = ApiClient.getClient(this).create(ApiService.class);

        api.getMeuPerfil(bearer).enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    startActivity(new Intent(IndexActivity.this, MainActivity.class));
                } else {
                    TokenManager.clear(IndexActivity.this);
                    startActivity(new Intent(IndexActivity.this, AuthenticationActivity.class));
                }
                finish();
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                Toast.makeText(IndexActivity.this, "Verificando sessão...", Toast.LENGTH_SHORT).show();
                TokenManager.clear(IndexActivity.this);
                startActivity(new Intent(IndexActivity.this, AuthenticationActivity.class));
                finish();
            }
        });
    }
}
