package com.sgf.poker.usecases.player;

import com.sgf.poker.data.repository.PlayerRepository;
import com.sgf.poker.domain.error.PlayerException;
import com.sgf.poker.domain.model.Player;

import java.util.function.UnaryOperator;

/** Applies a transformation to a player and persists the result. */
public class UpdatePlayerUseCase {

    private final PlayerRepository repository;

    public UpdatePlayerUseCase(PlayerRepository repository) {
        this.repository = repository;
    }

    public Player execute(String playerId, UnaryOperator<Player> mutation) {
        var players = repository.fetchAll();
        var existing = players.stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> PlayerException.notFound(playerId));

        var updated = mutation.apply(existing);
        repository.save(updated);
        return updated;
    }
}
