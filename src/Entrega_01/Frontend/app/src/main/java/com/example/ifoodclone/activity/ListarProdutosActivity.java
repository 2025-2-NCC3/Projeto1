package com.example.ifoodclone.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.ProdutoAdapter;
import com.example.ifoodclone.model.Product;
import com.example.ifoodclone.net.ApiClient;
import com.example.ifoodclone.net.ApiService;
import com.example.ifoodclone.util.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListarProdutosActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private ProdutoAdapter adapter;
    private final List<Product> data = new ArrayList<>();
    private ApiService api;

    private static final int REQ_EDIT = 101;

    // trava para evitar múltiplos DELETE simultâneos (e spam de toasts)
    private String deletingId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.produtos_cadastrados);

        api = ApiClient.getClient(this).create(ApiService.class);

        recycler = findViewById(R.id.recyclerProdutos);
        recycler.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new ProdutoAdapter(this, data);
        adapter.setOnProdutoClickListener(new ProdutoAdapter.OnProdutoClickListener() {
            @Override
            public void onEditar(Product produto) {
                Intent it = new Intent(ListarProdutosActivity.this, AddProductActivity.class);
                it.putExtra("produto", produto); // Product deve implementar Serializable
                startActivityForResult(it, REQ_EDIT);
            }

            @Override
            public void onExcluir(Product produto) {
                if (deletingId != null) {
                    // já existe uma exclusão em andamento
                    return;
                }
                deletingId = String.valueOf(produto.getId());
                excluirProduto(deletingId);
            }
        });
        recycler.setAdapter(adapter);

        Button btnVoltar = findViewById(R.id.btnVoltar);
        if (btnVoltar != null) btnVoltar.setOnClickListener(v -> finish());

        loadProducts();
    }

    private void loadProducts() {
        api.getProdutosAdminRaw().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.clear();
                    data.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ListarProdutosActivity.this, "Falha ao carregar produtos (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    Log.e("API_DEBUG", "GET /products erro: code=" + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ListarProdutosActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("API_DEBUG", "GET /products fail", t);
            }
        });
    }

    private void excluirProduto(String id) {
        String token = TokenManager.getToken(this);
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Sessão expirada. Faça login como admin.", Toast.LENGTH_LONG).show();
            deletingId = null;
            return;
        }
        String bearer = "Bearer " + token;

        api.deleteProduto(bearer, id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // libera a trava independentemente do resultado
                deletingId = null;

                String bodyStr = "";
                try {
                    bodyStr = response.errorBody() != null ? response.errorBody().string() : "";
                } catch (Exception ignored) {}

                if (response.isSuccessful()) {
                    Toast.makeText(ListarProdutosActivity.this, "Produto excluído com sucesso!", Toast.LENGTH_SHORT).show();
                    loadProducts();
                } else if (response.code() == 409) {
                    // mapeado no backend quando há referência em order_items
                    Log.e("API_DEBUG", "DELETE /admin/product/" + id + " code=409 body=" + bodyStr);
                    Toast.makeText(
                            ListarProdutosActivity.this,
                            "Não é possível excluir: produto já foi usado em pedidos.",
                            Toast.LENGTH_LONG
                    ).show();
                } else if (response.code() == 404) {
                    Toast.makeText(ListarProdutosActivity.this, "Produto não encontrado (já removido).", Toast.LENGTH_SHORT).show();
                    loadProducts();
                } else {
                    Log.e("API_DEBUG", "DELETE /admin/product/" + id + " code=" + response.code() + " body=" + bodyStr);
                    Toast.makeText(ListarProdutosActivity.this, "Erro ao excluir (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                deletingId = null;
                Toast.makeText(ListarProdutosActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("API_DEBUG", "DELETE /admin/product fail", t);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent dataIt) {
        super.onActivityResult(requestCode, resultCode, dataIt);
        if (requestCode == REQ_EDIT && resultCode == RESULT_OK) {
            loadProducts();
        }
    }
}
