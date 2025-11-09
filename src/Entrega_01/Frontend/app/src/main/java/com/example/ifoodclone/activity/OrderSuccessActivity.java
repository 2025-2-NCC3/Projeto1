package com.example.ifoodclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ifoodclone.R;

import java.text.NumberFormat;
import java.util.Locale;

public class OrderSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        // Recebe dados do /checkout (simulado) via Intent
        String orderId   = getIntent().getStringExtra("order_id");
        double total     = getIntent().getDoubleExtra("total", 0d);
        String pickup    = getIntent().getStringExtra("pickup_code");

        // Views
        TextView tvId     = findViewById(R.id.tvOrderId);
        TextView tvTotal  = findViewById(R.id.tvTotal);
        TextView tvPickup = findViewById(R.id.tvPickup);
        Button   btnPedidos = findViewById(R.id.btnVerPedidos);
        Button   btnHome    = findViewById(R.id.btnVoltarHome);

        // Formata total em BRL
        NumberFormat brl = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        // Seta valores
        tvId.setText("Pedido: " + (orderId != null ? orderId : "-"));
        tvTotal.setText("Total: " + brl.format(total));
        tvPickup.setText("Código de retirada: " + (pickup != null ? pickup : "-"));

        // Navegar para lista de pedidos (já existe no seu Manifest: PedidosActivity)
        btnPedidos.setOnClickListener(v -> {
            Intent i = new Intent(OrderSuccessActivity.this, PedidosActivity.class);
            startActivity(i);
            finish();
        });

        // Voltar pra Home (ajuste se quiser outra tela)
        btnHome.setOnClickListener(v -> {
            Intent i = new Intent(OrderSuccessActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });
    }
}
