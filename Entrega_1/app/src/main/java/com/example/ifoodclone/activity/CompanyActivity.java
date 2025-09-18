package com.example.ifoodclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;


import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.ProductAdapter;
import com.example.ifoodclone.helper.FirebaseConfiguration;
import com.example.ifoodclone.listener.RecyclerItemClickListener;
import com.example.ifoodclone.helper.FirebaseUserConfiguration;
import com.example.ifoodclone.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CompanyActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerProducts;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private String idUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        firebaseAuth = FirebaseConfiguration.getFirebaseAuth();
        databaseReference = FirebaseConfiguration.getDatabaseReference();
        idUser = FirebaseUserConfiguration.getUserId();

        findViewsById();
        toolbarConfig();

        //Configuracao recyclerview
        recyclerProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerProducts.setHasFixedSize(true);
        productAdapter = new ProductAdapter(productList, this);
        recyclerProducts.setAdapter(productAdapter);
        recoverProductData();

        recyclerProducts.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerProducts,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Product productSelected = productList.get(position);
                        productSelected.delete();
                        Toast.makeText(CompanyActivity.this, "Produto excluido", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));
    }

    private void findViewsById(){
        toolbar = findViewById(R.id.toolbar);
        recyclerProducts = findViewById(R.id.recyclerProducts);
    }
    private void toolbarConfig(){
        toolbar.setTitle("iFood - Company");
        setSupportActionBar(toolbar);
    }

    private void recoverProductData(){
        DatabaseReference productReference = databaseReference.child("products").child(idUser);
        productReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    productList.add(ds.getValue(Product.class));
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_company, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuAddProduct){
            addProduct();
        }if(item.getItemId() == R.id.menuOrders){
            orders();
        }
        if(item.getItemId() == R.id.menuConfigurations){
            configurations();
        }
        if(item.getItemId() == R.id.menuLogout){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }
    private void orders(){
        startActivity(new Intent(CompanyActivity.this, OrderActivity.class));
    }
    private void configurations(){
        startActivity(new Intent(CompanyActivity.this, CompanyConfigurationsActivity.class));
    }
    private void addProduct(){
        startActivity(new Intent(CompanyActivity.this, NewProductActivity.class));
    }
    private void logout(){
        try {
            firebaseAuth.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}