package com.example.ifoodclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ifoodclone.R;
import com.example.ifoodclone.model.ProductDto;
import com.example.ifoodclone.net.ApiClient;
import com.example.ifoodclone.net.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {

    private EditText editProductName, editProductPrice;
    private Button buttonSaveProduct, buttonVerProdutos;
    private ApiService api; // ✅ Aqui declaramos o objeto ApiService

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Inicializa o Retrofit
        api = ApiClient.getClient(this).create(ApiService.class);

        editProductName = findViewById(R.id.editProductName);
        editProductPrice = findViewById(R.id.editProductPrice);
        buttonSaveProduct = findViewById(R.id.buttonSaveProduct);
        buttonVerProdutos = findViewById(R.id.btnVerProdutos);

        buttonSaveProduct.setOnClickListener(v -> salvarProduto());
        buttonVerProdutos.setOnClickListener(v ->
                startActivity(new android.content.Intent(this, ListarProdutosActivity.class))
        );
    }

    private void salvarProduto() {
        String nome = editProductName.getText().toString().trim();
        String precoStr = editProductPrice.getText().toString().trim();

        if (nome.isEmpty() || precoStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        double preco;
        try {
            preco = Double.parseDouble(precoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Preço inválido!", Toast.LENGTH_SHORT).show();
            return;
        }

        ProductDto dto = new ProductDto(nome, preco);

        // Envia o produto via Retrofit
        api.addProduto(dto).enqueue(new Callback<ProductDto>() {
            @Override
            public void onResponse(Call<ProductDto> call, Response<ProductDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddProductActivity.this, "Produto adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                    editProductName.setText("");
                    editProductPrice.setText("");
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
}
