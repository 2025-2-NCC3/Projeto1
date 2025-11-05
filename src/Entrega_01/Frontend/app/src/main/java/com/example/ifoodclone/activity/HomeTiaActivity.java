package com.example.ifoodclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ifoodclone.R;

public class HomeTiaActivity extends AppCompatActivity {
    ImageButton btnAdicionarProduto, btnPedidos, btnCupons, btnDashboard;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_home_tia);

        btnAdicionarProduto = findViewById(R.id.btnAdicionarProduto);
        btnPedidos = findViewById(R.id.btnPedidos);
        btnCupons = findViewById(R.id.btnCupons);
        btnDashboard = findViewById(R.id.btnDashboard);

        btnAdicionarProduto.setOnClickListener(v -> startActivity(new Intent(this, AddProductActivity.class)));
        btnPedidos.setOnClickListener(v -> startActivity(new Intent(this, PedidosActivity.class)));
        btnCupons.setOnClickListener(v -> startActivity(new Intent(this, CuponsActivity.class)));
        btnDashboard.setOnClickListener(v -> startActivity(new Intent(this, ListarProdutosActivity.class)));
    }
}
