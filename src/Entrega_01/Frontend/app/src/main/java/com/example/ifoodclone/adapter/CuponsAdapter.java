package com.example.ifoodclone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ifoodclone.R;
import com.example.ifoodclone.model.CouponDto;
import java.util.List;

public class CuponsAdapter extends RecyclerView.Adapter<CuponsAdapter.ViewHolder> {

    private List<CouponDto> lista;

    public CuponsAdapter(List<CouponDto> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cupom, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CouponDto cupom = lista.get(position);

        holder.tvCodigo.setText(cupom.getCodigo());
        holder.tvDesconto.setText("Desconto: " + cupom.getDesconto() + "%");
        holder.tvValidade.setText("Válido até: " + cupom.getValidade());

        // Animação leve de entrada
        holder.itemView.setAlpha(0f);
        holder.itemView.animate().alpha(1f).setDuration(500).start();
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCodigo, tvDesconto, tvValidade;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCodigo = itemView.findViewById(R.id.tvCodigoCupom);
            tvDesconto = itemView.findViewById(R.id.tvDescontoCupom);
            tvValidade = itemView.findViewById(R.id.tvValidadeCupom);
            cardView = itemView.findViewById(R.id.cardCupom);
        }
    }
}
