package com.example.ifoodclone.adapter;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.ifoodclone.R;
import com.example.ifoodclone.model.Product;
import java.util.List;

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.VH> {

    public interface OnProdutoClickListener {
        void onEditar(Product produto);
        void onExcluir(Product produto);
    }

    private final Context ctx;
    private final List<Product> list;
    private OnProdutoClickListener listener;

    public ProdutoAdapter(Context ctx, List<Product> list) {
        this.ctx = ctx;
        this.list = list;
    }

    public void setOnProdutoClickListener(OnProdutoClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.produtos_tia_card, parent, false);

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) v.getLayoutParams();
        params.width = parent.getMeasuredWidth() / 2;
        v.setLayoutParams(params);

        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Product p = list.get(position);
        holder.txtNome.setText(p.getName());
        holder.txtPreco.setText(String.format("R$ %.2f", p.getPrice()));

        // Imagem
        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
            Glide.with(ctx)
                    .load("http://10.0.2.2:3000" + p.getImageUrl())
                    .placeholder(R.drawable.logo)
                    .centerCrop()
                    .into(holder.imgProduto);
        } else {
            holder.imgProduto.setImageResource(R.drawable.logo);
        }

        // Clique em editar
        holder.imgCarrinho.setOnClickListener(v -> {
            if (listener != null) listener.onEditar(p);
        });

        // Clique em excluir
        holder.imgLixeira.setOnClickListener(v -> {
            if (listener != null) listener.onExcluir(p);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        ImageView imgProduto, imgCarrinho, imgLixeira;
        TextView txtNome, txtPreco;

        public VH(@NonNull View v) {
            super(v);
            imgProduto = v.findViewById(R.id.imgProduto);
            imgCarrinho = v.findViewById(R.id.imgCarrinho);
            imgLixeira = v.findViewById(R.id.imgLixeira);
            txtNome = v.findViewById(R.id.txtNomeProduto);
            txtPreco = v.findViewById(R.id.txtPrecoProduto);
        }
    }
}
