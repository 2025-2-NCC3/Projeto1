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

import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.CompanyAdapter;
import com.example.ifoodclone.helper.FirebaseConfiguration;
import com.example.ifoodclone.listener.RecyclerItemClickListener;
import com.example.ifoodclone.model.Company;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerCompanies;
    private CompanyAdapter companyAdapter;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private List<Company> companies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        databaseReference = FirebaseConfiguration.getDatabaseReference();
        firebaseAuth = FirebaseConfiguration.getFirebaseAuth();

        findViewsById();
        toolbarConfig();

        //Configuracoes recyclerview
        recyclerCompanies.setLayoutManager(new LinearLayoutManager(this));
        recyclerCompanies.setHasFixedSize(true);
        companyAdapter = new CompanyAdapter(companies, this);
        recyclerCompanies.setAdapter(companyAdapter);
        recoverCompanies();

        recyclerCompanies.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerCompanies,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Company selectedCompany = companies.get(position);
                        Intent i = new Intent(HomeActivity.this, CompanyMenuActivity.class);
                        i.putExtra("company", selectedCompany);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));
    }

    private void findViewsById(){
        toolbar = findViewById(R.id.toolbar);
        recyclerCompanies = findViewById(R.id.recyclerCompanies);
    }
    private void toolbarConfig(){
        toolbar.setTitle("iFood - User");
        setSupportActionBar(toolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuSearch){
            search();
        }
        if(item.getItemId() == R.id.menuConfigurations){
            configurations();
        }
        if(item.getItemId() == R.id.menuLogout){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }
    private void recoverCompanies(){
        DatabaseReference companyReference = databaseReference.child("companies");
        companyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                companies.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    companies.add(ds.getValue(Company.class));
                }
                companyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void configurations(){
        startActivity(new Intent(HomeActivity.this, UserConfigurationsActivity.class));
    }
    private void search(){
        startActivity(new Intent(HomeActivity.this, SearchActivity.class));
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