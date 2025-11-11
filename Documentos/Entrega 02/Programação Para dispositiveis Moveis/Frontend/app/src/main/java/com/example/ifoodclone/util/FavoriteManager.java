package com.example.ifoodclone.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;


public final class FavoriteManager {

    private static final String PREFS_NAME = "ifoodclone_prefs";
    private static final String KEY_FAVORITES = "favorite_ids";

    private FavoriteManager() {}

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static Set<String> getStringSet(Context ctx) {
        Set<String> stored = prefs(ctx).getStringSet(KEY_FAVORITES, null);
        return (stored == null) ? new HashSet<>() : new HashSet<>(stored);
    }

    private static void putStringSet(Context ctx, Set<String> set) {
        prefs(ctx).edit().putStringSet(KEY_FAVORITES, new HashSet<>(set)).apply();
    }

    public static Set<Integer> getFavoritesSet(Context ctx) {
        Set<String> raw = getStringSet(ctx);
        Set<Integer> out = new HashSet<>();
        for (String s : raw) {
            try { out.add(Integer.parseInt(s)); } catch (Exception ignored) {}
        }
        return out;
    }

    public static boolean isFavorite(Context ctx, int productId) {
        return getStringSet(ctx).contains(String.valueOf(productId));
    }

    public static void addFavorite(Context ctx, int productId) {
        Set<String> set = getStringSet(ctx);
        set.add(String.valueOf(productId));
        putStringSet(ctx, set);
    }

    public static void removeFavorite(Context ctx, int productId) {
        Set<String> set = getStringSet(ctx);
        set.remove(String.valueOf(productId));
        putStringSet(ctx, set);
    }

    public static boolean toggleFavorite(Context ctx, int productId) {
        Set<String> set = getStringSet(ctx);
        String key = String.valueOf(productId);
        boolean isFavNow;
        if (set.contains(key)) {
            set.remove(key);
            isFavNow = false;
        } else {
            set.add(key);
            isFavNow = true;
        }
        putStringSet(ctx, set);
        return isFavNow;
    }
}
