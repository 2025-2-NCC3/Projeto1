package com.example.ifoodclone.model;

import com.google.gson.annotations.SerializedName;

public class UserDto {

    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("coupon_count")
    private Integer couponCount;

    public int getId() { return id; }
    public void setId(int id){ this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username){ this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email){ this.email = email; }

    public Integer getCouponCount() { return couponCount; }
    public void setCouponCount(Integer couponCount) { this.couponCount = couponCount; }
}
