package com.sgf.poker.ui.common;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Stores the light/dark preference chosen on the Settings screen and applies it to
 * AppCompatDelegate. This is a presentation-only concern (no domain model, no repository):
 * it lives in SharedPreferences and is applied at process start by {@code SGFPokerApplication}.
 */
public final class ThemePreference {

    /** Theme options offered on the Settings screen. */
    public enum Mode {
        SYSTEM(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
        LIGHT(AppCompatDelegate.MODE_NIGHT_NO),
        DARK(AppCompatDelegate.MODE_NIGHT_YES);

        private final int nightMode;

        Mode(int nightMode) {
            this.nightMode = nightMode;
        }

        public int nightMode() {
            return nightMode;
        }
    }

    private static final String PREFS_NAME = "sgf_poker_ui_prefs";
    private static final String KEY_THEME_MODE = "theme_mode";

    private ThemePreference() {
    }

    /** Returns the stored mode, defaulting to {@link Mode#SYSTEM}. */
    public static Mode get(Context context) {
        String stored = prefs(context).getString(KEY_THEME_MODE, Mode.SYSTEM.name());
        try {
            return Mode.valueOf(stored);
        } catch (IllegalArgumentException e) {
            return Mode.SYSTEM;
        }
    }

    /** Persists the mode and applies it immediately (running activities are recreated). */
    public static void set(Context context, Mode mode) {
        prefs(context).edit().putString(KEY_THEME_MODE, mode.name()).apply();
        AppCompatDelegate.setDefaultNightMode(mode.nightMode());
    }

    /** Applies the stored mode. Called once on application start. */
    public static void apply(Context context) {
        AppCompatDelegate.setDefaultNightMode(get(context).nightMode());
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
