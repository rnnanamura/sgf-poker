package com.sgf.poker.usecases.player;

import com.sgf.poker.data.repository.PlayerRepository;
import com.sgf.poker.domain.model.Player;
import java.util.List;

/** Retrieves all players sorted alphabetically. */
public class FetchPlayersUseCase {

    private final PlayerRepository repository;

    public FetchPlayersUseCase(PlayerRepository repository) {
        this.repository = repository;
    }

    public List<Player> execute() {
        return repository.fetchAll();
    }
}
