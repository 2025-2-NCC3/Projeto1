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
import com.example.ifoodclone.model.CheckoutItem;
import com.example.ifoodclone.model.CheckoutRequest;
import com.example.ifoodclone.model.CheckoutResponse;
import com.example.ifoodclone.model.ProductDto;
import com.example.ifoodclone.model.UserDto;
import com.example.ifoodclone.net.ApiClient;
import com.example.ifoodclone.net.ApiService;
import com.example.ifoodclone.util.CartManager;
import com.example.ifoodclone.util.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarrinhoActivity extends AppCompatActivity {

    private LinearLayout containerCarrinho;
    private TextView valorTotal;
    private Button btnPagarPix, btnCancelar;

    private List<ProductDto> itensCarrinho;
    private ApiService apiService;

    private double totalCarrinho = 0.0;

    private ImageView btnHome, btnFavoritos, btnCarrinho, btnHistorico, btnPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_carrinho);

        containerCarrinho = findViewById(R.id.containerCarrinho);
        valorTotal        = findViewById(R.id.valorTotal);
        btnPagarPix       = findViewById(R.id.btnPagarPix);
        btnCancelar       = findViewById(R.id.btnCancelar);

        btnHome      = findViewById(R.id.btnHome);
        btnFavoritos = findViewById(R.id.btnFavoritos);
        btnCarrinho  = findViewById(R.id.btnCarrinho);
        btnHistorico = findViewById(R.id.btnHistorico);
        btnPerfil    = findViewById(R.id.btnPerfil);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        itensCarrinho = CartManager.getItems(this);

        if (itensCarrinho == null || itensCarrinho.isEmpty()) {
            Toast.makeText(this, "Seu carrinho está vazio.", Toast.LENGTH_SHORT).show();
            atualizarTotal();
        } else {
            preencherCarrinho();
        }

        btnCancelar.setOnClickListener(v -> {
            CartManager.clearCart(CarrinhoActivity.this);
            if (itensCarrinho != null) itensCarrinho.clear();
            preencherCarrinho();
            Toast.makeText(CarrinhoActivity.this, "Carrinho cancelado.", Toast.LENGTH_SHORT).show();
        });

        btnPagarPix.setOnClickListener(v -> enviarPedidoBackend());

        if (btnHome != null) {
            btnHome.setOnClickListener(v ->
                    startActivity(new Intent(CarrinhoActivity.this, MainActivity.class)));
        }
        if (btnFavoritos != null) {
            btnFavoritos.setOnClickListener(v ->
                    Toast.makeText(CarrinhoActivity.this, "Favoritos em breve ;)", Toast.LENGTH_SHORT).show());
        }
        if (btnCarrinho != null) {
            btnCarrinho.setOnClickListener(v ->
                    Toast.makeText(CarrinhoActivity.this, "Você já está no carrinho", Toast.LENGTH_SHORT).show());
        }
        if (btnHistorico != null) {
            btnHistorico.setOnClickListener(v ->
                    startActivity(new Intent(CarrinhoActivity.this, PedidosActivity.class)));
        }
        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v ->
                    startActivity(new Intent(CarrinhoActivity.this, PerfilActivity.class)));
        }
    }

    private void preencherCarrinho() {
        if (containerCarrinho == null) return;

        containerCarrinho.removeAllViews();
        totalCarrinho = 0.0;

        if (itensCarrinho == null) return;

        for (int i = 0; i < itensCarrinho.size(); i++) {
            ProductDto produto = itensCarrinho.get(i);

            View card = getLayoutInflater().inflate(R.layout.card_carrinho, containerCarrinho, false);

            ImageView imgProduto      = card.findViewById(R.id.imgProdutoCarrinho);
            TextView txtNome          = card.findViewById(R.id.txtNomeProdutoCarrinho);
            TextView txtPreco         = card.findViewById(R.id.txtPrecoProdutoCarrinho);
            TextView txtQuantidade    = card.findViewById(R.id.txtQuantidade);
            ImageButton btnAumentar   = card.findViewById(R.id.btnAumentar);
            ImageButton btnDiminuir   = card.findViewById(R.id.btnDiminuir);
            ImageButton btnRemover    = card.findViewById(R.id.btnRemover);

            Glide.with(this)
                    .load(produto.getImagemUrl())
                    .placeholder(R.drawable.logo)
                    .into(imgProduto);

            txtNome.setText(produto.getNome());
            txtPreco.setText(String.format("R$ %.2f", produto.getPreco()));
            txtQuantidade.setText("x1");

            totalCarrinho += produto.getPreco();

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

            btnRemover.setOnClickListener(v -> {
                int idx = itensCarrinho.indexOf(produto);
                if (idx >= 0) {
                    itensCarrinho.remove(idx);
                    CartManager.removeItem(CarrinhoActivity.this, idx);
                    preencherCarrinho();
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

    private void enviarPedidoBackend() {
        if (itensCarrinho == null || itensCarrinho.isEmpty()) {
            Toast.makeText(this, "Seu carrinho está vazio.", Toast.LENGTH_SHORT).show();
            return;
        }

        SessionManager sm = new SessionManager(this);
        String token = sm.getToken();
        int userId = sm.getUserId();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Você precisa estar logado para finalizar o pedido.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AuthenticationActivity.class));
            return;
        }

        if (userId <= 0) {
            btnPagarPix.setEnabled(false);
            btnPagarPix.setText("Sincronizando...");
            apiService.getMeuPerfil("Bearer " + token).enqueue(new Callback<UserDto>() {
                @Override
                public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        btnPagarPix.setEnabled(true);
                        btnPagarPix.setText("Pagar (Simulado)");
                        Toast.makeText(CarrinhoActivity.this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CarrinhoActivity.this, AuthenticationActivity.class));
                        return;
                    }
                    UserDto u = response.body();
                    new SessionManager(CarrinhoActivity.this).saveLogin(token, u.getId());
                    continuarCheckout(u.getId());
                }

                @Override
                public void onFailure(Call<UserDto> call, Throwable t) {
                    btnPagarPix.setEnabled(true);
                    btnPagarPix.setText("Pagar (Simulado)");
                    Toast.makeText(CarrinhoActivity.this, "Erro ao validar sessão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        continuarCheckout(userId);
    }

    private void continuarCheckout(int userId) {
        List<CheckoutItem> direct = tryBuildCheckoutItems();
        if (direct != null) {
            fazerCheckout(userId, direct);
            return;
        }

        btnPagarPix.setEnabled(false);
        btnPagarPix.setText("Sincronizando...");

        apiService.getProdutos().enqueue(new Callback<List<ProductDto>>() {
            @Override
            public void onResponse(Call<List<ProductDto>> call, Response<List<ProductDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    btnPagarPix.setEnabled(true);
                    btnPagarPix.setText("Pagar (Simulado)");
                    Toast.makeText(CarrinhoActivity.this, "Falha ao sincronizar produtos.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Integer> idPorNomePreco = new HashMap<>();
                for (ProductDto p : response.body()) {
                    if (p != null && p.getIdAsInt() > 0) {
                        idPorNomePreco.put(keyNomePreco(p.getNome(), p.getPreco()), p.getIdAsInt());
                    }
                }

                for (ProductDto p : itensCarrinho) {
                    if (p == null) continue;
                    if (p.getIdAsInt() <= 0) {
                        Integer id = idPorNomePreco.get(keyNomePreco(p.getNome(), p.getPreco()));
                        if (id != null && id > 0) p.setId(id);
                    }
                }

                List<CheckoutItem> items = tryBuildCheckoutItems();
                if (items == null) {
                    btnPagarPix.setEnabled(true);
                    btnPagarPix.setText("Pagar (Simulado)");
                    Toast.makeText(CarrinhoActivity.this, "ID de produto inválido.", Toast.LENGTH_SHORT).show();
                    return;
                }

                fazerCheckout(userId, items);
            }

            @Override
            public void onFailure(Call<List<ProductDto>> call, Throwable t) {
                btnPagarPix.setEnabled(true);
                btnPagarPix.setText("Pagar (Simulado)");
                Toast.makeText(CarrinhoActivity.this, "Erro ao sincronizar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<CheckoutItem> tryBuildCheckoutItems() {
        List<CheckoutItem> checkoutItems = new ArrayList<>();

        for (int i = 0; i < itensCarrinho.size(); i++) {
            ProductDto p = itensCarrinho.get(i);
            if (p == null) return null;

            View card = containerCarrinho.getChildAt(i);
            int q = 1;
            if (card != null) {
                TextView txtQuantidade = card.findViewById(R.id.txtQuantidade);
                if (txtQuantidade != null) q = getQuantidade(txtQuantidade);
            }

            int productId = p.getIdAsInt();
            if (productId <= 0) return null;

            checkoutItems.add(new CheckoutItem(productId, q));
        }

        return checkoutItems;
    }

    private String keyNomePreco(String nome, double preco) {
        return (nome == null ? "" : nome.trim()) + "|" + preco;
    }

    private void fazerCheckout(int userId, List<CheckoutItem> checkoutItems) {
        CheckoutRequest body = new CheckoutRequest(userId, checkoutItems);

        btnPagarPix.setEnabled(false);
        btnPagarPix.setText("Processando...");

        apiService.checkout(body).enqueue(new Callback<CheckoutResponse>() {
            @Override
            public void onResponse(Call<CheckoutResponse> call, Response<CheckoutResponse> response) {
                btnPagarPix.setEnabled(true);
                btnPagarPix.setText("Pagar (Simulado)");

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(CarrinhoActivity.this, "Falha no checkout. Tente novamente.", Toast.LENGTH_SHORT).show();
                    return;
                }

                CheckoutResponse res = response.body();

                CartManager.clearCart(CarrinhoActivity.this);
                if (itensCarrinho != null) itensCarrinho.clear();
                preencherCarrinho();

                Intent intent = new Intent(CarrinhoActivity.this, OrderSuccessActivity.class);
                intent.putExtra("order_id", res.order_id);
                intent.putExtra("total", res.total);
                intent.putExtra("pickup_code", res.pickup_code);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<CheckoutResponse> call, Throwable t) {
                btnPagarPix.setEnabled(true);
                btnPagarPix.setText("Pagar (Simulado)");
                Toast.makeText(CarrinhoActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
