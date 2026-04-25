package com.sgf.poker.ui.prizes;

import java.util.Objects;

public final class PayedPlayer {
    private final String gamePlayerId;
    private final String name;

    public PayedPlayer(String gamePlayerId, String name) {
        this.gamePlayerId = gamePlayerId;
        this.name = name;
    }

    public String gamePlayerId() { return gamePlayerId; }
    public String name() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PayedPlayer)) return false;
        PayedPlayer other = (PayedPlayer) o;
        return Objects.equals(gamePlayerId, other.gamePlayerId)
                && Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gamePlayerId, name);
    }
}
