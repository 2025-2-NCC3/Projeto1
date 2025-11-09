package com.example.ifoodclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ifoodclone.R;
import com.example.ifoodclone.model.UpdateUserRequest;
import com.example.ifoodclone.model.UserDto;
import com.example.ifoodclone.net.ApiClient;
import com.example.ifoodclone.net.ApiService;
import com.example.ifoodclone.util.TokenManager; // <- usar TokenManager

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilActivity extends AppCompatActivity {

    private EditText inputNome, inputEmail, inputSenha;
    private Button btnSalvar;

    private ApiService api;

    // (opcional) menu inferior
    private LinearLayout bottomMenu;
    private ImageView iconHome, iconFavorite, iconCart, iconHistory, iconProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telaperfil);

        // verifica token com TokenManager
        if (!TokenManager.hasToken(this)) {
            startActivity(new Intent(this, AuthenticationActivity.class));
            finish();
            return;
        }

        api = ApiClient.getClient(this).create(ApiService.class);

        // bind
        inputNome  = findViewById(R.id.inputNome);
        inputEmail = findViewById(R.id.inputEmail);
        inputSenha = findViewById(R.id.inputSenha);
        btnSalvar  = findViewById(R.id.btnSalvar);

        bottomMenu   = findViewById(R.id.bottomMenu);
        iconHome     = findViewById(R.id.iconHome);
        iconFavorite = findViewById(R.id.iconFavorite);
        iconCart     = findViewById(R.id.iconCart);
        iconHistory  = findViewById(R.id.iconHistory);
        iconProfile  = findViewById(R.id.iconProfile);

        carregarPerfil();
        btnSalvar.setOnClickListener(v -> salvarAlteracoes());
        configurarMenuInferior();
    }

    private void carregarPerfil() {
        String bearer = "Bearer " + TokenManager.getToken(this);
        btnSalvar.setEnabled(false);

        api.getMeuPerfil(bearer).enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                btnSalvar.setEnabled(true);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(PerfilActivity.this, "Falha ao carregar perfil", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserDto user = response.body();
                if (user.getUsername() != null) inputNome.setText(user.getUsername());
                if (user.getEmail() != null)    inputEmail.setText(user.getEmail());
                inputSenha.setText("");
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                btnSalvar.setEnabled(true);
                Toast.makeText(PerfilActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void salvarAlteracoes() {
        String nome  = inputNome.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String senha = inputSenha.getText().toString();

        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Nome e e-mail são obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.isEmpty(senha) && senha.length() < 6) {
            Toast.makeText(this, "Senha deve ter no mínimo 6 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateUserRequest body = new UpdateUserRequest(
                nome,
                email,
                TextUtils.isEmpty(senha) ? null : senha
        );

        String bearer = "Bearer " + TokenManager.getToken(this);
        btnSalvar.setEnabled(false);

        api.updateMeuPerfil(bearer, body).enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                btnSalvar.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(PerfilActivity.this, "Perfil atualizado!", Toast.LENGTH_SHORT).show();
                    UserDto user = response.body();
                    inputNome.setText(user.getUsername() != null ? user.getUsername() : nome);
                    inputEmail.setText(user.getEmail() != null ? user.getEmail() : email);
                    inputSenha.setText("");
                } else {
                    Toast.makeText(PerfilActivity.this, "Não foi possível atualizar (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                btnSalvar.setEnabled(true);
                Toast.makeText(PerfilActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarMenuInferior() {
        if (iconHome != null) {
            iconHome.setOnClickListener(v ->
                    startActivity(new Intent(PerfilActivity.this, MainActivity.class)));
        }
        if (iconCart != null) {
            iconCart.setOnClickListener(v ->
                    startActivity(new Intent(PerfilActivity.this, CarrinhoActivity.class)));
        }
        if (iconProfile != null) {
            iconProfile.setOnClickListener(v -> carregarPerfil());
        }
    }
}
