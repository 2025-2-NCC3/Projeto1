package com.example.ifoodclone.model;

public class ProductDto {
    private String id;
    private String nome;
    private double preco;
    private String categoria;
    private int quantidade;
    private String descricao;

    // Construtor padrão (necessário para Retrofit/Gson)
    public ProductDto() {}

    // Construtor para adicionar produto completo
    public ProductDto(String nome, double preco, String categoria, int quantidade, String descricao) {
        this.nome = nome;
        this.preco = preco;
        this.categoria = categoria;
        this.quantidade = quantidade;
        this.descricao = descricao;
    }

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public double getPreco() { return preco; }
    public String getCategoria() { return categoria; }
    public int getQuantidade() { return quantidade; }
    public String getDescricao() { return descricao; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setPreco(double preco) { this.preco = preco; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
