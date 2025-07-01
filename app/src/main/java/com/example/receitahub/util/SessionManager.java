package com.example.receitahub.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "ReceitaHubSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_CHAT_COUNT = "chat_count";


    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(long userId) {
        editor.putLong(KEY_USER_ID, userId);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_CHAT_COUNT, 0); // Reseta a contagem no login
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    public int getChatInteractionCount() {
        return prefs.getInt(KEY_CHAT_COUNT, 0);
    }

    public void incrementChatInteractionCount() {
        int currentCount = getChatInteractionCount();
        editor.putInt(KEY_CHAT_COUNT, currentCount + 1);
        editor.apply();
    }
}