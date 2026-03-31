package com.sgf.poker.ui.prizes;

import androidx.annotation.Nullable;

/** Represents one awarded place, with or without an assigned player. */
public record PrizeLine(int position, double amount,
                        @Nullable String gamePlayerId,
                        @Nullable String playerName) {
    public boolean isAssigned() {
        return gamePlayerId != null;
    }
}
