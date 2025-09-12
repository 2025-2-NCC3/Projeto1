package com.example.ifoodclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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
    private EditText editEmail, editPassword;
    private SwitchCompat switchAuth, switchAccessType;
    private TextView textUser, textCompany;
    private Button buttonAccess;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        findViewsById();

        auth = FirebaseConfiguration.getFirebaseAuth();
        verifyCurrentUser(); //Verifica se há um usuário logado
        switchAuth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){ //Checked = fazer login, esconder tipo de cadastro
                    switchAccessType.setVisibility(View.VISIBLE);
                    textUser.setVisibility(View.VISIBLE);
                    textCompany.setVisibility(View.VISIBLE);
                }else{ //Not Checked = fazer cadastro, mostrar empresa ou user
                    switchAccessType.setVisibility(View.GONE);
                    textUser.setVisibility(View.GONE);
                    textCompany.setVisibility(View.GONE);
                }
            }
        });

        buttonAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();

                if(!email.isEmpty()){
                    if(!password.isEmpty()){
                        /* Verificar o estado do switch
                           Ativado - Fazer Cadastro
                           Desativado - Fazer login */
                        if(switchAuth.isChecked()){ //CADASTRO
                            auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(AuthenticationActivity.this, "Registration completed", Toast.LENGTH_SHORT).show();
                                                String type = getUserType();
                                                FirebaseUserConfiguration.updateUserType(type);
                                                openMainActivity(type);
                                            }else{
                                                String exception;
                                                try {
                                                    throw task.getException();
                                                }catch (FirebaseAuthWeakPasswordException e){
                                                    exception = "Enter a stronger password";
                                                }catch (FirebaseAuthInvalidCredentialsException e){
                                                    exception = "Enter a valid email";
                                                }catch (FirebaseAuthUserCollisionException e){
                                                    exception = "Account already registered";
                                                }catch (Exception e){
                                                    exception = "Registration failed: " + e.getMessage();
                                                    e.printStackTrace();
                                                }
                                                Toast.makeText(AuthenticationActivity.this, exception, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else{ //LOGIN
                            auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(AuthenticationActivity.this, "Sign in completed", Toast.LENGTH_SHORT).show();
                                                String userType = task.getResult().getUser().getDisplayName();
                                                openMainActivity(userType);
                                            }else{
                                                Toast.makeText(AuthenticationActivity.this, "Sign in failed, try again later", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }

                    }else{
                        Toast.makeText(AuthenticationActivity.this, "Password field is empty", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AuthenticationActivity.this, "Email field is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void verifyCurrentUser(){
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            String userType = currentUser.getDisplayName();
            openMainActivity(userType);
        }
    }
    private String getUserType(){
        return switchAccessType.isChecked() ? "C" : "U"; //ischecked = company, not checked = user
    }
    private void openMainActivity(String type){
        if(type.equals("C")){ //type = company
            startActivity(new Intent(getApplicationContext(), CompanyActivity.class));
        }else{//type = user
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }
    private void findViewsById(){
        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        switchAuth = findViewById(R.id.switchAuth);
        switchAccessType = findViewById(R.id.switchAccessType);
        textUser = findViewById(R.id.textViewUser);
        textCompany = findViewById(R.id.textViewCompany);
        buttonAccess = findViewById(R.id.buttonAccess);

        switchAccessType.setVisibility(View.GONE);
        textUser.setVisibility(View.GONE);
        textCompany.setVisibility(View.GONE);
    }
}