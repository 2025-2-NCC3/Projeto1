package com.example.ifoodclone.model;

import com.example.ifoodclone.helper.FirebaseConfiguration;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Company implements Serializable {
    private String id;
    private String urlImage;
    private String name;
    private String deliveryTime;
    private String category;
    private Double taxPrice;

    public Company() {
    }

    public void save(){
        DatabaseReference databaseReference = FirebaseConfiguration.getDatabaseReference();
        DatabaseReference companyReference = databaseReference.child("companies")
                .child(getId());
        companyReference.setValue(this);
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getTaxPrice() {
        return taxPrice;
    }

    public void setTaxPrice(Double taxPrice) {
        this.taxPrice = taxPrice;
    }
}
