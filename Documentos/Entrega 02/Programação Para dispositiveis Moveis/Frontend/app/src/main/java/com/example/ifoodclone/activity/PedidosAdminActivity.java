package com.example.ifoodclone.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.model.OrderDto;
import com.example.ifoodclone.model.OrderItemDto;
import com.example.ifoodclone.net.ApiClient;
import com.example.ifoodclone.util.TokenManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class PedidosAdminActivity extends AppCompatActivity {

    private static final int LAYOUT_RES = R.layout.activity_pedidos; // use o nome do XML que você colou

    private RecyclerView recycler;
    private Button btnVoltar;
    private AdminOrdersAdapter adapter;

    private AdminApi api;
    private String bearer;

    private interface AdminApi {
        @GET("/admin/orders")
        Call<List<OrderDto>> getPending(@Header("Authorization") String bearer);

        @PUT("/admin/orders/{order_id}")
        Call<ResponseBody> updateStatus(@Header("Authorization") String bearer,
                                        @Path("order_id") String orderId,
                                        @Body Map<String, String> body);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT_RES);

        recycler  = findViewById(R.id.recyclerPedidos);
        btnVoltar = findViewById(R.id.btnVoltar);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOrdersAdapter(this, new ArrayList<>());
        recycler.setAdapter(adapter);

        Retrofit retrofit = ApiClient.getClient(this);
        api = retrofit.create(AdminApi.class);

        String token = TokenManager.getToken(this);
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Sessão expirada. Faça login.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AuthenticationActivity.class));
            finish();
            return;
        }
        bearer = "Bearer " + token;

        btnVoltar.setOnClickListener(v -> finish());

        carregarPedidos();
    }

    private void carregarPedidos() {
        api.getPending(bearer).enqueue(new Callback<List<OrderDto>>() {
            @Override
            public void onResponse(Call<List<OrderDto>> call, Response<List<OrderDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    String msg = response.code() == 403 ? "Acesso negado. É necessário perfil de admin."
                            : "Não foi possível carregar os pedidos.";
                    Toast.makeText(PedidosAdminActivity.this, msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                adapter.setData(response.body());
            }

            @Override
            public void onFailure(Call<List<OrderDto>> call, Throwable t) {
                Toast.makeText(PedidosAdminActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void concluirPedido(int position, OrderDto order) {
        new AlertDialog.Builder(this)
                .setTitle("Concluir pedido")
                .setMessage("Marcar o pedido #" + order.id + " como concluído?")
                .setPositiveButton("Concluir", (d, w) -> {
                    Map<String, String> body = new HashMap<>();
                    body.put("status", "completed");

                    api.updateStatus(bearer, order.id, body).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                adapter.removeAt(position);
                                Toast.makeText(PedidosAdminActivity.this, "Pedido concluído!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PedidosAdminActivity.this, "Falha ao atualizar o status.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(PedidosAdminActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    private class AdminOrdersAdapter extends RecyclerView.Adapter<AdminOrdersAdapter.VH> {
        private final Context ctx;
        private final List<OrderDto> data;

        AdminOrdersAdapter(Context ctx, List<OrderDto> data) {
            this.ctx = ctx;
            this.data = data;
        }

        void setData(List<OrderDto> list) {
            data.clear();
            if (list != null) data.addAll(list);
            notifyDataSetChanged();
        }

        void removeAt(int pos) {
            if (pos >= 0 && pos < data.size()) {
                data.remove(pos);
                notifyItemRemoved(pos);
            }
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            // Reaproveito o seu item_pedido.xml (o mesmo do histórico do usuário)
            View v = LayoutInflater.from(ctx).inflate(R.layout.item_pedido, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            OrderDto o = data.get(position);

            h.tvNome.setText("Pedido #" + (o.id != null ? o.id : "-"));

            StringBuilder sb = new StringBuilder();
            if (o.items != null) {
                for (OrderItemDto it : o.items) {
                    sb.append(it.quantity).append("x ").append(it.name);
                    if (it.price > 0) {
                        sb.append(" - R$ ").append(String.format("%.2f", it.price));
                    }
                    sb.append("\n");
                }
            }
            h.tvItens.setText(sb.toString().trim());

            String status = (o.status == null) ? "pending" : o.status;
            h.tvStatus.setText("Status: " + ("completed".equalsIgnoreCase(status) ? "Concluído" : "Pendente"));
            h.tvTotal.setText("R$ " + String.format("%.2f", o.total));

            h.itemView.setOnClickListener(v -> {
                if (!"completed".equalsIgnoreCase(status)) {
                    concluirPedido(position, o);
                } else {
                    Toast.makeText(ctx, "Este pedido já está concluído.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class VH extends RecyclerView.ViewHolder {
            final android.widget.ImageView img;
            final android.widget.TextView tvNome;
            final android.widget.TextView tvItens;
            final android.widget.TextView tvStatus;
            final android.widget.TextView tvTotal;

            VH(@NonNull View itemView) {
                super(itemView);
                img     = itemView.findViewById(R.id.imgCliente);
                tvNome  = itemView.findViewById(R.id.tvNome);
                tvItens = itemView.findViewById(R.id.tvItens);
                tvStatus= itemView.findViewById(R.id.tvStatus);
                tvTotal = itemView.findViewById(R.id.tvTotal);

                // imagem opcional
                if (img != null) img.setImageResource(R.drawable.usuario);
            }
        }
    }
}
