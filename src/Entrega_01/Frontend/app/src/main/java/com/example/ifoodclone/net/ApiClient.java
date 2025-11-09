package com.example.ifoodclone.net;

import android.content.Context;

import com.example.ifoodclone.util.TokenManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;

    public static Retrofit getClient(Context ctx) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor((Interceptor) chain -> {
                        Request original = chain.request();
                        Request.Builder b = original.newBuilder();

                        String token = TokenManager.getToken(ctx);
                        if (token != null && !token.isEmpty()) {
                            b.addHeader("Authorization", "Bearer " + token);
                        }
                        return chain.proceed(b.build());
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:3000/") // ajuste se for diferente
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
