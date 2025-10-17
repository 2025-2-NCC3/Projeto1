package com.example.ifoodclone.activity;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ifoodclone.R;

public class AdicionarProdutoActivity extends AppCompatActivity {

    private Button btnVoltar;
    private ImageView imgLogo;
    private Button btnAdicionarFoto;
    private EditText edtNomeProduto;
    private EditText edtPrecoProduto;
    private Spinner spnCategoria;
    private EditText edtQuantidade;
    private EditText edtDescricao;
    private Button btnConcluir;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_produto);

        // referenciar views
        btnVoltar = findViewById(R.id.btnVoltar);
        imgLogo = findViewById(R.id.imgLogo);
        btnAdicionarFoto = findViewById(R.id.btnAdicionarFoto);
        edtNomeProduto = findViewById(R.id.edtNomeProduto);
        edtPrecoProduto = findViewById(R.id.edtPrecoProduto);
        spnCategoria = findViewById(R.id.spnCategoria);
        edtQuantidade = findViewById(R.id.edtQuantidade);
        edtDescricao = findViewById(R.id.edtDescricao);
        btnConcluir = findViewById(R.id.btnConcluir);

        // eventos
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();  // simplesmente volta
            }
        });

        btnAdicionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: abrir seletor de imagem / câmera
            }
        });

        btnConcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarProduto();
            }
        });

        // Se você quiser inicializar o Spinner (categoria) com valores:
        // ex: ArrayAdapter<String> adapter = new ArrayAdapter<>( this, android.R.layout.simple_spinner_item, listaCategorias );
        // spnCategoria.setAdapter(adapter);
    }

    private void salvarProduto() {
        String nome = edtNomeProduto.getText().toString().trim();
        String precoStr = edtPrecoProduto.getText().toString().trim();
        String categoria = spnCategoria.getSelectedItem() != null
                ? spnCategoria.getSelectedItem().toString()
                : "";
        String quantidadeStr = edtQuantidade.getText().toString().trim();
        String descricao = edtDescricao.getText().toString().trim();

        // Você pode fazer validações:
        if (nome.isEmpty()) {
            edtNomeProduto.setError("Informe o nome");
            edtNomeProduto.requestFocus();
            return;
        }
        if (precoStr.isEmpty()) {
            edtPrecoProduto.setError("Informe o preço");
            edtPrecoProduto.requestFocus();
            return;
        }
        if (quantidadeStr.isEmpty()) {
            edtQuantidade.setError("Informe a quantidade");
            edtQuantidade.requestFocus();
            return;
        }
        if (descricao.isEmpty()) {
            edtDescricao.setError("Informe a descrição");
            edtDescricao.requestFocus();
            return;
        }

        double preco = 0;
        int quantidade = 0;
        try {
            preco = Double.parseDouble(precoStr.replace(",", "."));
        } catch (NumberFormatException e) {
            edtPrecoProduto.setError("Preço inválido");
            edtPrecoProduto.requestFocus();
            return;
        }
        try {
            quantidade = Integer.parseInt(quantidadeStr);
        } catch (NumberFormatException e) {
            edtQuantidade.setError("Quantidade inválida");
            edtQuantidade.requestFocus();
            return;
        }

        // Aqui você pode criar um objeto Produto ou montar os dados para enviar para backend ou banco local
        // Exemplo:
        // Produto produto = new Produto(nome, preco, categoria, quantidade, descricao);
        // produto.setImagemUri(...); se você salvou imagem

        // TODO: lógica de salvar (no banco ou API)

        // Ao concluir, fechar a Activity:
        finish();
    }
}
