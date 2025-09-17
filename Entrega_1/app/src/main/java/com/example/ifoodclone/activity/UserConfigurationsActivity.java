package com.example.ifoodclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ifoodclone.R;
import com.example.ifoodclone.helper.FirebaseConfiguration;
import com.example.ifoodclone.helper.FirebaseUserConfiguration;
import com.example.ifoodclone.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class UserConfigurationsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText editName, editAddress;
    private Button buttonConfirm;
    private DatabaseReference databaseReference;
    private User recoverUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_configurations);
        findViewsById();
        toolbarConfig();
        databaseReference = FirebaseConfiguration.getDatabaseReference();
        recoverUserData();

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputValidation(view);
            }
        });
    }
    private void findViewsById(){
        toolbar = findViewById(R.id.toolbar);
        editAddress = findViewById(R.id.editTextUserAddress);
        editName = findViewById(R.id.editTextUserName);
        buttonConfirm = findViewById(R.id.buttonUserConfigSave);
    }
    private void toolbarConfig(){
        toolbar.setTitle("New Product");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void recoverUserData(){
        DatabaseReference userReference = databaseReference.child("users").child(FirebaseUserConfiguration.getUserId());
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    recoverUser = new User();
                    recoverUser = snapshot.getValue(User.class);
                    editName.setText(recoverUser.getName());
                    editAddress.setText(recoverUser.getAddress());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void inputValidation(View v){
        String name = editName.getText().toString();
        String address = editAddress.getText().toString();

        if(!name.isEmpty()){
            if(!address.isEmpty()){
                /* Fim da validação dos campos */
                User user = new User();
                user.setId(FirebaseUserConfiguration.getUserId());
                user.setName(name);
                user.setAddress(address);
                user.save();
                errorToast("Saved");
                finish();
            }else{
                errorToast("User address field is empty");
            }
        }else{
            errorToast("User name field is empty");
        }
    }
    private void errorToast(String error){
        Toast.makeText(UserConfigurationsActivity.this, error, Toast.LENGTH_SHORT).show();
    }
}