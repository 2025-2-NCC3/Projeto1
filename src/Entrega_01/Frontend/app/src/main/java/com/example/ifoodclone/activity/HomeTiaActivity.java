package com.example.ifoodclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ifoodclone.R;
import com.example.ifoodclone.util.TokenManager;
import com.example.ifoodclone.util.SessionManager;

public class HomeTiaActivity extends AppCompatActivity {
    ImageButton btnAdicionarProduto, btnPedidos, btnCupons, btnDashboard;
    Button btnSair;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_home_tia);

        btnAdicionarProduto = findViewById(R.id.btnAdicionarProduto);
        btnPedidos          = findViewById(R.id.btnPedidos);
        btnCupons           = findViewById(R.id.btnCupons);
        btnDashboard        = findViewById(R.id.btnDashboard);
        btnSair             = findViewById(R.id.btnSair);

        btnAdicionarProduto.setOnClickListener(v ->
                startActivity(new Intent(this, AddProductActivity.class)));

        btnPedidos.setOnClickListener(v ->
                startActivity(new Intent(this, PedidosAdminActivity.class)));

        btnCupons.setOnClickListener(v ->
                startActivity(new Intent(this, CuponsActivity.class)));

        btnDashboard.setOnClickListener(v ->
                startActivity(new Intent(this, ListarProdutosActivity.class)));

        // === Botão SAIR ===
        btnSair.setOnClickListener(v -> {
            // limpa sessão/token (compat com os dois gerenciadores que você usa no app)
            try { TokenManager.clear(this); } catch (Throwable ignored) {}
            try { new SessionManager(this).logout(); } catch (Throwable ignored) {}

            // volta para a tela de autenticação limpando a pilha
            Intent i = new Intent(this, AuthenticationActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }
}
