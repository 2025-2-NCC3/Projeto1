package com.example.ifoodclone.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ifoodclone.R;
import com.example.ifoodclone.model.ProductDto;
import com.example.ifoodclone.util.CartManager;

public class DetalheProdutoActivity extends AppCompatActivity {

    private ImageView imgProdutoDetalhe;
    private TextView txtNomeDetalhe, txtPrecoDetalhe, txtDescricaoDetalhe;
    private Button btnAdicionarCarrinho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_produto);

        imgProdutoDetalhe   = findViewById(R.id.imgProdutoDetalhe);
        txtNomeDetalhe      = findViewById(R.id.txtNomeDetalhe);
        txtPrecoDetalhe     = findViewById(R.id.txtPrecoDetalhe);
        txtDescricaoDetalhe = findViewById(R.id.txtDescricaoDetalhe);
        btnAdicionarCarrinho= findViewById(R.id.btnAdicionarCarrinho);

        String nome      = getIntent().getStringExtra("produto_nome");
        double preco     = getIntent().getDoubleExtra("produto_preco", 0.0);
        String descricao = getIntent().getStringExtra("produto_descricao");
        String imagem    = getIntent().getStringExtra("produto_imagem");

        txtNomeDetalhe.setText(nome != null ? nome : "");
        txtPrecoDetalhe.setText(String.format("R$ %.2f", preco));
        txtDescricaoDetalhe.setText(descricao != null ? descricao : "");

        if (imagem != null && !imagem.isEmpty()) {
            Glide.with(this).load(imagem).placeholder(R.drawable.logo).into(imgProdutoDetalhe);
        } else {
            imgProdutoDetalhe.setImageResource(R.drawable.logo);
        }

        btnAdicionarCarrinho.setOnClickListener(v -> {
            ProductDto p = new ProductDto();
            p.setNome(nome);
            p.setPreco(preco);
            p.setDescricao(descricao);
            p.setImagemUrl(imagem);
            CartManager.addItem(this, p);
            Toast.makeText(this, "Adicionado ao carrinho!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
