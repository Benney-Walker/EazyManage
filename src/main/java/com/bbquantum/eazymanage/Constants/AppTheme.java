package com.bbquantum.eazymanage.Constants;

public enum AppTheme {
    LIGHT,
    DARK,
    SYSTEM;

    public static AppTheme fromString(String value, AppTheme fallback) {
        if (value == null || value.isEmpty()) {
            return fallback;
        }

        try {
            return AppTheme.valueOf(value);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }
}
