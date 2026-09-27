package com.sgf.poker;

import android.app.Application;

import com.sgf.poker.ui.common.ThemePreference;

/**
 * Applies the saved light/dark theme before any activity is created, so the app never
 * flashes the wrong mode on cold start.
 */
public class SGFPokerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ThemePreference.apply(this);
    }
}
