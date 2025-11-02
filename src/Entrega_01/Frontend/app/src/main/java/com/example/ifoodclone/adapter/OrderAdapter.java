package com.example.ifoodclone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.model.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private final List<Order> orders;

    public OrderAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvNome.setText(order.getName() != null ? order.getName() : "Você");
        holder.tvItens.setText(formatItems(order));
        holder.tvTotal.setText(String.format("R$ %.2f", order.getTotal() != null ? order.getTotal() : 0.0));
        holder.tvStatus.setText("Status: " + (order.getStatus() != null ? order.getStatus() : "Pendente"));

        // muda a cor do status
        if ("Pendente".equalsIgnoreCase(order.getStatus()) || "pending".equalsIgnoreCase(order.getStatus())) {
            holder.tvStatus.setTextColor(0xFFFFC107); // amarelo
        } else {
            holder.tvStatus.setTextColor(0xFF1A3A34); // verde escuro
        }
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvItens, tvTotal, tvStatus;
        ImageView imgCliente;

        MyViewHolder(View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNome);
            tvItens = itemView.findViewById(R.id.tvItens);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            imgCliente = itemView.findViewById(R.id.imgCliente);
        }
    }

    private String formatItems(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < order.getItems().size(); i++) {
            sb.append(order.getItems().get(i).getQuantity())
                    .append("x ")
                    .append(order.getItems().get(i).getProductName());
            if (i < order.getItems().size() - 1) sb.append("\n");
        }
        return sb.toString();
    }
}