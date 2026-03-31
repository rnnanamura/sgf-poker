package com.sgf.poker.usecases.game;

import com.sgf.poker.data.repository.GameRepository;
import com.sgf.poker.data.repository.PlayerRepository;
import com.sgf.poker.domain.model.Game;
import com.sgf.poker.domain.model.GamePlayer;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates a new game pre-populated with all current players as GamePlayer entries.
 * If the player list cannot be read the game is still created with an empty roster.
 */
public class CreateGameUseCase {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    public CreateGameUseCase(GameRepository gameRepository, PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    public Game execute(LocalDate eventDate) {
        List<GamePlayer> gamePlayers;
        try {
            gamePlayers = playerRepository.fetchAll().stream()
                    .map(p -> GamePlayer.forPlayer(p.getId()))
                    .collect(Collectors.toList());
        } catch (Exception ignored) {
            gamePlayers = Collections.emptyList();
        }

        var game = Game.create(eventDate, gamePlayers);
        gameRepository.save(game);
        return game;
    }
}
