package com.example.ifoodclone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ifoodclone.R;
import com.example.ifoodclone.model.ProductDto;
import com.example.ifoodclone.net.ApiClient;
import com.example.ifoodclone.net.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {

    private EditText editNomeProduto, editPrecoProduto, edtQuantidade, edtDescricao;
    private Spinner spnCategoria;
    private Button btnConcluir, btnVoltar, btnAdicionarFoto;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Inicializa Retrofit
        api = ApiClient.getClient(this).create(ApiService.class);

        // Referências aos elementos do layout
        btnVoltar = findViewById(R.id.btnVoltar);
        btnAdicionarFoto = findViewById(R.id.btnAdicionarFoto);
        editNomeProduto = findViewById(R.id.editNomeProduto);
        editPrecoProduto = findViewById(R.id.editPrecoProduto);
        spnCategoria = findViewById(R.id.spnCategoria);
        edtQuantidade = findViewById(R.id.edtQuantidade);
        edtDescricao = findViewById(R.id.edtDescricao);
        btnConcluir = findViewById(R.id.btnConcluir);

        // Ações dos botões
        btnVoltar.setOnClickListener(v -> finish());

        btnConcluir.setOnClickListener(v -> salvarProduto());

        btnAdicionarFoto.setOnClickListener(v ->
                Toast.makeText(this, "Função de adicionar foto ainda não implementada.", Toast.LENGTH_SHORT).show()
        );
    }

    private void salvarProduto() {
        String nome = editNomeProduto.getText().toString().trim();
        String precoStr = editPrecoProduto.getText().toString().trim();
        String categoria = spnCategoria.getSelectedItem() != null ? spnCategoria.getSelectedItem().toString() : "";
        String quantidadeStr = edtQuantidade.getText().toString().trim();
        String descricao = edtDescricao.getText().toString().trim();

        if (nome.isEmpty() || precoStr.isEmpty() || categoria.isEmpty() || quantidadeStr.isEmpty() || descricao.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        double preco;
        int quantidade;
        try {
            preco = Double.parseDouble(precoStr);
            quantidade = Integer.parseInt(quantidadeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Verifique os valores de preço e quantidade!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cria DTO com os novos campos
        ProductDto produto = new ProductDto(nome, preco, categoria, quantidade, descricao);

        api.addProduto(produto).enqueue(new Callback<ProductDto>() {
            @Override
            public void onResponse(Call<ProductDto> call, Response<ProductDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddProductActivity.this, "Produto adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                    limparCampos();
                } else {
                    Toast.makeText(AddProductActivity.this, "Erro ao adicionar produto!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductDto> call, Throwable t) {
                Toast.makeText(AddProductActivity.this, "Falha na conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void limparCampos() {
        editNomeProduto.setText("");
        editPrecoProduto.setText("");
        edtQuantidade.setText("");
        edtDescricao.setText("");
        spnCategoria.setSelection(0);
    }
}
