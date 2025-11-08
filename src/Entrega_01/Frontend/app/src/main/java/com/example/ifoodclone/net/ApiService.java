package com.example.ifoodclone.net;

import com.example.ifoodclone.model.*;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // 🔐 Autenticação
    @POST("login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("user")
    Call<Void> register(@Body RegisterRequest request);

    // 🛍️ Produtos
    @GET("/products")
    Call<List<ProductDto>> getProdutos(); // ✅ nome ajustado

    @Multipart
    @POST("/admin/product")
    Call<ProductDto> addProduto(
            @Part("name") RequestBody name,
            @Part("price") RequestBody price,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part image
    );

    // 🎟️ Cupons
    @GET("/cupons")
    Call<List<CouponDto>> getCoupons();

    // 🧾 Pedidos
    @GET("/orders")
    Call<List<OrderDto>> getMyOrders();

    @POST("/orders")
    Call<OrderDto> createOrder(@Body OrderDto dto);

    // 🗑️ Excluir produto
    @retrofit2.http.DELETE("/admin/product/{id}")
    Call<Void> deleteProduto(@Path("id") String id);

    // ✏️ Atualizar produto
    @Multipart
    @PUT("admin/product/{id}")
    Call<ResponseBody> updateProduto(
            @Path("id") String id,
            @Part("name") RequestBody name,
            @Part("price") RequestBody price,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part image
    );
}
