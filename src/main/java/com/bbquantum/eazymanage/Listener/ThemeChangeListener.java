package com.bbquantum.eazymanage.Listener;

import com.bbquantum.eazymanage.Constants.AppTheme;

@FunctionalInterface
public interface ThemeChangeListener {

    void onThemeChange(AppTheme theme);
}
