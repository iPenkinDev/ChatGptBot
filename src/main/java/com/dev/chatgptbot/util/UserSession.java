package com.dev.chatgptbot.util;

public class UserSession {
    private static Long savedUserId;

    public static void saveUserId(long userId) {
        savedUserId = userId;
    }

    public static long getSavedUserId() {
        return savedUserId;
    }
}
