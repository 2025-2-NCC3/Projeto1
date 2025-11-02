package com.example.ifoodclone.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.OrderAdapter;
import com.example.ifoodclone.model.Order;
import com.example.ifoodclone.model.OrderDto;
import com.example.ifoodclone.model.OrderItemDto;
import com.example.ifoodclone.net.ApiClient;
import com.example.ifoodclone.net.ApiService;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidosActivity extends AppCompatActivity {

    RecyclerView recyclerPedidos;
    Button btnVoltar;
    OrderAdapter adapter;
    List<Order> dados = new ArrayList<>();
    NumberFormat brl = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_pedidos);

        recyclerPedidos = findViewById(R.id.recyclerPedidos);
        btnVoltar = findViewById(R.id.btnVoltar);

        adapter = new OrderAdapter(dados);
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.setAdapter(adapter);

        btnVoltar.setOnClickListener(v -> finish());

        carregarPedidos();
    }

    private void carregarPedidos() {
        ApiService api = ApiClient.get(this).create(ApiService.class);
        api.getMyOrders().enqueue(new Callback<List<OrderDto>>() {
            @Override public void onResponse(Call<List<OrderDto>> call, Response<List<OrderDto>> resp) {
                if (!resp.isSuccessful() || resp.body()==null) {
                    Toast.makeText(PedidosActivity.this, "Falha ao carregar pedidos", Toast.LENGTH_SHORT).show();
                    return;
                }
                dados.clear();
                for (OrderDto dto : resp.body()) {
                    StringBuilder itens = new StringBuilder();
                    double totalCalc = 0;
                    if (dto.items != null) {
                        for (OrderItemDto it : dto.items) {
                            itens.append(it.quantity).append("x ").append(it.name).append("\n");
                            totalCalc += it.price * it.quantity;
                        }
                    }
                    String totalStr = brl.format(dto.total > 0 ? dto.total : totalCalc);
                    String status = map(dto.status); // mapeia pro texto da sua UI
                    // Nome fixo "Você" (o /orders não traz nome do cliente)
                    dados.add(new Order("Você", itens.toString().trim(), totalStr, status));
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onFailure(Call<List<OrderDto>> call, Throwable t) {
                Toast.makeText(PedidosActivity.this, "Erro de rede", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String map(String s){
        if (s == null) return "Pendente";
        switch (s.toLowerCase()){
            case "completed": return "Recebido";
            case "pending":   return "Pendente";
            default:          return s;
        }
    }
}
