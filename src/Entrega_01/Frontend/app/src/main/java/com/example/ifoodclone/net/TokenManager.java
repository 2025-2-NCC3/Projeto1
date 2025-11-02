package com.example.ifoodclone.net;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private final SharedPreferences sp;
    public TokenManager(Context c){ sp = c.getSharedPreferences("auth", Context.MODE_PRIVATE); }
    public void save(String token){ sp.edit().putString("jwt", token).apply(); }
    public String get(){ return sp.getString("jwt", null); }
