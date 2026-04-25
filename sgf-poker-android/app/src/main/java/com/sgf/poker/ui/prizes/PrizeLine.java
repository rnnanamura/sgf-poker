package com.sgf.poker.ui.prizes;

import java.util.Objects;

public final class PrizeLine {
    private final int position;
    private final double amount;
    private final String gamePlayerId;
    private final String playerName;

    public PrizeLine(int position, double amount, String gamePlayerId, String playerName) {
        this.position = position;
        this.amount = amount;
        this.gamePlayerId = gamePlayerId;
        this.playerName = playerName;
    }

    public int position() { return position; }
    public double amount() { return amount; }
    public String gamePlayerId() { return gamePlayerId; }
    public String playerName() { return playerName; }
    public boolean isAssigned() { return gamePlayerId != null; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrizeLine)) return false;
        PrizeLine other = (PrizeLine) o;
        return position == other.position
                && Double.compare(amount, other.amount) == 0
                && Objects.equals(gamePlayerId, other.gamePlayerId)
                && Objects.equals(playerName, other.playerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, amount, gamePlayerId, playerName);
    }
}
