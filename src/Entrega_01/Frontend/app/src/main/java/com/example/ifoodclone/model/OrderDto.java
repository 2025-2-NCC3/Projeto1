package com.example.ifoodclone.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderDto {
    public String id;
    public double total;
    public String status;
    @SerializedName("items") public List<OrderItemDto> items;
}