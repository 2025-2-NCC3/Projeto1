package com.example.ifoodclone.net;

import android.content.Context;

import com.example.ifoodclone.util.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;

    // URL base — use 10.0.2.2 para acessar o localhost do PC a partir do emulador Android
    private static final String BASE_URL = "http://10.0.2.2:3000/";

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {

            // 🔒 Interceptor para adicionar token JWT automaticamente
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            String token = TokenManager.getToken(context);

                            Request.Builder builder = original.newBuilder()
                                    .header("Content-Type", "application/json");

                            if (token != null && !token.isEmpty()) {
                                builder.header("Authorization", "Bearer " + token);
                            }

                            Request request = builder.build();
                            return chain.proceed(request);
                        }
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }
}
