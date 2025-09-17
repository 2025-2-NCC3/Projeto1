package com.example.ifoodclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.OrderAdapter;
import com.example.ifoodclone.adapter.ProductAdapter;
import com.example.ifoodclone.helper.FirebaseConfiguration;
import com.example.ifoodclone.helper.FirebaseUserConfiguration;
import com.example.ifoodclone.listener.RecyclerItemClickListener;
import com.example.ifoodclone.model.Order;
import com.example.ifoodclone.model.Product;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerOrders;
    private List<Order> orderList;
    private OrderAdapter orderAdapter;
    private AlertDialog alertDialog;
    private DatabaseReference databaseReference;
    private String idCompany;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        findViewsById();
        toolbarConfig();
        databaseReference = FirebaseConfiguration.getDatabaseReference();
        idCompany = FirebaseUserConfiguration.getUserId();

        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerOrders.setHasFixedSize(true);
        orderAdapter = new OrderAdapter(orderList);
        recyclerOrders.setAdapter(orderAdapter);

        recyclerOrders.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerOrders,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Order order = orderList.get(position);
                        order.setStatus("Delivered");
                        order.updateStatus();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        recoverOrders();
    }
    private void loadingDialog(String title){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        alert.setCancelable(false);
        alert.setView(R.layout.loading);
        alertDialog = alert.create();
        alertDialog.show();
    }

    private void recoverOrders(){
        loadingDialog("Loading");

        DatabaseReference orderReference = databaseReference.child("order").child(idCompany);
        orderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null){
                    orderList.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Order order = dataSnapshot.getValue(Order.class);
                        orderList.add(order);
                    }
                    orderAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void findViewsById(){
        orderList = new ArrayList<>();
        recyclerOrders = findViewById(R.id.recyclerOrders);
        toolbar = findViewById(R.id.toolbar);
    }
    private void toolbarConfig(){
        toolbar.setTitle("Orders");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}