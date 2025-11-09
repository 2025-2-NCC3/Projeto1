package com.example.ifoodclone.util;

import android.content.Context;
import android.content.SharedPreferences;

public final class TokenManager {

    private static final String PREF = "auth_prefs";
    private static final String K_TOKEN = "jwt_token";
    private static final String K_USER_ID = "user_id";

    private TokenManager() {}

    private static SharedPreferences sp(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    /** Salva token e userId juntos (fonte única da verdade). */
    public static void save(Context ctx, String token, int userId) {
        sp(ctx).edit()
                .putString(K_TOKEN, token)
                .putInt(K_USER_ID, userId)
                .apply();
    }

    // Compat: usados em alguns pontos do app
    public static void saveToken(Context ctx, String token) {
        sp(ctx).edit().putString(K_TOKEN, token).apply();
    }

    public static void saveUserId(Context ctx, int userId) {
        sp(ctx).edit().putInt(K_USER_ID, userId).apply();
    }

    public static String getToken(Context ctx) {
        return sp(ctx).getString(K_TOKEN, null);
    }

    public static int getUserId(Context ctx) {
        return sp(ctx).getInt(K_USER_ID, -1);
    }

    public static boolean hasToken(Context ctx) {
        String t = getToken(ctx);
        int id = getUserId(ctx);
        return t != null && !t.isEmpty() && id > 0;
    }

    public static void clear(Context ctx) {
        sp(ctx).edit().clear().apply();
    }
}
