package com.example.ifoodclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.model.OrderDto;
import com.example.ifoodclone.model.OrderItemDto;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.PedidoVH> {

    private final Context context;
    private final List<OrderDto> pedidos;
    private final NumberFormat BRL = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));

    public PedidosAdapter(Context context, List<OrderDto> pedidos) {
        this.context = context;
        this.pedidos = pedidos;
    }

    @NonNull
    @Override
    public PedidoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_pedido, parent, false);
        return new PedidoVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoVH h, int position) {
        OrderDto o = pedidos.get(position);

        // Nome exibido (mantendo o visual original)
        h.tvNome.setText("Você");

        // Lista de itens em múltiplas linhas: "QTx Nome"
        StringBuilder itensStr = new StringBuilder();
        List<OrderItemDto> itens = o.getItems();
        if (itens != null && !itens.isEmpty()) {
            for (int i = 0; i < itens.size(); i++) {
                OrderItemDto it = itens.get(i);
                if (it == null) continue;
                if (i > 0) itensStr.append("\n");
                itensStr.append(it.getQuantity())
                        .append("x ")
                        .append(safe(it.getName()));
            }
        } else {
            itensStr.append("Sem itens");
        }
        h.tvItens.setText(itensStr.toString());

        // Status traduzido para PT-BR
        h.tvStatus.setText("Status: " + traduzStatus(o.getStatus()));

        // Total formatado como BRL
        h.tvTotal.setText(BRL.format(o.getTotal()));

        // Avatar padrão
        h.imgCliente.setImageResource(R.drawable.usuario);
    }

    @Override
    public int getItemCount() {
        return pedidos == null ? 0 : pedidos.size();
    }

    static class PedidoVH extends RecyclerView.ViewHolder {
        ImageView imgCliente;
        TextView tvNome, tvItens, tvStatus, tvTotal;

        public PedidoVH(@NonNull View v) {
            super(v);
            imgCliente = v.findViewById(R.id.imgCliente);
            tvNome     = v.findViewById(R.id.tvNome);
            tvItens    = v.findViewById(R.id.tvItens);
            tvStatus   = v.findViewById(R.id.tvStatus);
            tvTotal    = v.findViewById(R.id.tvTotal);
        }
    }

    private String traduzStatus(String s) {
        if (s == null) return "";
        switch (s.toLowerCase(Locale.ROOT)) {
            case "pending":   return "Pendente";
            case "completed": return "Concluído";
            case "canceled":
            case "cancelled": return "Cancelado";
            case "ready":     return "Pronto";
            default:          return s;
        }
    }

    private String safe(String s){ return s == null ? "" : s; }
}
