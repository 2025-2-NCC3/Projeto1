package com.example.ifoodclone.model;

import com.example.ifoodclone.helper.FirebaseConfiguration;
import com.google.firebase.database.DatabaseReference;

public class Product {
    private String userId;
    private String productId;
    private String name;
    private String description;
    private Double price;

    public Product() {
        DatabaseReference databaseReference = FirebaseConfiguration.getDatabaseReference();
        DatabaseReference productReference = databaseReference.child("products");
        setProductId(productReference.push().getKey());
    }
    public void save(){
        DatabaseReference databaseReference = FirebaseConfiguration.getDatabaseReference();
        DatabaseReference productReference = databaseReference.child("products").child(getUserId()).child(getProductId());
        productReference.setValue(this);
    }
    public void delete(){
        DatabaseReference databaseReference = FirebaseConfiguration.getDatabaseReference();
        DatabaseReference productReference = databaseReference.child("products").child(getUserId()).child(getProductId());
        productReference.removeValue();
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
