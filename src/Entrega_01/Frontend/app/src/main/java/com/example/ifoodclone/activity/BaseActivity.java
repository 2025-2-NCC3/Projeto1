package com.example.ifoodclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ifoodclone.R;

public abstract class BaseActivity extends AppCompatActivity {

    protected ImageView btnHome, btnFavoritos, btnCarrinho, btnHistorico, btnPerfil;

    /** Cada Activity vai dizer qual é a aba atual */
    protected abstract MenuScreen getCurrentScreen();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Subclasses chamam setContentView(...)
    }

    protected void setupBottomBar() {
        ImageView home = findViewById(R.id.iconHome);
        ImageView fav = findViewById(R.id.iconFavorite);
        ImageView cart = findViewById(R.id.iconCart);
        ImageView history = findViewById(R.id.iconHistory);
        ImageView profile = findViewById(R.id.iconProfile);

        if (home != null)
            home.setOnClickListener(v -> {
                if (getCurrentScreen() != MenuScreen.HOME)
                    startActivity(new Intent(this, MainActivity.class));
            });

        if (fav != null)
            fav.setOnClickListener(v -> {
                if (getCurrentScreen() != MenuScreen.FAVORITOS)
                    startActivity(new Intent(this, FavoritosActivity.class));
            });

        if (cart != null)
            cart.setOnClickListener(v -> {
                if (getCurrentScreen() != MenuScreen.CARRINHO)
                    startActivity(new Intent(this, CarrinhoActivity.class));
            });

        if (history != null)
            history.setOnClickListener(v -> {
                if (getCurrentScreen() != MenuScreen.HISTORICO)
                    startActivity(new Intent(this, PedidosActivity.class));
            });

        if (profile != null)
            profile.setOnClickListener(v -> {
                if (getCurrentScreen() != MenuScreen.PERFIL)
                    startActivity(new Intent(this, PerfilActivity.class));
            });
    }

}
