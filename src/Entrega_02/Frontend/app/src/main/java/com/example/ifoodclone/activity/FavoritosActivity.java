package com.example.ifoodclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
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
import com.example.ifoodclone.util.FavoriteManager;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritosActivity extends BaseActivity {

    @Override
    protected MenuScreen getCurrentScreen() {
        return MenuScreen.FAVORITOS;
    }

    private RecyclerView recyclerFavoritos;
    private EditText editSearch;

    private ProdutoClienteAdapter adapter;
    /** lista completa de favoritos (sem filtro de busca) */
    private final List<ProductDto> favTodos = new ArrayList<>();
    /** lista visível (após busca) */
    private final List<ProductDto> favVisiveis = new ArrayList<>();

    private ImageView btnHome, btnFavoritos, btnCarrinho, btnHistorico, btnPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favoritos);

        recyclerFavoritos = findViewById(R.id.recyclerFavoritos);
        editSearch        = findViewById(R.id.editSearch);

        btnHome      = findViewById(R.id.btnHome);
        btnFavoritos = findViewById(R.id.btnFavoritos);
        btnCarrinho  = findViewById(R.id.btnCarrinho);
        btnHistorico = findViewById(R.id.btnHistorico);
        btnPerfil    = findViewById(R.id.btnPerfil);

        // Recycler
        recyclerFavoritos.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProdutoClienteAdapter(
                this,
                favVisiveis,
                // abrir detalhes:
                produto -> {
                    Intent it = new Intent(FavoritosActivity.this, DetalheProdutoActivity.class);
                    it.putExtra("produto_nome",      produto.getNome());
                    it.putExtra("produto_preco",     produto.getPreco());
                    it.putExtra("produto_descricao", produto.getDescricao());
                    it.putExtra("produto_imagem",    produto.getImagemUrl());
                    startActivity(it);
                },
                // add carrinho:
                produto -> {
                    CartManager.addItem(this, produto);
                    Toast.makeText(this, "Adicionado ao carrinho!", Toast.LENGTH_SHORT).show();
                },
                // toggle favorito: se remover, some desta tela
                (produto, isFavNow) -> {
                    if (isFavNow) {
                        Toast.makeText(this, "Você favoritou este item", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Você desfavoritou este item", Toast.LENGTH_SHORT).show();
                        // remove das listas locais
                        removeProdutoDasListas(produto);
                    }
                }
        );
        recyclerFavoritos.setAdapter(adapter);

        // Bottom bar
        if (btnHome != null) {
            btnHome.setOnClickListener(v ->
                    startActivity(new Intent(FavoritosActivity.this, MainActivity.class)));
        }
        if (btnCarrinho != null) {
            btnCarrinho.setOnClickListener(v ->
                    startActivity(new Intent(FavoritosActivity.this, CarrinhoActivity.class)));
        }
        if (btnHistorico != null) {
            btnHistorico.setOnClickListener(v ->
                    startActivity(new Intent(FavoritosActivity.this, PedidosActivity.class)));
        }
        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v ->
                    startActivity(new Intent(FavoritosActivity.this, PerfilActivity.class)));
        }

        // Busca local (apenas entre favoritos)
        if (editSearch != null) {
            editSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filtrarBusca(s.toString()); }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        // Carrega favoritos
        carregarFavoritos();
    }

    private void carregarFavoritos() {
        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        api.getProdutos().enqueue(new Callback<List<ProductDto>>() {
            @Override
            public void onResponse(Call<List<ProductDto>> call, Response<List<ProductDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(FavoritosActivity.this, "Falha ao carregar produtos", Toast.LENGTH_SHORT).show();
                    return;
                }

                Set<Integer> idsFav = new HashSet<>(FavoriteManager.getFavoritesSet(FavoritosActivity.this));
                favTodos.clear();
                for (ProductDto p : response.body()) {
                    if (p != null && idsFav.contains(p.getIdAsInt())) {
                        favTodos.add(p);
                    }
                }
                // aplica a busca atual (se houver) sobre os favoritos
                String q = editSearch != null ? editSearch.getText().toString() : "";
                aplicarBusca(q);
            }

            @Override
            public void onFailure(Call<List<ProductDto>> call, Throwable t) {
                Toast.makeText(FavoritosActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrarBusca(String q) {
        aplicarBusca(q);
    }

    private void aplicarBusca(String q) {
        favVisiveis.clear();
        if (favTodos.isEmpty()) {
            adapter.notifyDataSetChanged();
            return;
        }
        String needle = norm(q);
        if (needle.isEmpty()) {
            favVisiveis.addAll(favTodos);
        } else {
            for (ProductDto p : favTodos) {
                String nome = norm(p.getNome());
                String cat  = norm(p.getCategoria());
                if (nome.contains(needle) || cat.contains(needle)) {
                    favVisiveis.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void removeProdutoDasListas(ProductDto produto) {
        // remove do conjunto local e atualiza UI
        for (int i = favTodos.size() - 1; i >= 0; i--) {
            if (favTodos.get(i).getIdAsInt() == produto.getIdAsInt()) {
                favTodos.remove(i);
            }
        }
        for (int i = favVisiveis.size() - 1; i >= 0; i--) {
            if (favVisiveis.get(i).getIdAsInt() == produto.getIdAsInt()) {
                favVisiveis.remove(i);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private static String norm(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return n.toLowerCase(Locale.ROOT).trim();
    }
}
