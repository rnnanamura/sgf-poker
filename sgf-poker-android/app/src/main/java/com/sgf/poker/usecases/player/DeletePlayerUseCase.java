package com.sgf.poker.usecases.player;

import com.sgf.poker.data.repository.PlayerRepository;

/** Removes a player by id. */
public class DeletePlayerUseCase {

    private final PlayerRepository repository;

    public DeletePlayerUseCase(PlayerRepository repository) {
        this.repository = repository;
    }

    public void execute(String playerId) {
        repository.delete(playerId);
    }
}
