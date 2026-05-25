package com.bbquantum.eazymanage.Utitity;

import java.util.prefs.Preferences;

public class SessionManager {
    private static final Preferences preferences = Preferences.userNodeForPackage(SessionManager.class);

    private static final String TOKEN_KEY = "jwt_token";

    private static final String USER_ID = "jwt_user_id";

    private static final String USER_NAME = "jwt_user_name";

    private static final String ORGANIZATION = "jwt_organization";

    private static final String ROLE_KEY = "user_role";

    private static final String ACTIVE_ROLE_KEY = "active_role";

    private static final String TOKEN_EXPIRATION_TIME = "token_expiration_time";

    public static void saveSession(String token, String userId, String userName,
                                   String organization, String role) {
        long expiryTime = System.currentTimeMillis() + 86400000;

        preferences.put(TOKEN_KEY, token);
        preferences.put(USER_ID, userId);
        preferences.put(USER_NAME, userName);
        preferences.put(ORGANIZATION, organization);
        preferences.put(ROLE_KEY, role);
        preferences.putLong(TOKEN_EXPIRATION_TIME, expiryTime);
    }

    public static String getToken() {
        return preferences.get(TOKEN_KEY, "");
    }

    public static String getUserId() {
        return preferences.get(USER_ID, "");
    }

    public static String getUserName() {
        return preferences.get(USER_NAME, "");
    }

    public static String getOrganization() {
        return preferences.get(ORGANIZATION, "");
    }

    public static String getRole() {
        return preferences.get(ROLE_KEY, "");
    }

    public static String getActiveRole() {
        return preferences.get(ACTIVE_ROLE_KEY, "");
    }

    /** Persist the active role. Called on launch (to record the picked
     *  default) and from Settings when the user switches. */
    public static void setActiveRole(String role) {
        if (role == null || role.isBlank()) {
            preferences.remove(ACTIVE_ROLE_KEY);
        } else {
            preferences.put(ACTIVE_ROLE_KEY, role);
        }
    }

    public static boolean isTokenExpired() {
        String token = getToken();
        long expiryTime = preferences.getLong(TOKEN_EXPIRATION_TIME, 0);

        if (!token.isEmpty() && System.currentTimeMillis() < expiryTime) return false;

        return true;
    }

    public static void clearSession() {
        preferences.remove(TOKEN_KEY);
        preferences.remove(USER_ID);
        preferences.remove(USER_NAME);
        preferences.remove(ORGANIZATION);
        preferences.remove(ROLE_KEY);
        preferences.remove(ACTIVE_ROLE_KEY);
        preferences.remove(TOKEN_EXPIRATION_TIME);
    }
}
