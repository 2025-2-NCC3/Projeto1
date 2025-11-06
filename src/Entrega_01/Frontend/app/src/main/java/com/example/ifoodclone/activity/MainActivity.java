package com.example.ifoodclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ifoodclone.R;

public class MainActivity extends AppCompatActivity {

    // Views principais
    private ImageView logoImage;
    private EditText editSearch;

    // Categorias
    private TextView tvSalgados, tvBebidas, tvMarmitas, tvFavoritos;

    // Container de produtos
    private LinearLayout containerProdutos;

    // Menu inferior
    private ImageView iconHome, iconFavorite, iconCart, iconHistory, iconProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa componentes
        initViews();

        // Configura listeners
        setupCategoryClicks();
        setupBottomMenuClicks();

        // Exemplo: adiciona cards dinamicamente
        addMockProducts();
    }

    private void initViews() {
        logoImage = findViewById(R.id.logoImage);
        editSearch = findViewById(R.id.editSearch);

        tvSalgados = findViewById(R.id.tvSalgados);
        tvBebidas = findViewById(R.id.tvBebidas);
        tvMarmitas = findViewById(R.id.tvMarmitas);
        tvFavoritos = findViewById(R.id.tvFavoritos);

        containerProdutos = findViewById(R.id.containerProdutos);

        iconHome = findViewById(R.id.iconHome);
        iconFavorite = findViewById(R.id.iconFavorite);
        iconCart = findViewById(R.id.iconCart);
        iconHistory = findViewById(R.id.iconHistory);
        iconProfile = findViewById(R.id.iconProfile);
    }

    private void setupCategoryClicks() {
        View.OnClickListener listener = v -> {
            TextView tv = (TextView) v;
            String categoria = tv.getText().toString();
            Toast.makeText(this, "Categoria: " + categoria, Toast.LENGTH_SHORT).show();
            // Aqui futuramente você pode filtrar os produtos dessa categoria
        };

        tvSalgados.setOnClickListener(listener);
        tvBebidas.setOnClickListener(listener);
        tvMarmitas.setOnClickListener(listener);
        tvFavoritos.setOnClickListener(listener);
    }

    private void setupBottomMenuClicks() {
        iconHome.setOnClickListener(v -> Toast.makeText(this, "Início", Toast.LENGTH_SHORT).show());
        iconFavorite.setOnClickListener(v -> Toast.makeText(this, "Favoritos", Toast.LENGTH_SHORT).show());
        iconCart.setOnClickListener(v -> Toast.makeText(this, "Carrinho", Toast.LENGTH_SHORT).show());
        iconHistory.setOnClickListener(v -> Toast.makeText(this, "Histórico", Toast.LENGTH_SHORT).show());
        iconProfile.setOnClickListener(v -> Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show());
    }

    // Apenas para exemplo visual
    private void addMockProducts() {
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < 4; i++) {
            View card = inflater.inflate(R.layout.card_produtos, containerProdutos, false);

            TextView nome = card.findViewById(R.id.txtNomeProduto);
            TextView preco = card.findViewById(R.id.txtPrecoProduto);

            nome.setText("Produto " + (i + 1));
            preco.setText("R$ " + (10 + i * 2) + ",00");

            containerProdutos.addView(card);
        }
    }
}
