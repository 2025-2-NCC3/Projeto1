package com.example.ifoodclone.model;

import com.google.gson.annotations.SerializedName;

public class OrderItemDto {

    // nomes iguais aos retornados pelo backend
    @SerializedName("name")
    public String name;

    @SerializedName("price")
    public double price;

    @SerializedName("quantity")
    public int quantity;

    // getters (mantendo os campos públicos pra não quebrar nada que use acesso direto)
    public String getName()     { return name; }
    public double getPrice()    { return price; }
    public int getQuantity()    { return quantity; }

    public void setName(String name)       { this.name = name; }
    public void setPrice(double price)     { this.price = price; }
    public void setQuantity(int quantity)  { this.quantity = quantity; }
}
