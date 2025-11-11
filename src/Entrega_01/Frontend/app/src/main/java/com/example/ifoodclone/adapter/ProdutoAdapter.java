package com.example.ifoodclone.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.activity.ListarProdutosActivity;
import com.example.ifoodclone.activity.Produto;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.MyViewHolder> {

    private List<Produto> listaProdutos;
    private Context context;
    private FirebaseFirestore db;

    public ProdutoAdapter(List<Produto> listaProdutos, Context context) {
        this.listaProdutos = listaProdutos;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_produto, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Produto produto = listaProdutos.get(position);

        holder.nome.setText(produto.getNome());
        holder.preco.setText("R$ " + produto.getPreco());


        holder.btnExcluir.setOnClickListener(v -> {
            db.collection("produtos").document(produto.getId())
                    .delete()
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(context, "Produto excluído!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });


        holder.btnEditar.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("Editar Produto");

            View viewDialog = LayoutInflater.from(context).inflate(R.layout.dialog_editar_produto, null);
            dialog.setView(viewDialog);

            EditText editNome = viewDialog.findViewById(R.id.editNomeDialog);
            EditText editPreco = viewDialog.findViewById(R.id.editPrecoDialog);


            editNome.setText(produto.getNome());
            editPreco.setText(String.valueOf(produto.getPreco()));

            dialog.setPositiveButton("Salvar", (dialogInterface, i) -> {
                String novoNome = editNome.getText().toString();
                double novoPreco = Double.parseDouble(editPreco.getText().toString());

                db.collection("produtos").document(produto.getId())
                        .update("nome", novoNome, "preco", novoPreco)
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(context, "Produto atualizado!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(context, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            });

            dialog.setNegativeButton("Cancelar", (dialogInterface, i) -> dialogInterface.dismiss());

            dialog.create().show();
        });
    }

    @Override
    public int getItemCount() {
        return listaProdutos.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nome, preco;
        Button btnEditar, btnExcluir;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.textNomeProduto);
            preco = itemView.findViewById(R.id.textPrecoProduto);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }
}
