package com.example.ifoodclone.net;

import com.example.ifoodclone.model.AuthResponse;
import com.example.ifoodclone.model.CouponDto;
import com.example.ifoodclone.model.LoginRequest;
import com.example.ifoodclone.model.OrderDto;
import com.example.ifoodclone.model.ProductDto;
import com.example.ifoodclone.model.RegisterRequest;
import com.example.ifoodclone.model.UpdateUserRequest;
import com.example.ifoodclone.model.UserDto;
import com.example.ifoodclone.model.CheckoutRequest;
import com.example.ifoodclone.model.CheckoutResponse;
import com.example.ifoodclone.model.Product;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // 🔐 Auth
    @POST("login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("user")
    Call<Void> register(@Body RegisterRequest request);

    // 👤 Perfil
    @GET("/me")
    Call<UserDto> getMeuPerfil(@Header("Authorization") String bearerToken);

    @PUT("/me")
    Call<UserDto> updateMeuPerfil(
            @Header("Authorization") String bearerToken,
            @Body UpdateUserRequest body
    );

    @GET("/products")
    Call<List<ProductDto>> getProdutos();

    @GET("/admin/products")
    Call<List<Product>> getProdutosAdminRaw();

    @GET("/cupons")
    Call<List<CouponDto>> getCoupons();

    @GET("/myorders")
    Call<List<OrderDto>> getMyOrders(@Header("Authorization") String bearerToken);


    @POST("/orders")
    Call<OrderDto> createOrder(@Body OrderDto dto);

    @Multipart
    @POST("/admin/product")
    Call<ProductDto> addProduto(
            @Header("Authorization") String bearer,
            @Part("name") RequestBody name,
            @Part("price") RequestBody price,
            @Part("description") RequestBody description,
            @Part("category") RequestBody category,   // 🟢 NOVO
            @Part("quantity") RequestBody quantity,   // 🟢 NOVO
            @Part MultipartBody.Part image
    );

    @Multipart
    @PUT("/admin/product/{id}")
    Call<ResponseBody> updateProduto(
            @Header("Authorization") String bearer,
            @Path("id") String id,
            @Part("name") RequestBody name,
            @Part("price") RequestBody price,
            @Part("description") RequestBody description,
            @Part("category") RequestBody category,
            @Part("quantity") RequestBody quantity,
            @Part MultipartBody.Part image
    );

    @DELETE("/admin/product/{id}")
    Call<Void> deleteProduto(
            @Header("Authorization") String bearer,
            @Path("id") String id
    );

    @POST("/checkout")
    Call<CheckoutResponse> checkout(@Body CheckoutRequest body);
}
