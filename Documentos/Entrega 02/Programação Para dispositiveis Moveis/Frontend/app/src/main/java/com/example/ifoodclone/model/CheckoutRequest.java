package com.example.ifoodclone.model;

import java.util.List;

public class CheckoutRequest {
    public int user_id;
    public List<CheckoutItem> items;

    public CheckoutRequest() {}

    public CheckoutRequest(int user_id, List<CheckoutItem> items) {
        this.user_id = user_id;
        this.items = items;
    }
}
