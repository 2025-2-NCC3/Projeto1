package com.example.ifoodclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.ifoodclone.R;

public class HomeTiaActivity extends AppCompatActivity {

    private ImageButton btnAdicionarProduto, btnPedidos, btnCupons, btnDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tia);


        btnAdicionarProduto = findViewById(R.id.btnAdicionarProduto);
        btnPedidos = findViewById(R.id.btnPedidos);
        btnCupons = findViewById(R.id.btnCupons);
        btnDashboard = findViewById(R.id.btnDashboard);


        btnAdicionarProduto.setOnClickListener(v -> {

            startActivity(new Intent(getApplicationContext(), AddProductActivity.class));
        });

        btnPedidos.setOnClickListener(v -> {

            startActivity(new Intent(getApplicationContext(), PedidosActivity.class));
        });

        btnCupons.setOnClickListener(v -> {

            startActivity(new Intent(getApplicationContext(), CuponsActivity.class));
        });

        btnDashboard.setOnClickListener(v -> {

            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
        });
    }
}
