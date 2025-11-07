package com.example.ifoodclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.ProdutoAdapter;
import com.example.ifoodclone.model.Product;
import com.example.ifoodclone.model.ProductDto;
import com.example.ifoodclone.net.ApiClient;
import com.example.ifoodclone.net.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListarProdutosActivity extends AppCompatActivity {

    private static final int REQ_EDITAR_PRODUTO = 101;

    private RecyclerView recyclerProdutos;
    private ProdutoAdapter adapter;
    private List<Product> listaProdutos = new ArrayList<>();
    private List<Product> listaFiltrada = new ArrayList<>();
    private EditText editSearch;
    private Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.produtos_cadastrados);

        recyclerProdutos = findViewById(R.id.recyclerProdutos);
        editSearch = findViewById(R.id.editSearch);
        btnVoltar = findViewById(R.id.btnVoltar);

        // Configuração do RecyclerView com layout em grade 2x2
        recyclerProdutos.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProdutoAdapter(this, listaFiltrada);
        recyclerProdutos.setAdapter(adapter);

        adapter.setOnProdutoClickListener(new ProdutoAdapter.OnProdutoClickListener() {
            @Override
            public void onEditar(Product produto) {
                // ✅ Abre AddProductActivity para editar produto
                Intent intent = new Intent(ListarProdutosActivity.this, AddProductActivity.class);
                intent.putExtra("produto", produto); // Envia o produto
                startActivityForResult(intent, REQ_EDITAR_PRODUTO);
            }

            @Override
            public void onExcluir(Product produto) {
                excluirProduto(produto.getId());
            }
        });

        btnVoltar.setOnClickListener(v -> finish());

        // Carrega produtos da API
        loadProducts();

        // Filtro de busca
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarProdutos(s.toString());
            }
        });
    }

    private void loadProducts() {
        ApiService api = ApiClient.getClient(this).create(ApiService.class);

        api.getProdutos().enqueue(new Callback<List<ProductDto>>() {
            @Override
            public void onResponse(Call<List<ProductDto>> call, Response<List<ProductDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaProdutos.clear();
                    for (ProductDto dto : response.body()) {
                        listaProdutos.add(new Product(
                                dto.getId() != null ? dto.getId() : "",
                                dto.getNome(),
                                dto.getPreco(),
                                dto.getDescricao() != null ? dto.getDescricao() : "",
                                dto.getImagemUrl() != null ? dto.getImagemUrl() : ""
                        ));
                    }

                    listaFiltrada.clear();
                    listaFiltrada.addAll(listaProdutos);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ListarProdutosActivity.this, "Erro ao carregar produtos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductDto>> call, Throwable t) {
                Toast.makeText(ListarProdutosActivity.this, "Falha na requisição: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void excluirProduto(String id) {
        ApiService api = ApiClient.getClient(this).create(ApiService.class);

        api.deleteProduto(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ListarProdutosActivity.this, "Produto excluído com sucesso!", Toast.LENGTH_SHORT).show();
                    loadProducts();
                } else {
                    Toast.makeText(ListarProdutosActivity.this, "Erro ao excluir produto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ListarProdutosActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrarProdutos(String texto) {
        listaFiltrada.clear();
        if (texto.isEmpty()) {
            listaFiltrada.addAll(listaProdutos);
        } else {
            for (Product p : listaProdutos) {
                if (p.getName().toLowerCase().contains(texto.toLowerCase())) {
                    listaFiltrada.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    // 🔄 Quando AddProductActivity terminar (edição concluída), atualiza lista
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_EDITAR_PRODUTO && resultCode == RESULT_OK) {
            loadProducts(); // Recarrega os produtos atualizados
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }
}
