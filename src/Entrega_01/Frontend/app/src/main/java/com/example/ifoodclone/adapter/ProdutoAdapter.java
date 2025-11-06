package com.example.ifoodclone.adapter;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ifoodclone.R;
import com.example.ifoodclone.model.Product;
import java.util.List;

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.VH> {

    private Context ctx;
    private List<Product> list;

    public ProdutoAdapter(Context ctx, List<Product> list) {
        this.ctx = ctx;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_produto, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Product p = list.get(position);
        holder.txtNome.setText(p.getName());
        holder.txtPreco.setText(String.format("R$ %.2f", p.getPrice()));
        holder.txtDesc.setText(p.getDescription() != null ? p.getDescription() : "");
        // add click listeners for edit/delete if needed
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class VH extends RecyclerView.ViewHolder {
        TextView txtNome, txtPreco, txtDesc;
        public VH(@NonNull View v) {
            super(v);
            txtNome = v.findViewById(R.id.textNomeProduto);
            txtPreco = v.findViewById(R.id.textPrecoProduto);
            txtDesc = v.findViewById(R.id.textDescProduto);
        }
    }
}
