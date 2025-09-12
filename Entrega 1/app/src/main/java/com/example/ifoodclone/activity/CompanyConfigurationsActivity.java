package com.example.ifoodclone.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ifoodclone.R;
import com.example.ifoodclone.helper.FirebaseConfiguration;
import com.example.ifoodclone.helper.FirebaseUserConfiguration;
import com.example.ifoodclone.model.Company;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompanyConfigurationsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText editName, editCategory, editDeliveryTime, editDeliveryTax;
    private CircleImageView imageProfile;
    private Button buttonConfirm;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String urlImage = "";
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_configurations);
        findViewsById();
        toolbarConfig();

        storageReference = FirebaseConfiguration.getStorageReference();
        databaseReference = FirebaseConfiguration.getDatabaseReference();
        recoverCompanyData();

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //if(intent.resolveActivity(getPackageManager()) != null){
                galeryActivityResult.launch(intent);
                //}
            }
        });
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputValidation(view);
            }
        });
    }
    private void findViewsById(){
        toolbar = findViewById(R.id.toolbar);

        imageProfile = findViewById(R.id.imageViewCompanyProfile);
        editName = findViewById(R.id.editTextCompanyName);
        editCategory = findViewById(R.id.editTextCompanyCategory);
        editDeliveryTime = findViewById(R.id.editTextCompanyDeliveryTime);
        editDeliveryTax = findViewById(R.id.editTextCompanyDeliveryTax);
        buttonConfirm = findViewById(R.id.buttonCompanyConfigSave);
        userId = FirebaseUserConfiguration.getUserId();
    }
    private void toolbarConfig(){
        toolbar.setTitle("New Product");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void recoverCompanyData(){
        DatabaseReference companyReference = databaseReference.child("companies").child(userId);
        companyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null){
                    Company company = snapshot.getValue(Company.class);
                    editName.setText(company.getName());
                    editCategory.setText(company.getCategory());
                    editDeliveryTime.setText(company.getDeliveryTime());
                    editDeliveryTax.setText(String.valueOf(company.getTaxPrice()));
                    Glide.with(CompanyConfigurationsActivity.this).load(company.getUrlImage()).into(imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void inputValidation(View v){
        String name = editName.getText().toString();
        String category = editCategory.getText().toString();
        String deliveryTime = editDeliveryTime.getText().toString();
        String deliveryTax = editDeliveryTax.getText().toString();

        if(!name.isEmpty()){
            if(!category.isEmpty()){
                if(!deliveryTime.isEmpty()){
                    if(!deliveryTax.isEmpty()){
                        /* Fim da validação dos campos */
                        Company company = new Company();
                        company.setId(userId);
                        company.setName(name);
                        company.setCategory(category);
                        company.setDeliveryTime(deliveryTime);
                        company.setTaxPrice(Double.parseDouble(deliveryTax));
                        company.setUrlImage(urlImage);
                        company.save();
                        finish();
                    }else{
                        errorToast("Delivery tax field is empty");
                    }
                }else{
                    errorToast("Delivery time field is empty");
                }
            }else{
                errorToast("Category field is empty");
            }
        }else{
            errorToast("Name field is empty");
        }

    }
    private void errorToast(String error){
        Toast.makeText(CompanyConfigurationsActivity.this, error, Toast.LENGTH_SHORT).show();
    }
    private ActivityResultLauncher<Intent> galeryActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bitmap image = null;
                        try {
                            //seleção da galeria de foto
                            Uri imageSelected = result.getData().getData();
                            image = MediaStore.Images.Media.getBitmap(getContentResolver(), imageSelected);

                            if(image != null){
                                //salvar imagem na tela do app
                                imageProfile.setImageBitmap(image);

                                //recuperar dados para salvar a imagem
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                image.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                                byte[] dadosImagem = byteArrayOutputStream.toByteArray();

                                //salvar imagem no storage do firebase
                                StorageReference imageRef = storageReference
                                        .child("images")
                                        .child("company")
                                        .child(userId + ".jpg");

                                UploadTask uploadTask = imageRef.putBytes(dadosImagem);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CompanyConfigurationsActivity.this, "Failure on uploading image", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                /* Recuperar a url da imagem salva */
                                                Uri url = task.getResult();
                                                urlImage = url.toString();
                                            }
                                        });
                                        Toast.makeText(CompanyConfigurationsActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


}