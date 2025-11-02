package com.example.ifoodclone.net;

import android.content.Context;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Emulador: 10.0.2.2 ; Em celular: IP da sua máquina
    private static final String BASE_URL = "http://10.0.2.2:3000/";

    public static Retrofit get(Context ctx){
        TokenManager tm = new TokenManager(ctx);
        OkHttpClient ok = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request r = chain.request();
                    String tk = tm.get();
                    if (tk != null) {
                        r = r.newBuilder().addHeader("Authorization","Bearer "+tk).build();
                    }
                    return chain.proceed(r);
                }).build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(ok)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
