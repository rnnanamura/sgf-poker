package com.sgf.poker.domain.model;

public final class PlayerPrize {
    private final String gamePlayerId;
    private final String playerName;
    private final int position;
    private final double entryFee;
    private final double prizeAmount;

    public PlayerPrize(String gamePlayerId, String playerName, int position,
            double entryFee, double prizeAmount) {
        this.gamePlayerId = gamePlayerId;
        this.playerName = playerName;
        this.position = position;
        this.entryFee = entryFee;
        this.prizeAmount = prizeAmount;
    }

    public String gamePlayerId() { return gamePlayerId; }
    public String playerName() { return playerName; }
    public int position() { return position; }
    public double entryFee() { return entryFee; }
    public double prizeAmount() { return prizeAmount; }
}