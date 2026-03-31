package com.sgf.poker.usecases.game;

import com.sgf.poker.data.repository.GameRepository;
import com.sgf.poker.domain.model.Game;
import java.util.List;

/** Retrieves all games sorted by event date descending. */
public class FetchGamesUseCase {

    private final GameRepository repository;

    public FetchGamesUseCase(GameRepository repository) {
        this.repository = repository;
    }

    public List<Game> execute() {
        return repository.fetchAll();
    }
}
