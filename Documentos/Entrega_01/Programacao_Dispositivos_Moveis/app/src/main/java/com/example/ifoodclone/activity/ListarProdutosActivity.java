package com.example.ifoodclone.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.ProdutoAdapter;
import com.example.ifoodclone.activity.Produto;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListarProdutosActivity extends AppCompatActivity {

    private RecyclerView recyclerProdutos;
    private ProdutoAdapter adapter;
    private List<Produto> listaProdutos = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);

        recyclerProdutos = findViewById(R.id.recyclerProdutos);
        recyclerProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutos.setHasFixedSize(true);

        adapter = new ProdutoAdapter(listaProdutos, this);

        recyclerProdutos.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        carregarProdutos();
    }

    private void carregarProdutos() {
        db.collection("produtos")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        listaProdutos.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Produto produto = doc.toObject(Produto.class);
                            produto.setId(doc.getId());
                            listaProdutos.add(produto);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }


}
