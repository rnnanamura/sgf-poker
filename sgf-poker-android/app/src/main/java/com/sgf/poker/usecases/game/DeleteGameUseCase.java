package com.sgf.poker.usecases.game;

import com.sgf.poker.data.repository.GameRepository;

/** Removes a game by id. */
public class DeleteGameUseCase {

    private final GameRepository repository;

    public DeleteGameUseCase(GameRepository repository) {
        this.repository = repository;
    }

    public void execute(String gameId) {
        repository.delete(gameId);
    }
}
