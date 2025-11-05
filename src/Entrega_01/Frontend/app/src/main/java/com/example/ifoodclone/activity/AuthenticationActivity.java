package com.example.ifoodclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.ifoodclone.R;
import com.example.ifoodclone.model.AuthResponse;
import com.example.ifoodclone.model.LoginRequest;
import com.example.ifoodclone.model.RegisterRequest;
import com.example.ifoodclone.net.ApiClient;
import com.example.ifoodclone.net.ApiService;
import com.example.ifoodclone.util.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationActivity extends AppCompatActivity {

    private EditText etEmail, etSenha, etNome;
    private Button btnAcessar, btnLoginTab, btnRegisterTab;
    private TextView tvForgot;
    private boolean isLoginMode = true;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        etEmail = findViewById(R.id.editTextEmail);
        etSenha = findViewById(R.id.editTextPassword);
        etNome = findViewById(R.id.editTextUsername); // <-- alterado
        etNome.setVisibility(View.GONE);


        btnAcessar = findViewById(R.id.buttonAccess);
        btnLoginTab = findViewById(R.id.buttonLoginTab);
        btnRegisterTab = findViewById(R.id.buttonRegisterTab);
        tvForgot = findViewById(R.id.textViewForgotPassword);

        api = ApiClient.getClient(this).create(ApiService.class);

        // Botões de alternância
        btnLoginTab.setOnClickListener(v -> setMode(true));
        btnRegisterTab.setOnClickListener(v -> setMode(false));

        // Ação principal
        btnAcessar.setOnClickListener(v -> {
            if (isLoginMode) {
                doLogin();
            } else {
                doRegister();
            }
        });
    }

    private void setMode(boolean loginMode) {
        isLoginMode = loginMode;

        if (isLoginMode) {
            etNome.setVisibility(View.GONE);   // Username some
            btnAcessar.setText("Entrar");

            btnLoginTab.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_green));
            btnLoginTab.setTextColor(ContextCompat.getColor(this, android.R.color.white));

            btnRegisterTab.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.light_gray));
            btnRegisterTab.setTextColor(ContextCompat.getColor(this, android.R.color.black));

        } else {
            etNome.setVisibility(View.VISIBLE); // Username aparece
            btnAcessar.setText("Cadastrar");

            btnRegisterTab.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_green));
            btnRegisterTab.setTextColor(ContextCompat.getColor(this, android.R.color.white));

            btnLoginTab.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.light_gray));
            btnLoginTab.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        }
    }


    private void doLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etSenha.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest request = new LoginRequest(email, password);

        api.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    TokenManager.saveToken(AuthenticationActivity.this, token);

                    Toast.makeText(AuthenticationActivity.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(AuthenticationActivity.this, HomeTiaActivity.class));
                    finish();
                } else {
                    Toast.makeText(AuthenticationActivity.this, "Credenciais inválidas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(AuthenticationActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doRegister() {
        String username = etNome.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etSenha.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest request = new RegisterRequest(username, email, password);

        api.register(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AuthenticationActivity.this, "Cadastro realizado! Faça login.", Toast.LENGTH_SHORT).show();
                    setMode(true);
                } else {
                    Toast.makeText(AuthenticationActivity.this, "Erro ao cadastrar. Tente novamente.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AuthenticationActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
