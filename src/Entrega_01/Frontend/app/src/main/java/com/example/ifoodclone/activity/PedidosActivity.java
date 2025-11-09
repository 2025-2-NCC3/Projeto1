package com.example.ifoodclone.activity;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidosActivity extends AppCompatActivity {

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
        if (iconFavorite != null) iconFavorite.setOnClickListener(v -> Toast.makeText(this, "Favoritos em breve ;)", Toast.LENGTH_SHORT).show());
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
        api.getMyOrders().enqueue(new Callback<List<OrderDto>>() {
            @Override
            public void onResponse(Call<List<OrderDto>> call, Response<List<OrderDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(PedidosActivity.this, "Não foi possível carregar seus pedidos.", Toast.LENGTH_SHORT).show();
                    return;
                }
                data.clear();
                data.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<OrderDto>> call, Throwable t) {
                Toast.makeText(PedidosActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
