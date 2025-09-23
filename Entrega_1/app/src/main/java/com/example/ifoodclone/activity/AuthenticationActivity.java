package com.example.ifoodclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ifoodclone.R;
import com.example.ifoodclone.helper.FirebaseConfiguration;
import com.example.ifoodclone.helper.FirebaseUserConfiguration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationActivity extends AppCompatActivity {

    private EditText editEmail, editPassword, editPhone;
    private Button buttonLoginTab, buttonRegisterTab, buttonAccess;
    private TextView textViewWelcome, textViewForgotPassword;

    private FirebaseAuth auth;

    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        findViewsById();
        auth = FirebaseConfiguration.getFirebaseAuth();


        buttonLoginTab.setOnClickListener(v -> setLoginMode(true));


        buttonRegisterTab.setOnClickListener(v -> setLoginMode(false));


        buttonAccess.setOnClickListener(v -> handleAccess());

        String mode = getIntent().getStringExtra("MODE");
        if (mode != null && mode.equals("REGISTER")) {
            setLoginMode(false);
        }
    }

    private void handleAccess() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (!email.isEmpty()) {
            if (!password.isEmpty()) {
                if (isLoginMode) {

                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AuthenticationActivity.this, "Sign in completed", Toast.LENGTH_SHORT).show();
                                        String userType = task.getResult().getUser().getDisplayName();
                                        openMainActivity(userType);
                                    } else {
                                        Toast.makeText(AuthenticationActivity.this, "Sign in failed, try again later", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // CADASTRO
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AuthenticationActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                                        // por padrão, usuário normal
                                        String type = "U";
                                        FirebaseUserConfiguration.updateUserType(type);
                                        openMainActivity(type);
                                    } else {
                                        String exception;
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthWeakPasswordException e) {
                                            exception = "Senha fraca!";
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            exception = "E-mail inválido!";
                                        } catch (FirebaseAuthUserCollisionException e) {
                                            exception = "Usuário já existente";
                                        } catch (Exception e) {
                                            exception = "Cadastro falhou: " + e.getMessage();
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(AuthenticationActivity.this, exception, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            } else {
                Toast.makeText(this, "Senha vazia!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "E-mail vazio!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setLoginMode(boolean loginMode) {
        isLoginMode = loginMode;

        if (isLoginMode) {

            buttonLoginTab.setBackgroundTintList(getColorStateList(R.color.green_dark));
            buttonLoginTab.setTextColor(getColor(android.R.color.white));

            buttonRegisterTab.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
            buttonRegisterTab.setTextColor(getColor(android.R.color.black));

            textViewWelcome.setText("Bem-vindo à Comedoria da Tia!");
            buttonAccess.setText("Entrar");

            editPhone.setVisibility(View.GONE);
            textViewForgotPassword.setVisibility(View.VISIBLE);

        } else {

            buttonRegisterTab.setBackgroundTintList(getColorStateList(R.color.green_dark));
            buttonRegisterTab.setTextColor(getColor(android.R.color.white));

            buttonLoginTab.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
            buttonLoginTab.setTextColor(getColor(android.R.color.black));

            textViewWelcome.setText("Novo por aqui?\nCadastre-se!");
            buttonAccess.setText("Cadastrar");

            editPhone.setVisibility(View.VISIBLE);
            textViewForgotPassword.setVisibility(View.GONE);
        }
    }

    private void verifyCurrentUser() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userType = currentUser.getDisplayName();
            openMainActivity(userType);
        }
    }

    private void openMainActivity(String type) {

            startActivity(new Intent(getApplicationContext(), HomeTiaActivity.class));

        finish();
    }


    private void findViewsById() {
        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        editPhone = findViewById(R.id.editTextPhone);

        buttonLoginTab = findViewById(R.id.buttonLoginTab);
        buttonRegisterTab = findViewById(R.id.buttonRegisterTab);
        buttonAccess = findViewById(R.id.buttonAccess);

        textViewWelcome = findViewById(R.id.textViewWelcome);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
    }
}
