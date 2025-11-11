package com.example.ifoodclone.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderDto {

    @SerializedName("id")
    public String id;

    @SerializedName("total")
    public double total;

    @SerializedName("status")
    public String status;

    @SerializedName("pickup_code")
    public String pickup_code;

    @SerializedName("created_at")
    public String created_at;

    @SerializedName("items")
    public List<OrderItemDto> items;


    @SerializedName("paid")
    public Integer paid;

    @SerializedName("user_id")
    public Integer user_id;

    public String getId()                 { return id; }
    public double getTotal()              { return total; }
    public String getStatus()             { return status; }
    public String getPickup_code()        { return pickup_code; }
    public String getCreated_at()         { return created_at; }
    public List<OrderItemDto> getItems()  { return items; }
    public Integer getPaid()              { return paid; }
    public Integer getUser_id()           { return user_id; }

    // setters
    public void setId(String id)                       { this.id = id; }
    public void setTotal(double total)                 { this.total = total; }
    public void setStatus(String status)               { this.status = status; }
    public void setPickup_code(String pickup_code)     { this.pickup_code = pickup_code; }
    public void setCreated_at(String created_at)       { this.created_at = created_at; }
    public void setItems(List<OrderItemDto> items)     { this.items = items; }
    public void setPaid(Integer paid)                  { this.paid = paid; }
    public void setUser_id(Integer user_id)            { this.user_id = user_id; }
}
