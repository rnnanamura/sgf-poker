package com.sgf.poker.domain.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Represents a single poker game entry.
 * Name is derived from eventDate as "MMM yyyy" (e.g. "Jan 2025").
 */
public class Game {

    private final String id;
    private final LocalDate eventDate;
    private final LocalDate createdAt;
    private final List<GamePlayer> players;

    private static final DateTimeFormatter NAME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM yyyy");

    public Game(String id, LocalDate eventDate, LocalDate createdAt, List<GamePlayer> players) {
        this.id = id;
        this.eventDate = eventDate;
        this.createdAt = createdAt;
        this.players = new ArrayList<>(players);
    }

    /** Factory — new game with generated id and today as createdAt. */
    public static Game create(LocalDate eventDate, List<GamePlayer> players) {
        return new Game(UUID.randomUUID().toString(), eventDate, LocalDate.now(), players);
    }

    // ── Derived ──────────────────────────────────────────────────────────────

    /** Display name derived from eventDate, e.g. "Jan 2025". */
    public String getName() {
        return NAME_FORMATTER.format(eventDate);
    }

    public int getEventYear()  { return eventDate.getYear(); }
    public int getEventMonth() { return eventDate.getMonthValue(); }

    public List<GamePlayer> getConfirmedPlayers() {
        return players.stream().filter(GamePlayer::isComing).collect(Collectors.toList());
    }

    public List<GamePlayer> getPresentPlayers() {
        return players.stream().filter(GamePlayer::isPresent).collect(Collectors.toList());
    }

    public List<GamePlayer> getRankedPlayers() {
        return players.stream()
                .filter(GamePlayer::hasFinished)
                .sorted((a, b) -> {
                    int pa = a.getFinalPosition() != null ? a.getFinalPosition() : Integer.MAX_VALUE;
                    int pb = b.getFinalPosition() != null ? b.getFinalPosition() : Integer.MAX_VALUE;
                    return Integer.compare(pa, pb);
                })
                .collect(Collectors.toList());
    }

    public boolean isFinished() {
        var present = getPresentPlayers();
        return !present.isEmpty() && present.stream().allMatch(GamePlayer::hasFinished);
    }

    // ── Mutation helpers (return new instance — immutable-style) ─────────────

    public Game withPlayers(List<GamePlayer> newPlayers) {
        return new Game(id, eventDate, createdAt, newPlayers);
    }

    public Game withUpdatedPlayer(GamePlayer updated) {
        var newPlayers = players.stream()
                .map(gp -> gp.getId().equals(updated.getId()) ? updated : gp)
                .collect(Collectors.toList());
        return withPlayers(newPlayers);
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public String getId()          { return id; }
    public LocalDate getEventDate(){ return eventDate; }
    public LocalDate getCreatedAt(){ return createdAt; }
    public List<GamePlayer> getPlayers() { return List.copyOf(players); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game g)) return false;
        return Objects.equals(id, g.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return "Game{id=%s, name=%s}".formatted(id, getName()); }
}
