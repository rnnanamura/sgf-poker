package com.sgf.poker.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player's participation record in a specific game.
 * Links a Player (by id) to a Game and tracks their in-game status.
 */
public class GamePlayer {

    private final String id;
    private final String playerId;
    private final boolean coming;
    private final boolean present;
    private final boolean payed;
    private final int rebuyCount;
    private final Integer finalPosition; // null = not yet ranked
    private final Double overridePrizeAmount; // null = use calculated amount

    public GamePlayer(
            String id,
            String playerId,
            boolean coming,
            boolean present,
            boolean payed,
            int rebuyCount,
            Integer finalPosition,
            Double overridePrizeAmount) {
        this.id = id;
        this.playerId = playerId;
        this.coming = coming;
        this.present = present;
        this.payed = payed;
        this.rebuyCount = rebuyCount;
        this.finalPosition = finalPosition;
        this.overridePrizeAmount = overridePrizeAmount;
    }

    /** Factory — new GamePlayer seeded from an existing Player. */
    public static GamePlayer forPlayer(String playerId) {
        return new GamePlayer(UUID.randomUUID().toString(), playerId,
                false, false, false, 0, null, null);
    }

    // ── Derived ──────────────────────────────────────────────────────────────

    public int getTotalBuyIns() { return 1 + rebuyCount; }
    public boolean hasFinished() { return finalPosition != null; }

    // ── Mutation helpers (return new instance) ────────────────────────────────

    public GamePlayer withComing(boolean val)                { return new GamePlayer(id, playerId, val, present, payed, rebuyCount, finalPosition, overridePrizeAmount); }
    public GamePlayer withPresent(boolean val)               { return new GamePlayer(id, playerId, val || coming, val, payed, rebuyCount, finalPosition, overridePrizeAmount); }
    public GamePlayer withPayed(boolean val)                 { return new GamePlayer(id, playerId, val || coming, val || present, val, rebuyCount, finalPosition, overridePrizeAmount); }
    public GamePlayer withRebuyCount(int val)                { return new GamePlayer(id, playerId, coming, present, payed, val, finalPosition, overridePrizeAmount); }
    public GamePlayer withFinalPosition(Integer val)         { return new GamePlayer(id, playerId, coming, present, payed, rebuyCount, val, overridePrizeAmount); }
    public GamePlayer withOverridePrizeAmount(Double val)    { return new GamePlayer(id, playerId, coming, present, payed, rebuyCount, finalPosition, val); }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public String getId()                   { return id; }
    public String getPlayerId()             { return playerId; }
    public boolean isComing()               { return coming; }
    public boolean isPresent()              { return present; }
    public boolean isPayed()                { return payed; }
    public int getRebuyCount()              { return rebuyCount; }
    public Integer getFinalPosition()       { return finalPosition; }
    public Double getOverridePrizeAmount()  { return overridePrizeAmount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GamePlayer gp)) return false;
        return Objects.equals(id, gp.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
