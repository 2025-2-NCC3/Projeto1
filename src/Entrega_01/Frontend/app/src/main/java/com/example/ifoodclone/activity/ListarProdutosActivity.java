package com.example.ifoodclone.activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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

    private RecyclerView recycler;
    private ProdutoAdapter adapter;
    private List<Product> lista = new ArrayList<>();

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_produtos);

        recycler = findViewById(R.id.recyclerProdutos);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProdutoAdapter(this, lista);
        recycler.setAdapter(adapter);

        loadProducts();
    }

    private void loadProducts() {
        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        api.getProdutos().enqueue(new Callback<List<ProductDto>>() {
            @Override
            public void onResponse(Call<List<ProductDto>> call, Response<List<ProductDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lista.clear();
                    for (ProductDto dto : response.body()) {
                        String id = dto.getId() != null ? dto.getId() : "";
                        lista.add(new Product(
                                id,
                                dto.getNome(),
                                dto.getPreco(),
                                "", // descrição vazia por enquanto
                                ""  // imagem vazia
                        ));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ListarProdutosActivity.this, "Erro ao carregar produtos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductDto>> call, Throwable t) {
                Toast.makeText(ListarProdutosActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
