package com.example.ifoodclone.model;

import com.google.gson.annotations.SerializedName;

public class ProductDto {

    private String id;

    @SerializedName("name")
    private String nome;

    @SerializedName("price")
    private double preco;

    @SerializedName("category")
    private String categoria;

    @SerializedName("quantity")
    private int quantidade;

    @SerializedName("description")
    private String descricao;

    // 🆕 Campo de imagem retornado pelo backend
    @SerializedName("image_url")
    private String imagemUrl;

    public ProductDto() {}

    public ProductDto(String nome, double preco, String categoria, int quantidade, String descricao, String imagemUrl) {
        this.nome = nome;
        this.preco = preco;
        this.categoria = categoria;
        this.quantidade = quantidade;
        this.descricao = descricao;
        this.imagemUrl = imagemUrl;
    }

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public double getPreco() { return preco; }
    public String getCategoria() { return categoria; }
    public int getQuantidade() { return quantidade; }
    public String getDescricao() { return descricao; }
    public String getImagemUrl() { return imagemUrl; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setPreco(double preco) { this.preco = preco; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }
}
