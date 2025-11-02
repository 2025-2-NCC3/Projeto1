package com.example.ifoodclone.net;

import com.example.ifoodclone.model.OrderDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("orders")
    Call<List<OrderDto>> getMyOrders();
}
