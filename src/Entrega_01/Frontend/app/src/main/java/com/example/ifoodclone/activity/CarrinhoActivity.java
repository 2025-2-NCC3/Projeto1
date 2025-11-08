package com.example.ifoodclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ifoodclone.R;
import com.example.ifoodclone.model.ProductDto;
import com.example.ifoodclone.net.ApiClient;
import com.example.ifoodclone.net.ApiService;
import com.example.ifoodclone.util.CartManager;

import java.util.List;

public class CarrinhoActivity extends AppCompatActivity {

    private LinearLayout containerCarrinho;   // <- onde os cards serão adicionados
    private TextView valorTotal;
    private Button btnPagarPix, btnCancelar;

    private List<ProductDto> itensCarrinho;
    private ApiService apiService;

    private double totalCarrinho = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_carrinho);

        // Views principais
        containerCarrinho = findViewById(R.id.containerCarrinho);
        valorTotal       = findViewById(R.id.valorTotal);
        btnPagarPix      = findViewById(R.id.btnPagarPix);
        btnCancelar      = findViewById(R.id.btnCancelar);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        // Carrega itens salvos localmente
        itensCarrinho = CartManager.getItems(this);

        if (itensCarrinho == null || itensCarrinho.isEmpty()) {
            // Mesmo vazio, deixamos a tela carregada para o usuário ver o total = 0
            Toast.makeText(this, "Seu carrinho está vazio.", Toast.LENGTH_SHORT).show();
            atualizarTotal(); // mostra R$ 0,00
        } else {
            preencherCarrinho();
        }

        // Ações
        btnCancelar.setOnClickListener(v -> {
            CartManager.clearCart(CarrinhoActivity.this);
            itensCarrinho.clear();
            preencherCarrinho(); // limpa a lista na UI também
            Toast.makeText(CarrinhoActivity.this, "Carrinho cancelado.", Toast.LENGTH_SHORT).show();
        });

        btnPagarPix.setOnClickListener(v -> enviarPedidoBackend());
    }

    /**
     * Popula a lista de cards com os produtos do carrinho.
     * Usa o containerCarrinho (LinearLayout vertical dentro de um ScrollView).
     */
    private void preencherCarrinho() {
        if (containerCarrinho == null) return;

        containerCarrinho.removeAllViews();
        totalCarrinho = 0.0;

        for (int i = 0; i < itensCarrinho.size(); i++) {
            ProductDto produto = itensCarrinho.get(i);

            // Infla o layout do card individual
            View card = getLayoutInflater().inflate(R.layout.card_carrinho, containerCarrinho, false);

            ImageView imgProduto      = card.findViewById(R.id.imgProdutoCarrinho);
            TextView txtNome          = card.findViewById(R.id.txtNomeProdutoCarrinho);
            TextView txtPreco         = card.findViewById(R.id.txtPrecoProdutoCarrinho);
            TextView txtQuantidade    = card.findViewById(R.id.txtQuantidade);
            ImageButton btnAumentar   = card.findViewById(R.id.btnAumentar);
            ImageButton btnDiminuir   = card.findViewById(R.id.btnDiminuir);
            ImageButton btnRemover    = card.findViewById(R.id.btnRemover);

            // Preenche dados
            Glide.with(this)
                    .load(produto.getImagemUrl())
                    .placeholder(R.drawable.logo)
                    .into(imgProduto);

            txtNome.setText(produto.getNome());
            txtPreco.setText(String.format("R$ %.2f", produto.getPreco()));
            txtQuantidade.setText("x1");

            totalCarrinho += produto.getPreco();

            // Listeners de quantidade
            btnAumentar.setOnClickListener(v -> {
                int q = getQuantidade(txtQuantidade) + 1;
                txtQuantidade.setText("x" + q);
                totalCarrinho += produto.getPreco();
                atualizarTotal();
            });

            btnDiminuir.setOnClickListener(v -> {
                int q = getQuantidade(txtQuantidade);
                if (q > 1) {
                    q--;
                    txtQuantidade.setText("x" + q);
                    totalCarrinho -= produto.getPreco();
                    atualizarTotal();
                }
            });

            // Remover item
            btnRemover.setOnClickListener(v -> {
                int idx = itensCarrinho.indexOf(produto);
                if (idx >= 0) {
                    itensCarrinho.remove(idx);
                    CartManager.removeItem(CarrinhoActivity.this, idx);
                    preencherCarrinho(); // redesenha a lista
                }
            });

            containerCarrinho.addView(card);
        }

        atualizarTotal();
    }

    private int getQuantidade(TextView txtQuantidade) {
        String text = txtQuantidade.getText().toString().replace("x", "").trim();
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private void atualizarTotal() {
        if (valorTotal != null) {
            valorTotal.setText(String.format("R$ %.2f", totalCarrinho));
        }
    }

    /**
     * Envia o pedido ao backend e abre a tela do Pix.
     * (mantido simples; integre OrderDto aqui quando desejar)
     */
    private void enviarPedidoBackend() {
        if (itensCarrinho == null || itensCarrinho.isEmpty()) {
            Toast.makeText(this, "Seu carrinho está vazio.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Pedido enviado! Gerando QR Code...", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, PagamentoPixActivity.class);
        intent.putExtra("valorTotal", totalCarrinho);
        startActivity(intent);
    }
}
