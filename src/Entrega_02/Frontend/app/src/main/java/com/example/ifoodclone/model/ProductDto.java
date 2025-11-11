package com.example.ifoodclone.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class ProductDto {

    @SerializedName(value = "id", alternate = {"product_id"})
    private Integer id;

    @SerializedName("name")
    private String nome;

    @SerializedName("price")
    private double preco;

    @SerializedName("category")
    @Nullable
    private String categoria;

    @SerializedName("quantity")
    private int quantidade;

    @SerializedName("description")
    private String descricao;

    @SerializedName("image_url")
    private String imagemUrl;

    public ProductDto() {}

    public ProductDto(Integer id, String nome, double preco, String categoria,
                      int quantidade, String descricao, String imagemUrl) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.categoria = categoria;
        this.quantidade = quantidade;
        this.descricao = descricao;
        this.imagemUrl = imagemUrl;
    }

    public Integer getId() { return id; }
    public int getIdAsInt() { return id != null ? id : -1; }

    public String getNome() { return nome; }
    public double getPreco() { return preco; }

    @Nullable
    public String getCategoria() { return categoria; }

    @NonNull
    public String getCategoriaSafe() { return categoria == null ? "" : categoria; }

    public int getQuantidade() { return quantidade; }
    public boolean temEstoque() { return quantidade > 0; }

    public String getDescricao() { return descricao; }
    public String getImagemUrl() { return imagemUrl; }

    // ========= Setters =========
    public void setId(Integer id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setPreco(double preco) { this.preco = preco; }
    public void setCategoria(@Nullable String categoria) { this.categoria = categoria; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }

    // ========= Utilidades =========
    /** Útil para validar antes de favoritar/adicionar ao carrinho */
    public boolean hasValidId() { return getIdAsInt() > 0; }

    // Igualdade por ID (evita itens duplicados em listas, facilita remove/update)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductDto)) return false;
        ProductDto that = (ProductDto) o;
        return getIdAsInt() == that.getIdAsInt();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdAsInt());
    }

    @NonNull
    @Override
    public String toString() {
        return "ProductDto{id=" + getIdAsInt() + ", nome='" + nome + "'}";
    }

    public String getImagemUrlCompleta() {
        if (imagemUrl == null || imagemUrl.isEmpty()) return "";
        if (imagemUrl.startsWith("http")) return imagemUrl;
        // Ajusta se vier apenas /uploads/xxx.webp
        return "http://10.0.2.2:3000" + imagemUrl;
    }

}
