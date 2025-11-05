package com.example.ifoodclone.model;

public class ProductDto {
    private String id;
    private String nome;
    private double preco;

    public ProductDto() {}

    public ProductDto(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public double getPreco() { return preco; }

    public void setId(String id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setPreco(double preco) { this.preco = preco; }
}
