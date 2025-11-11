package com.example.ifoodclone.model;

public class CouponDto {
    private String id;
    private String codigo;
    private double desconto;
    private String validade;

    public CouponDto() {}

    public CouponDto(String id, String codigo, double desconto, String validade) {
        this.id = id;
        this.codigo = codigo;
        this.desconto = desconto;
        this.validade = validade;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public double getDesconto() { return desconto; }
    public void setDesconto(double desconto) { this.desconto = desconto; }

    public String getValidade() { return validade; }
    public void setValidade(String validade) { this.validade = validade; }
}
