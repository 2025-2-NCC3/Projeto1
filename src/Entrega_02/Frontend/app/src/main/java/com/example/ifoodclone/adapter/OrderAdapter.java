package com.example.ifoodclone.adapter;

import android.content.Context;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ifoodclone.R;
import com.example.ifoodclone.model.OrderDto;
import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.VH> {

    private Context ctx;
    private List<OrderDto> list = new ArrayList<>();

    public OrderAdapter(Context ctx) { this.ctx = ctx; }

    public void setList(List<OrderDto> l) { this.list = l; notifyDataSetChanged(); }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_pedido, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        OrderDto o = list.get(position);
        holder.txtCliente.setText("Você"); // backend não retorna nome no orders (ajuste se retornar)
        holder.txtStatus.setText(o.status != null ? o.status : "Pendente");
        holder.txtTotal.setText(String.format("R$ %.2f", o.total));
        // montar itens em string se precisar
    }

    @Override public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtCliente, txtTotal, txtStatus;
        VH(View v){
            super(v);
            txtCliente = v.findViewById(R.id.tvNome);
            txtTotal = v.findViewById(R.id.tvTotal);
            txtStatus = v.findViewById(R.id.tvStatus);
        }
    }
}
