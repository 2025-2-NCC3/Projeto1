package com.example.ifoodclone.net;

import com.example.ifoodclone.model.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // 🔐 Autenticação
    @POST("/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("/user")
    Call<Void> register(@Body RegisterRequest request);

    // 🛍️ Produtos
    @GET("/produtos")
    Call<List<ProductDto>> getProdutos();

    @POST("/produtos")
    Call<ProductDto> addProduto(@Body ProductDto dto);

    // 🎟️ Cupons
    @GET("/cupons")
    Call<List<CouponDto>> getCoupons();

    // 🧾 Pedidos
    @GET("/orders")
    Call<List<OrderDto>> getMyOrders();

    @POST("/orders")
    Call<OrderDto> createOrder(@Body OrderDto dto);
}
