package com.example.ifoodclone.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.DigitsKeyListener; // << ADICIONADO
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ifoodclone.R;
import com.example.ifoodclone.model.Product;
import com.example.ifoodclone.model.ProductDto;
import com.example.ifoodclone.net.ApiClient;
import com.example.ifoodclone.net.ApiService;
import com.example.ifoodclone.util.TokenManager; // <<-- usa TokenManager

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 100;

    private EditText editNomeProduto, editPrecoProduto, edtQuantidade, edtDescricao;
    private Spinner spnCategoria;
    private Button btnConcluir, btnVoltar, btnAdicionarFoto, btnListarProdutos;
    private ApiService api;
    private Uri imageUri; // armazena a URI da imagem escolhida

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        api = ApiClient.getClient(this).create(ApiService.class);

        btnVoltar = findViewById(R.id.btnVoltar);
        btnAdicionarFoto = findViewById(R.id.btnAdicionarFoto);
        editNomeProduto = findViewById(R.id.editNomeProduto);
        editPrecoProduto = findViewById(R.id.editPrecoProduto);
        spnCategoria = findViewById(R.id.spnCategoria);
        edtQuantidade = findViewById(R.id.edtQuantidade);
        edtDescricao = findViewById(R.id.edtDescricao);
        btnConcluir = findViewById(R.id.btnConcluir);
        btnListarProdutos = findViewById(R.id.btnListarProdutos);

        // Força o teclado a aceitar dígitos + separadores decimal , e .
        editPrecoProduto.setKeyListener(DigitsKeyListener.getInstance("0123456789.,"));

        // Spinner
        String[] categorias = {"Selecione a categoria", "Salgados", "Bebidas", "Marmitas"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categorias
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategoria.setAdapter(adapter);

        // 📦 Produto recebido para edição (se houver)
        Product produtoEditando = (Product) getIntent().getSerializableExtra("produto");

        if (produtoEditando != null) {
            // 🟢 Edição
            preencherCampos(produtoEditando);
            btnConcluir.setText("Atualizar Produto");
            btnConcluir.setOnClickListener(v -> atualizarProduto(produtoEditando.getId()));
        } else {
            // 🟡 Novo
            btnConcluir.setText("Cadastrar Produto");
            btnConcluir.setOnClickListener(v -> salvarProduto());
        }

        btnAdicionarFoto.setOnClickListener(v -> escolherImagem());
        btnVoltar.setOnClickListener(v -> finish());
        btnListarProdutos.setOnClickListener(v ->
                startActivity(new Intent(AddProductActivity.this, ListarProdutosActivity.class))
        );
    }

    private void preencherCampos(Product produto) {
        editNomeProduto.setText(produto.getName());
        editPrecoProduto.setText(String.valueOf(produto.getPrice()));
        edtDescricao.setText(produto.getDescription());
    }

    private void escolherImagem() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Toast.makeText(this, "Imagem selecionada com sucesso!", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarProduto() {
        String nome = editNomeProduto.getText().toString().trim();
        String precoStr = editPrecoProduto.getText().toString().trim();
        String categoria = spnCategoria.getSelectedItem() != null ? spnCategoria.getSelectedItem().toString() : "";
        String descricao = edtDescricao.getText().toString().trim();

        if (categoria.equals("Selecione a categoria")) {
            Toast.makeText(this, "Selecione uma categoria válida!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nome.isEmpty() || precoStr.isEmpty() || descricao.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        double preco;
        try {
            // aceita "23,50" ou "23.50"
            preco = Double.parseDouble(precoStr.replace(',', '.'));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Verifique o valor do preço!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Partes do multipart
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), nome);
        RequestBody priceBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(preco));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), descricao);

        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            String filePath = com.example.ifoodclone.activity.FileUtils.getPath(this, imageUri);
            if (filePath != null) {
                File file = new File(filePath);
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), file);
                imagePart = MultipartBody.Part.createFormData("image", file.getName(), imageBody);
            }
        }

        Log.d("API_DEBUG", "Enviando produto: " + nome + " | " + preco + " | " + categoria);

        // ===== Bearer (TokenManager) =====
        String token = TokenManager.getToken(this);
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Sessão expirada. Faça login como admin.", Toast.LENGTH_LONG).show();
            return;
        }
        String bearer = "Bearer " + token;

        api.addProduto(bearer, nameBody, priceBody, descriptionBody, imagePart)
                .enqueue(new Callback<ProductDto>() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.d("API_DEBUG", "HTTP Code: " + response.code());
                        if (response.isSuccessful()) {
                            Toast.makeText(AddProductActivity.this, "Produto adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                            limparCampos();
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "sem detalhes";
                                Log.e("API_DEBUG", "Erro body: " + errorBody);
                                Toast.makeText(AddProductActivity.this,
                                        "Erro ao adicionar produto (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e("API_DEBUG", "Erro ao ler errorBody: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Log.e("API_DEBUG", "Falha na conexão: " + t.getMessage());
                        Toast.makeText(AddProductActivity.this,
                                "Falha na conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void limparCampos() {
        editNomeProduto.setText("");
        editPrecoProduto.setText("");
        edtDescricao.setText("");
        spnCategoria.setSelection(0);
        imageUri = null;
    }

    private void atualizarProduto(String id) {
        String nome = editNomeProduto.getText().toString().trim();
        String precoStr = editPrecoProduto.getText().toString().trim();
        String descricao = edtDescricao.getText().toString().trim();

        if (nome.isEmpty() || precoStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        double preco;
        try {
            // aceita "23,50" ou "23.50"
            preco = Double.parseDouble(precoStr.replace(',', '.'));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Verifique o valor do preço!", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), nome);
        RequestBody priceBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(preco));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), descricao);

        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            String filePath = com.example.ifoodclone.activity.FileUtils.getPath(this, imageUri);
            if (filePath != null) {
                File file = new File(filePath);
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), file);
                imagePart = MultipartBody.Part.createFormData("image", file.getName(), imageBody);
            }
        }

        // ===== Bearer (TokenManager) =====
        String token = TokenManager.getToken(this);
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Sessão expirada. Faça login como admin.", Toast.LENGTH_LONG).show();
            return;
        }
        String bearer = "Bearer " + token;

        api.updateProduto(bearer, id, nameBody, priceBody, descriptionBody, imagePart)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                String serverMessage = response.body() != null ? response.body().string() : "";
                                Log.d("API_DEBUG", "Update OK: " + serverMessage);
                                Toast.makeText(AddProductActivity.this, "Produto atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(AddProductActivity.this, "Erro ao ler resposta do servidor", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AddProductActivity.this, "Erro ao atualizar produto (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(AddProductActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
