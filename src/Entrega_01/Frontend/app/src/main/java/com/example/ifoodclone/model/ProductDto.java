package com.example.ifoodclone.model;

import com.google.gson.annotations.SerializedName;

public class ProductDto {

    // id numérico vindo do backend; aceita "id" ou "product_id"
    @SerializedName(value = "id", alternate = {"product_id"})
    private Integer id;

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

    @SerializedName("image_url")
    private String imagemUrl;

    public ProductDto() {}



    public ProductDto(Integer id, String nome, double preco, String categoria, int quantidade, String descricao, String imagemUrl) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.categoria = categoria;
        this.quantidade = quantidade;
        this.descricao = descricao;
        this.imagemUrl = imagemUrl;
    }

    // Getters
    public Integer getId() { return id; }
    public int getIdAsInt() { return id != null ? id : -1; } // helper seguro

    public String getNome() { return nome; }
    public double getPreco() { return preco; }
    public String getCategoria() { return categoria; }
    public int getQuantidade() { return quantidade; }
    public String getDescricao() { return descricao; }
    public String getImagemUrl() { return imagemUrl; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setPreco(double preco) { this.preco = preco; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }
}
