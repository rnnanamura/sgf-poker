package com.sgf.poker.data;

import com.sgf.poker.data.repository.GameRepository;
import com.sgf.poker.data.repository.PlayerRepository;
import com.sgf.poker.domain.error.GameException;
import com.sgf.poker.domain.error.PlayerException;
import com.sgf.poker.domain.model.Game;
import com.sgf.poker.domain.model.Player;

import java.util.ArrayList;
import java.util.List;

/** In-memory test double for GameRepository. */
public class MockGameRepository implements GameRepository {

    private final List<Game> store = new ArrayList<>();
    public RuntimeException saveError = null;
    public RuntimeException fetchError = null;

    @Override
    public List<Game> fetchAll() {
        if (fetchError != null) throw fetchError;
        return List.copyOf(store);
    }

    @Override
    public void save(Game game) {
        if (saveError != null) throw saveError;
        store.removeIf(g -> g.getId().equals(game.getId()));
        store.add(game);
    }

    @Override
    public void delete(String id) {
        boolean removed = store.removeIf(g -> g.getId().equals(id));
        if (!removed) throw GameException.notFound(id);
    }

    public List<Game> getStore() { return List.copyOf(store); }
}
