package com.example.ifoodclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ifoodclone.R;
import com.example.ifoodclone.helper.FirebaseUserConfiguration;
import com.example.ifoodclone.model.Product;

public class NewProductActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText editName, editDescription, editPrice;
    private Button buttonAdd;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);
        findViewsById();
        toolbarConfig();
        userId = FirebaseUserConfiguration.getUserId();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputValidation(view);
            }
        });
    }
    private void findViewsById(){
        toolbar = findViewById(R.id.toolbar);
        editName = findViewById(R.id.editTextProductName);
        editDescription = findViewById(R.id.editTextProductDescription);
        editPrice = findViewById(R.id.editTextProductPrice);
        buttonAdd = findViewById(R.id.buttonProductAdd);
    }
    private void toolbarConfig(){
        toolbar.setTitle("Add new product");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void inputValidation(View v){
        String name = editName.getText().toString();
        String description = editDescription.getText().toString();
        String price = editPrice.getText().toString();

        if(!name.isEmpty()){
            if(!description.isEmpty()){
                if(!price.isEmpty()){
                    /* Fim da validação dos campos */
                    Product product = new Product();
                    product.setUserId(userId);
                    product.setName(name);
                    product.setDescription(description);
                    product.setPrice(Double.parseDouble(price));
                    product.save();
                    errorToast("Product saved successfully");
                    finish();
                }else{
                    errorToast("Product name field is empty");
                }
            }else{
                errorToast("Product description field is empty");
            }
        }else{
            errorToast("Product price field is empty");
        }

    }
    private void errorToast(String error){
        Toast.makeText(NewProductActivity.this, error, Toast.LENGTH_SHORT).show();
    }
}