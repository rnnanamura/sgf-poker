package com.sgf.poker.usecases.game;

import com.sgf.poker.data.repository.GameRepository;
import com.sgf.poker.domain.error.GameException;
import com.sgf.poker.domain.model.Game;
import com.sgf.poker.domain.model.GamePlayer;

import java.util.function.UnaryOperator;

/**
 * Applies a transformation to a single GamePlayer inside a Game and persists the result.
 * Uses a UnaryOperator<GamePlayer> instead of Swift's inout closure — same intent.
 */
public class UpdateGamePlayerUseCase {

    private final GameRepository repository;

    public UpdateGamePlayerUseCase(GameRepository repository) {
        this.repository = repository;
    }

    /**
     * @param gameId       the game containing the player
     * @param gamePlayerId the specific GamePlayer record to update
     * @param mutation     transformation to apply to the GamePlayer
     * @return the updated Game
     */
    public Game execute(String gameId, String gamePlayerId, UnaryOperator<GamePlayer> mutation) {
        var games = repository.fetchAll();
        var game = games.stream()
                .filter(g -> g.getId().equals(gameId))
                .findFirst()
                .orElseThrow(() -> GameException.notFound(gameId));

        var target = game.getPlayers().stream()
                .filter(gp -> gp.getId().equals(gamePlayerId))
                .findFirst()
                .orElseThrow(() -> GameException.notFound(gamePlayerId));

        var updated = game.withUpdatedPlayer(mutation.apply(target));
        repository.save(updated);
        return updated;
    }
}
