package com.example.ifoodclone.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ifoodclone.R;
import com.example.ifoodclone.model.ProductDto;
import com.example.ifoodclone.util.FavoriteManager;

import java.util.List;

public class ProdutoClienteAdapter extends RecyclerView.Adapter<ProdutoClienteAdapter.VH> {

    // Callbacks
    public interface OnAddToCartListener { void onAdd(ProductDto p); }
    public interface OnOpenDetailsListener { void onOpen(ProductDto p); }
    public interface OnToggleFavoriteListener { void onToggle(ProductDto p, boolean isNowFavorite); }

    private final Context ctx;
    private final List<ProductDto> data;
    private final OnAddToCartListener addListener;
    private final OnOpenDetailsListener openListener;
    private final OnToggleFavoriteListener toggleFavListener;

    public ProdutoClienteAdapter(Context ctx,
                                 List<ProductDto> data,
                                 OnAddToCartListener addListener,
                                 OnOpenDetailsListener openListener,
                                 OnToggleFavoriteListener toggleFavListener) {
        this.ctx = ctx;
        this.data = data;
        this.addListener = addListener;
        this.openListener = openListener;
        this.toggleFavListener = toggleFavListener;
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
            Glide.with(ctx)
                    .load(p.getImagemUrl())
                    .placeholder(R.drawable.logo)
                    .centerCrop()
                    .into(h.imgProduto);
        } else {
            h.imgProduto.setImageResource(R.drawable.logo);
        }

        // Abrir detalhes ao clicar no card ou na imagem
        h.itemView.setOnClickListener(v -> { if (openListener != null) openListener.onOpen(p); });
        h.imgProduto.setOnClickListener(v -> { if (openListener != null) openListener.onOpen(p); });

        // Adicionar ao carrinho
        if (h.imgCarrinho != null) {
            h.imgCarrinho.setOnClickListener(v -> { if (addListener != null) addListener.onAdd(p); });
        }

        // Favoritar / Desfavoritar (se o ImageView existir no layout)
        if (h.imgFavorito != null) {
            final int id = p.getIdAsInt();
            boolean isFav = FavoriteManager.isFavorite(ctx, id);
            paintHeart(h.imgFavorito, isFav);

            h.imgFavorito.setOnClickListener(v -> {
                boolean nowFav;
                if (FavoriteManager.isFavorite(ctx, id)) {
                    FavoriteManager.removeFavorite(ctx, id);
                    nowFav = false;
                    paintHeart(h.imgFavorito, false);
                    Toast.makeText(ctx, "Você desfavoritou este item", Toast.LENGTH_SHORT).show();
                } else {
                    FavoriteManager.addFavorite(ctx, id);
                    nowFav = true;
                    paintHeart(h.imgFavorito, true);
                    Toast.makeText(ctx, "Você favoritou este item", Toast.LENGTH_SHORT).show();
                }
                if (toggleFavListener != null) {
                    toggleFavListener.onToggle(p, nowFav);
                }
            });
        }
    }

    @Override public int getItemCount() { return data.size(); }

    private void paintHeart(ImageView iv, boolean active) {
        // cinza = #7B7B7B | verde = #1B5E20
        iv.setColorFilter(Color.parseColor(active ? "#E53935" : "#7B7B7B"));
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgProduto, imgCarrinho, imgFavorito;
        TextView txtNome, txtPreco;

        VH(@NonNull View v) {
            super(v);
            txtNome     = v.findViewById(getId(v, "txtNomeProduto"));
            txtPreco    = v.findViewById(getId(v, "txtPrecoProduto"));
            imgProduto  = v.findViewById(getId(v, "imgProduto"));
            imgCarrinho = v.findViewById(getId(v, "imgCarrinho"));

            // Busca dinâmica: só existe se o layout tiver o coração com id "imgFavorito"
            int favId = getId(v, "imgFavorito");
            imgFavorito = favId != 0 ? v.findViewById(favId) : null;
        }

        private static int getId(View v, String name) {
            return v.getResources().getIdentifier(name, "id", v.getContext().getPackageName());
        }
    }
}
