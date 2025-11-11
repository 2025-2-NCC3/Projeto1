package com.example.ifoodclone.model;

import java.io.Serializable;

public class Product implements Serializable {
    private String id;
    private String name;
    private double price;
    private String description;
    private String imageUrl;

    // 🟢 Novos campos
    private String category;
    private int quantity;

    public Product() {}

    public Product(String id, String name, double price, String description,
                   String imageUrl, String category, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.quantity = quantity;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }   // 🟢
    public int getQuantity() { return quantity; }      // 🟢

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setCategory(String category) { this.category = category; }   // 🟢
    public void setQuantity(int quantity) { this.quantity = quantity; }      // 🟢
}
