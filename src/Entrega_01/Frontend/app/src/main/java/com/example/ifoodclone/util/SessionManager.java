package com.example.ifoodclone.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF = "sessao";
    private static final String K_TOKEN = "token";
    private static final String K_USER_ID = "user_id";

    private final SharedPreferences sp;

    public SessionManager(Context ctx){
        sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void saveLogin(String token, int userId){
        sp.edit()
                .putString(K_TOKEN, token)
                .putInt(K_USER_ID, userId)
                .apply();
    }

    public String getToken(){
        return sp.getString(K_TOKEN, null);
    }

    public int getUserId(){
        return sp.getInt(K_USER_ID, -1);
    }

    public boolean isLogged(){
        return getToken() != null && getUserId() > 0;
    }

    public void logout(){
        sp.edit().clear().apply();
    }
}
