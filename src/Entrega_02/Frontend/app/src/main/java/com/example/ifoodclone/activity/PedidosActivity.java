package com.example.ifoodclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.PedidosAdapter;
import com.example.ifoodclone.model.OrderDto;
import com.example.ifoodclone.net.ApiClient;
import com.example.ifoodclone.net.ApiService;
import com.example.ifoodclone.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidosActivity extends BaseActivity {


    @Override
    protected MenuScreen getCurrentScreen() {
        return MenuScreen.HISTORICO;
    }

    private RecyclerView recyclerPedidos;
    private PedidosAdapter adapter;
    private final List<OrderDto> data = new ArrayList<>();

    private ImageView iconHome, iconFavorite, iconCart, iconHistory, iconProfile;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedidos_anteriores);

        recyclerPedidos = findViewById(R.id.recyclerPedidos);
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PedidosAdapter(this, data);
        recyclerPedidos.setAdapter(adapter);

        iconHome     = findViewById(R.id.iconHome);
        iconFavorite = findViewById(R.id.iconFavorite);
        iconCart     = findViewById(R.id.iconCart);
        iconHistory  = findViewById(R.id.iconHistory);
        iconProfile  = findViewById(R.id.iconProfile);

        api = ApiClient.getClient(this).create(ApiService.class);

        // bottom menu
        if (iconHome != null)     iconHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        if (iconFavorite != null) iconFavorite.setOnClickListener(v -> startActivity(new Intent(this, FavoritosActivity.class)));
        if (iconCart != null)     iconCart.setOnClickListener(v -> startActivity(new Intent(this, CarrinhoActivity.class)));
        if (iconHistory != null)  iconHistory.setOnClickListener(v -> {}); // já está aqui
        if (iconProfile != null)  iconProfile.setOnClickListener(v -> startActivity(new Intent(this, PerfilActivity.class)));

        // mensagem opcional ao vir do checkout
        String pickup = getIntent().getStringExtra("just_placed_pickup_code");
        if (pickup != null) {
            Toast.makeText(this, "Pedido confirmado! Código de retirada: " + pickup, Toast.LENGTH_LONG).show();
        }

        carregarPedidos();
    }


    private void carregarPedidos() {
        final String TAG = "PedidosActivity";

        SessionManager sm = new SessionManager(this);
        String token = sm.getToken();

        Log.d(TAG, "🔹 Iniciando carregamento de pedidos...");
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "❌ Nenhum token encontrado. Usuário não logado.");
            Toast.makeText(this, "Faça login para ver seus pedidos.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AuthenticationActivity.class));
            return;
        }

        Log.d(TAG, "📤 Enviando requisição GET /myorders com token: " + token.substring(0, Math.min(token.length(), 10)) + "...");

        api.getMyOrders("Bearer " + token).enqueue(new Callback<List<OrderDto>>() {
            @Override
            public void onResponse(Call<List<OrderDto>> call, Response<List<OrderDto>> response) {
                Log.d(TAG, "📥 Resposta recebida do servidor. Código HTTP: " + response.code());

                if (!response.isSuccessful()) {
                    try {
                        String errBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e(TAG, "⚠️ Falha ao carregar pedidos. Código: " + response.code() + " | Corpo: " + errBody);
                    } catch (Exception e) {
                        Log.e(TAG, "⚠️ Falha ao ler corpo de erro: " + e.getMessage());
                    }

                    String msg = (response.code() == 401 || response.code() == 403)
                            ? "Sessão expirada. Entre novamente."
                            : "Não foi possível carregar seus pedidos.";
                    Toast.makeText(PedidosActivity.this, msg, Toast.LENGTH_SHORT).show();
                    return;
                }

                List<OrderDto> pedidos = response.body();
                if (pedidos == null || pedidos.isEmpty()) {
                    Log.d(TAG, "ℹ️ Nenhum pedido retornado do servidor.");
                    Toast.makeText(PedidosActivity.this, "Nenhum pedido encontrado.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d(TAG, "✅ " + pedidos.size() + " pedido(s) recebido(s) do servidor.");
                for (OrderDto pedido : pedidos) {
                    Log.d(TAG, "🧾 Pedido " + pedido.getId() +
                            " | Total: " + pedido.getTotal() +
                            " | Itens: " + (pedido.getItems() != null ? pedido.getItems().size() : 0));
                }

                data.clear();
                data.addAll(pedidos);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<OrderDto>> call, Throwable t) {
                Log.e(TAG, "💥 Erro de rede: " + t.getMessage(), t);
                Toast.makeText(PedidosActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
