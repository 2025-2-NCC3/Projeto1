package com.example.ifoodclone.model;

public class CheckoutItem {
    public int product_id;
    public int quantity;

    public CheckoutItem() {}

    public CheckoutItem(int product_id, int quantity) {
        this.product_id = product_id;
        this.quantity = quantity;
    }
}
