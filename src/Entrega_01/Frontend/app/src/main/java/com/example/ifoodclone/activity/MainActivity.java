package com.example.ifoodclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.ProdutoClienteAdapter;
import com.example.ifoodclone.model.ProductDto;
import com.example.ifoodclone.net.ApiClient;
import com.example.ifoodclone.net.ApiService;
import com.example.ifoodclone.util.CartManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerProdutos;
    private ProdutoClienteAdapter adapter;
    private final List<ProductDto> listaProdutos = new ArrayList<>();
    private ImageView btnCarrinho;
    private ImageView btnPerfil; // <- adicionado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1) Vincula as views do layout
        recyclerProdutos = findViewById(R.id.recyclerProdutos);
        btnCarrinho      = findViewById(R.id.btnCarrinho);
        btnPerfil        = findViewById(R.id.btnPerfil); // <- adicionado

        // (Opcional) Proteção extra de debug
        if (recyclerProdutos == null) {
            throw new IllegalStateException("activity_main.xml precisa ter RecyclerView com id @id/recyclerProdutos");
        }
        if (btnCarrinho == null) {
            throw new IllegalStateException("activity_main.xml precisa ter ImageView com id @id/btnCarrinho");
        }
        if (btnPerfil == null) {
            throw new IllegalStateException("activity_main.xml precisa ter ImageView com id @id/btnPerfil");
        }

        // 2) LayoutManager
        recyclerProdutos.setLayoutManager(new GridLayoutManager(this, 2));

        // 3) Adapter com os listeners (abrir detalhe e adicionar ao carrinho)
        adapter = new ProdutoClienteAdapter(
                this,
                listaProdutos,
                // openListener -> ABRIR DETALHES
                produto -> {
                    Intent it = new Intent(MainActivity.this, DetalheProdutoActivity.class);
                    it.putExtra("produto_nome",      produto.getNome());
                    it.putExtra("produto_preco",     produto.getPreco());
                    it.putExtra("produto_descricao", produto.getDescricao());
                    it.putExtra("produto_imagem",    produto.getImagemUrl());
                    startActivity(it);
                },
                // addListener -> ADICIONAR AO CARRINHO
                produto -> {
                    CartManager.addItem(this, produto);
                    Toast.makeText(this, "Adicionado ao carrinho!", Toast.LENGTH_SHORT).show();
                }
        );
        recyclerProdutos.setAdapter(adapter);

        // 4) Botão do carrinho
        btnCarrinho.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CarrinhoActivity.class))
        );

        // 4.1) Botão do perfil
        btnPerfil.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, PerfilActivity.class))
        );

        // 5) Carregar produtos da API
        carregarProdutos();
    }

    private void carregarProdutos() {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Call<List<ProductDto>> call = apiService.getProdutos();

        call.enqueue(new Callback<List<ProductDto>>() {
            @Override
            public void onResponse(Call<List<ProductDto>> call, Response<List<ProductDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaProdutos.clear();
                    listaProdutos.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Falha ao carregar produtos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductDto>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
