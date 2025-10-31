package com.example.ifoodclone.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.example.ifoodclone.R;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class PedidosStatusActivity extends AppCompatActivity {

    private LinearLayout layoutListaPedidos;
    private Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_status);

        layoutListaPedidos = findViewById(R.id.layoutListaPedidos);
        btnVoltar = findViewById(R.id.btnVoltar);

        btnVoltar.setOnClickListener(v -> finish());

        // Exemplo de pedidos simulados
        List<Pedido> pedidos = new ArrayList<>();
        pedidos.add(new Pedido("Fulana", "1x Pão de queijo  R$5,50\n1x Red Bull  R$8,00", "Recebido", "R$13,50"));
        pedidos.add(new Pedido("Fulana", "1x Pão de queijo  R$5,50\n1x Red Bull  R$8,00", "Pendente", "R$13,50"));
        pedidos.add(new Pedido("Fulana", "1x Pão de queijo  R$5,50\n1x Red Bull  R$8,00", "Recebido", "R$13,50"));

        carregarPedidos(pedidos);
    }

    private void carregarPedidos(List<Pedido> pedidos) {
        layoutListaPedidos.removeAllViews();

        for (Pedido p : pedidos) {
            View card = getLayoutInflater().inflate(R.layout.pedido_status, layoutListaPedidos, false);

            TextView tvNome = card.findViewById(R.id.tvNomeCliente);
            TextView tvItens = card.findViewById(R.id.tvItensPedido);
            TextView tvStatus = card.findViewById(R.id.tvStatusPedido);
            TextView tvValor = card.findViewById(R.id.tvValorPedido);

            tvNome.setText(p.getNomeCliente());
            tvItens.setText(p.getItens());
            tvStatus.setText("Status: " + p.getStatus());
            tvValor.setText(p.getValor());

            // Cor do status
            if (p.getStatus().equalsIgnoreCase("Pendente")) {
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            } else {
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }

            layoutListaPedidos.addView(card);
        }
    }

    private static class Pedido {
        private final String nomeCliente;
        private final String itens;
        private final String status;
        private final String valor;

        public Pedido(String nomeCliente, String itens, String status, String valor) {
            this.nomeCliente = nomeCliente;
            this.itens = itens;
            this.status = status;
            this.valor = valor;
        }

        public String getNomeCliente() { return nomeCliente; }
        public String getItens() { return itens; }
        public String getStatus() { return status; }
        public String getValor() { return valor; }
    }
}
