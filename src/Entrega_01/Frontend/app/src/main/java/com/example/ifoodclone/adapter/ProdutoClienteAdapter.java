package com.example.ifoodclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ifoodclone.R;
import com.example.ifoodclone.model.ProductDto;

import java.util.List;

public class ProdutoClienteAdapter extends RecyclerView.Adapter<ProdutoClienteAdapter.VH> {

    public interface OnAddToCartListener { void onAdd(ProductDto p); }
    public interface OnOpenDetailsListener { void onOpen(ProductDto p); }

    private final Context ctx;
    private final List<ProductDto> data;
    private final OnAddToCartListener addListener;
    private final OnOpenDetailsListener openListener;

    public ProdutoClienteAdapter(Context ctx,
                                 List<ProductDto> data,
                                 OnAddToCartListener addListener,
                                 OnOpenDetailsListener openListener) {
        this.ctx = ctx;
        this.data = data;
        this.addListener = addListener;
        this.openListener = openListener;
        setHasStableIds(false);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.produtos_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ProductDto p = data.get(position);

        h.txtNome.setText(p.getNome());
        h.txtPreco.setText(String.format("R$ %.2f", p.getPreco()));

        if (p.getImagemUrl() != null && !p.getImagemUrl().isEmpty()) {
            // ajuste se precisar prefixar seu host
            Glide.with(ctx).load(p.getImagemUrl()).placeholder(R.drawable.logo).centerCrop().into(h.imgProduto);
        } else {
            h.imgProduto.setImageResource(R.drawable.logo);
        }

        // abre detalhes tocando no card inteiro OU na imagem
        h.itemView.setOnClickListener(v -> { if (openListener != null) openListener.onOpen(p); });
        h.imgProduto.setOnClickListener(v -> { if (openListener != null) openListener.onOpen(p); });

        // adiciona ao carrinho
        h.imgCarrinho.setOnClickListener(v -> { if (addListener != null) addListener.onAdd(p); });
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgProduto, imgCarrinho;
        TextView txtNome, txtPreco;
        VH(@NonNull View v) {
            super(v);
            imgProduto  = v.findViewById(R.id.imgProduto);
            imgCarrinho = v.findViewById(R.id.imgCarrinho);
            txtNome     = v.findViewById(R.id.txtNomeProduto);
            txtPreco    = v.findViewById(R.id.txtPrecoProduto);
        }
    }
}
