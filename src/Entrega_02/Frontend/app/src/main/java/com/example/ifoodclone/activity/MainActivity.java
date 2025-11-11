package com.example.ifoodclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
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

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    @Override
    protected MenuScreen getCurrentScreen() {
        return MenuScreen.HOME; // indica qual aba está ativa
    }

    private RecyclerView recyclerProdutos;
    private EditText editSearch;
    private ProdutoClienteAdapter adapter;

    /** lista ligada ao adapter (visível) */
    private final List<ProductDto> listaProdutos = new ArrayList<>();
    /** lista completa (sem filtro) */
    private final List<ProductDto> listaTodos = new ArrayList<>();

    // Bottom bar
    private ImageView btnHome, btnFavoritos, btnCarrinho, btnHistorico, btnPerfil;

    // abas de categoria (se existirem no layout)
    private TextView tabTodos, tabSalgados, tabBebidas, tabMarmitas, tabDoces;
    private TextView[] todasTabs;

    // estado de filtro
    private String catAtual = "todos";
    private String buscaAtual = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ====== Views ======
        recyclerProdutos = findViewById(R.id.recyclerProdutos);
        editSearch = findViewById(R.id.editSearch);

        btnHome      = findViewById(R.id.btnHome);
        btnFavoritos = findViewById(R.id.btnFavoritos);
        btnCarrinho  = findViewById(R.id.btnCarrinho);
        btnHistorico = findViewById(R.id.btnHistorico);
        btnPerfil    = findViewById(R.id.btnPerfil);

        // ====== Abas de categoria ======
        tabTodos    = safeFindText(R.id.tvTodos);     // <-- adicionado
        tabSalgados = safeFindText(R.id.tvSalgados);
        tabBebidas  = safeFindText(R.id.tvBebidas);
        tabMarmitas = safeFindText(R.id.tvMarmitas);
        tabDoces    = safeFindText(R.id.tvDoces);

        todasTabs = new TextView[]{ tabTodos, tabSalgados, tabBebidas, tabMarmitas, tabDoces };

        // ====== Recycler / Adapter ======
        recyclerProdutos.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProdutoClienteAdapter(
                this,
                listaProdutos,
                // Clique em adicionar -> abre tela de detalhes
                produto -> {
                    Intent it = new Intent(MainActivity.this, DetalheProdutoActivity.class);
                    it.putExtra("produto_nome",      produto.getNome());
                    it.putExtra("produto_preco",     produto.getPreco());
                    it.putExtra("produto_descricao", produto.getDescricao());
                    it.putExtra("produto_imagem",    produto.getImagemUrl());
                    startActivity(it);
                },
                // Clique no card -> abre tela de detalhes
                produto -> {
                    Intent it = new Intent(MainActivity.this, DetalheProdutoActivity.class);
                    it.putExtra("produto_nome",      produto.getNome());
                    it.putExtra("produto_preco",     produto.getPreco());
                    it.putExtra("produto_descricao", produto.getDescricao());
                    it.putExtra("produto_imagem",    produto.getImagemUrl());
                    startActivity(it);
                },
                // Favoritar
                (produto, isNowFav) -> {
                    // futuro: filtrar por favoritos
                }
        );
        recyclerProdutos.setAdapter(adapter);

        // ====== Bottom bar actions ======
        if (btnCarrinho != null)
            btnCarrinho.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, CarrinhoActivity.class)));

        if (btnHistorico != null)
            btnHistorico.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, PedidosActivity.class)));

        if (btnHome != null)
            btnHome.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, MainActivity.class)));

        if (btnFavoritos != null)
            btnFavoritos.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, FavoritosActivity.class)));

        if (btnPerfil != null)
            btnPerfil.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, PerfilActivity.class)));

        // ====== Tabs actions ======
        if (tabTodos != null)
            tabTodos.setOnClickListener(v -> selecionarCategoria("todos", tabTodos));
        if (tabSalgados != null)
            tabSalgados.setOnClickListener(v -> selecionarCategoria("salgados", tabSalgados));
        if (tabBebidas != null)
            tabBebidas.setOnClickListener(v -> selecionarCategoria("bebidas", tabBebidas));
        if (tabMarmitas != null)
            tabMarmitas.setOnClickListener(v -> selecionarCategoria("marmitas", tabMarmitas));
        if (tabDoces != null)
            tabDoces.setOnClickListener(v -> selecionarCategoria("doces", tabDoces));

        destaqueAba(tabTodos); // marca "todos" por padrão

        // ====== Busca ======
        if (editSearch != null) {
            editSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                @Override public void afterTextChanged(Editable s) {
                    buscaAtual = s.toString();
                    aplicarFiltros();
                }
            });
        }

        // Carrega produtos do backend
        carregarProdutos();
    }

    private TextView safeFindText(int id) {
        try { return findViewById(id); } catch (Exception e) { return null; }
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
                    aplicarFiltros();
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
        aplicarFiltros();
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

    /** Aplica categoria + texto de busca (nome/descrição/categoria) */
    private void aplicarFiltros() {
        listaProdutos.clear();
        if (listaTodos.isEmpty()) {
            adapter.notifyDataSetChanged();
            return;
        }

        String cat = norm(catAtual);
        String q = norm(buscaAtual);

        for (ProductDto p : listaTodos) {
            boolean okCategoria = cat.equals("todos") || norm(p.getCategoriaSafe()).equals(cat);
            boolean okBusca = q.isEmpty()
                    || norm(p.getNome()).contains(q)
                    || norm(p.getDescricao()).contains(q)
                    || norm(p.getCategoriaSafe()).contains(q);

            if (okCategoria && okBusca) {
                listaProdutos.add(p);
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
