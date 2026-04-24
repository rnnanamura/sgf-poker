package com.sgf.poker.domain.model;

import java.util.Objects;
import java.util.UUID;

/** Represents a player registered in the league. */
public class Player {

    private final String id;
    private final String name;
    private final boolean isMember;
    private final boolean isFounder;
    private final int currentPoints;

    public Player(String id, String name, boolean isMember, boolean isFounder, int currentPoints) {
        this.id = id;
        this.name = name;
        this.isMember = isMember;
        this.isFounder = isFounder;
        this.currentPoints = currentPoints;
    }

    /** Factory — new player with generated id and defaults. */
    public static Player create(String name, boolean isMember, boolean isFounder) {
        return new Player(UUID.randomUUID().toString(), name, isMember, isFounder, 0);
    }

    // ── Derived ──────────────────────────────────────────────────────────────

    /** Members and founders pay the lower entry fee. */
    public boolean isEligibleForMemberFee() {
        return isMember || isFounder;
    }

    // ── Mutation helpers (return new instance) ────────────────────────────────

    public Player withName(String val)          { return new Player(id, val,  isMember, isFounder, currentPoints); }
    public Player withMember(boolean val)       { return new Player(id, name, val,      isFounder, currentPoints); }
    public Player withFounder(boolean val)      { return new Player(id, name, isMember, val,       currentPoints); }
    public Player withPoints(int val)           { return new Player(id, name, isMember, isFounder, val); }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public String getId()         { return id; }
    public String getName()       { return name; }
    public boolean isMember()     { return isMember; }
    public boolean isFounder()    { return isFounder; }
    public int getCurrentPoints() { return currentPoints; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player p)) return false;
        return Objects.equals(id, p.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return "Player{id=%s, name=%s}".formatted(id, name); }
}
