package com.example.ifoodclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ifoodclone.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private TextView textViewTitle;
    private EditText editProductName, editProductPrice;
    private Button buttonSaveProduct, buttonVerProdutos;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);


        db = FirebaseFirestore.getInstance();


        textViewTitle = findViewById(R.id.textTitle);
        editProductName = findViewById(R.id.editProductName);
        editProductPrice = findViewById(R.id.editProductPrice);
        buttonSaveProduct = findViewById(R.id.buttonSaveProduct);
        buttonVerProdutos = findViewById(R.id.btnVerProdutos);

        textViewTitle.setText("Tela de Adicionar Produto");


        buttonSaveProduct.setOnClickListener(v -> salvarProduto());


        buttonVerProdutos.setOnClickListener(v -> {
            Intent intent = new Intent(AddProductActivity.this, ListarProdutosActivity.class);
            startActivity(intent);
        });
    }

    private void salvarProduto() {
        String nome = editProductName.getText().toString().trim();
        String precoStr = editProductPrice.getText().toString().trim();

        if (nome.isEmpty() || precoStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        double preco = Double.parseDouble(precoStr);


        Map<String, Object> produto = new HashMap<>();
        produto.put("nome", nome);
        produto.put("preco", preco);


        db.collection("produtos")
                .add(produto)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(this, "Produto adicionado com sucesso!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao salvar produto: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
