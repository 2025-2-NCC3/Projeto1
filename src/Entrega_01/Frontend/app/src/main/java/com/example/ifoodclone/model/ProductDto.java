package com.example.ifoodclone.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class ProductDto {

    // id numérico vindo do backend; aceita "id" ou "product_id"
    @SerializedName(value = "id", alternate = {"product_id"})
    private Integer id;

    @SerializedName("name")
    private String nome;

    @SerializedName("price")
    private double preco;

    // OBS: seu backend atual não envia "category" e "quantity".
    // Se vierem ausentes no JSON, ficarão null/0 aqui (ok).
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

    // ========= Getters =========
    public Integer getId() { return id; }
    /** Helper seguro para favoritos/carrinho/listagens */
    public int getIdAsInt() { return id != null ? id : -1; }

    public String getNome() { return nome; }
    public double getPreco() { return preco; }

    /** Pode vir null se o backend não mandar "category" */
    @Nullable
    public String getCategoria() { return categoria; }

    /** Categoria "segura" para filtros/pesquisa (sem null) */
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
}
