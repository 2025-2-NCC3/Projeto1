package com.example.ifoodclone.activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.OrderAdapter;
import com.example.ifoodclone.model.OrderDto;
import com.example.ifoodclone.net.ApiClient;
import com.example.ifoodclone.net.ApiService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidosActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_pedidos);

        recycler = findViewById(R.id.recyclerPedidos);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(this);
        recycler.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders(){
        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        api.getMyOrders().enqueue(new Callback<List<OrderDto>>() {
            @Override public void onResponse(Call<List<OrderDto>> call, Response<List<OrderDto>> response) {
                if (response.isSuccessful() && response.body()!=null) {
                    adapter.setList(response.body());
                } else {
                    Toast.makeText(PedidosActivity.this,"Erro ao carregar pedidos",Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<List<OrderDto>> call, Throwable t) {
                Toast.makeText(PedidosActivity.this,"Erro: "+t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
