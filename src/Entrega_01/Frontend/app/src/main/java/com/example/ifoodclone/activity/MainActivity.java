package com.example.ifoodclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerProdutos;
    private ProdutoClienteAdapter adapter;

    /** lista ligada ao adapter (visível) */
    private final List<ProductDto> listaProdutos = new ArrayList<>();
    /** lista completa (sem filtro) */
    private final List<ProductDto> listaTodos = new ArrayList<>();

    // Bottom bar (suporta ids btnX ou iconX)
    private ImageView btnHome, btnFavoritos, btnCarrinho, btnHistorico, btnPerfil;

    // Abas de categoria
    private TextView tabTodos, tabSalgados, tabBebidas, tabMarmitas, tabDoces;
    private TextView[] todasTabs;

    // Categoria atual
    private String catAtual = "todos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ====== Views ======
        recyclerProdutos = findViewById(R.id.recyclerProdutos);

        // bottom bar
        btnHome      = bindImageView(R.id.btnHome, R.id.iconHome);
        btnFavoritos = bindImageView(R.id.btnFavoritos, R.id.iconFavorite);
        btnCarrinho  = bindImageView(R.id.btnCarrinho, R.id.iconCart);
        btnHistorico = bindImageView(R.id.btnHistorico, R.id.iconHistory);
        btnPerfil    = bindImageView(R.id.btnPerfil, R.id.iconProfile);


        // ====== Recycler ======
        recyclerProdutos.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProdutoClienteAdapter(
                this,
                listaProdutos,
                produto -> {
                    Intent it = new Intent(MainActivity.this, DetalheProdutoActivity.class);
                    it.putExtra("produto_nome",      produto.getNome());
                    it.putExtra("produto_preco",     produto.getPreco());
                    it.putExtra("produto_descricao", produto.getDescricao());
                    it.putExtra("produto_imagem",    produto.getImagemUrl());
                    startActivity(it);
                },
                produto -> {
                    CartManager.addItem(this, produto);
                    Toast.makeText(this, "Adicionado ao carrinho!", Toast.LENGTH_SHORT).show();
                }
        );
        recyclerProdutos.setAdapter(adapter);

        // ====== Bottom bar actions ======
        if (btnCarrinho != null) {
            btnCarrinho.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, CarrinhoActivity.class)));
        }
        if (btnHistorico != null) {
            btnHistorico.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, PedidosActivity.class)));
        }
        if (btnHome != null) {
            btnHome.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, MainActivity.class)));
        }
        if (btnFavoritos != null) {
            btnFavoritos.setOnClickListener(v ->
                    Toast.makeText(MainActivity.this, "Favoritos em breve ;)", Toast.LENGTH_SHORT).show());
        }
        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, PerfilActivity.class)));
        }

        // ====== Tabs actions ======
        if (tabTodos != null)    tabTodos.setOnClickListener(v -> selecionarCategoria("todos", tabTodos));
        if (tabSalgados != null) tabSalgados.setOnClickListener(v -> selecionarCategoria("salgados", tabSalgados));
        if (tabBebidas != null)  tabBebidas.setOnClickListener(v -> selecionarCategoria("bebidas", tabBebidas));
        if (tabMarmitas != null) tabMarmitas.setOnClickListener(v -> selecionarCategoria("marmitas", tabMarmitas));
        if (tabDoces != null)    tabDoces.setOnClickListener(v -> selecionarCategoria("doces", tabDoces));

        // Deixa "Todos" selecionado visualmente
        destaqueAba(tabTodos);

        // Carrega produtos
        carregarProdutos();
    }

    private ImageView bindImageView(int... ids) {
        for (int id : ids) {
            try {
                ImageView v = findViewById(id);
                if (v != null) return v;
            } catch (Exception ignored) {}
        }
        return null;
    }

    private TextView bindText(int id) {
        try {
            return findViewById(id);
        } catch (Exception ignored) {
            return null;
        }
    }

    private void carregarProdutos() {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Call<List<ProductDto>> call = apiService.getProdutos();

        call.enqueue(new Callback<List<ProductDto>>() {
            @Override
            public void onResponse(Call<List<ProductDto>> call, Response<List<ProductDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaTodos.clear();
                    listaTodos.addAll(response.body());
                    aplicarFiltro(catAtual);
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

    private void selecionarCategoria(String categoria, TextView aba) {
        catAtual = categoria;
        destaqueAba(aba);
        aplicarFiltro(categoria);
    }

    private void destaqueAba(TextView selecionada) {
        if (todasTabs == null) return;
        for (TextView t : todasTabs) {
            if (t == null) continue;
            t.setAlpha(0.55f);
            t.setTextColor(getColor(android.R.color.black));
            t.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
        if (selecionada != null) {
            selecionada.setAlpha(1.0f);
            selecionada.setTextColor(getColor(R.color.colorOliveGreen));
            selecionada.setTypeface(null, android.graphics.Typeface.BOLD);
        }
    }

    private void aplicarFiltro(String categoria) {
        listaProdutos.clear();
        if (listaTodos.isEmpty()) {
            adapter.notifyDataSetChanged();
            return;
        }

        String cat = norm(categoria);
        if (cat.equals("todos")) {
            listaProdutos.addAll(listaTodos);
        } else {
            for (ProductDto p : listaTodos) {
                String c = norm(p.getCategoria());
                // contém para tolerar variações (ex.: "Bebida" vs "Bebidas")
                if (c.contains(cat)) {
                    listaProdutos.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private String norm(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return n.toLowerCase(Locale.ROOT).trim();
    }
}
