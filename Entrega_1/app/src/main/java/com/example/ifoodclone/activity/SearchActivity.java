package com.example.ifoodclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.CompanyAdapter;
import com.example.ifoodclone.helper.FirebaseConfiguration;
import com.example.ifoodclone.model.Company;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SearchView searchView;
    private RecyclerView recyclerSearch;
    private CompanyAdapter companyAdapter;
    private List<Company> companyList, searchCompanyList;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findViewsById();
        toolbarConfig();

        databaseReference = FirebaseConfiguration.getDatabaseReference();
        recoverCompanies();

        recyclerSearch.setLayoutManager(new LinearLayoutManager(this));
        recyclerSearch.setHasFixedSize(true);
        companyAdapter = new CompanyAdapter(searchCompanyList, this);
        recyclerSearch.setAdapter(companyAdapter);

        searchView.setQueryHint("Search company or category");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String searchText = newText.toLowerCase();
                search(searchText);
                return true;
            }
        });

    }
    private void findViewsById(){
        companyList = new ArrayList<>();
        searchCompanyList = new ArrayList<>();
        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.searchView);
        recyclerSearch = findViewById(R.id.recyclerSearch);
    }

    private void toolbarConfig(){
        toolbar.setTitle("iFood - Search");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void recoverCompanies(){
        DatabaseReference companiesReference = databaseReference.child("companies");
        companiesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                companyList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    companyList.add(ds.getValue(Company.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void search(String s){
        searchCompanyList.clear();
        if(s.length() > 2){
            for(Company c : companyList){
                if(c.getName().toLowerCase().contains(s) || c.getCategory().toLowerCase().contains(s)){
                    searchCompanyList.add(c);
                }
            }
            Collections.reverse(searchCompanyList);
            companyAdapter.notifyDataSetChanged();
        }
    }
}