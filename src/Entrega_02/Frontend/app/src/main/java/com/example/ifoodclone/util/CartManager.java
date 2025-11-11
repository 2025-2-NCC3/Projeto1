package com.example.ifoodclone.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ifoodclone.model.ProductDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static final String PREF_NAME = "cart_pref";
    private static final String CART_KEY = "cart_items";

    public static void addItem(Context context, ProductDto produto) {
        List<ProductDto> itens = getItems(context);
        itens.add(produto);
        saveItems(context, itens);
    }

    public static List<ProductDto> getItems(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(CART_KEY, null);

        if (json == null) return new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<List<ProductDto>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void removeItem(Context context, int position) {
        List<ProductDto> itens = getItems(context);
        if (position >= 0 && position < itens.size()) {
            itens.remove(position);
            saveItems(context, itens);
        }
    }

    public static void clearCart(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(CART_KEY).apply();
    }

    private static void saveItems(Context context, List<ProductDto> itens) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(itens);
        prefs.edit().putString(CART_KEY, json).apply();
    }

    // Helper opcional (se usar) — agora pegando ID como Integer do DTO
    public static List<int[]> asProductQtyPairs(Context context) {
        List<ProductDto> itens = getItems(context);
        List<int[]> pairs = new ArrayList<>();
        for (ProductDto p : itens) {
            int id = (p != null) ? p.getIdAsInt() : -1;
            if (id > 0) {
                pairs.add(new int[]{ id, 1 }); // quantidade padrão = 1
            }
        }
        return pairs;
    }
}
