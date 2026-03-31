package com.sgf.poker.data;

import com.sgf.poker.data.repository.PlayerRepository;
import com.sgf.poker.domain.error.PlayerException;
import com.sgf.poker.domain.model.Player;

import java.util.ArrayList;
import java.util.List;

/** In-memory test double for PlayerRepository. */
public class MockPlayerRepository implements PlayerRepository {

    private final List<Player> store = new ArrayList<>();
    public RuntimeException saveError = null;
    public RuntimeException fetchError = null;

    @Override
    public List<Player> fetchAll() {
        if (fetchError != null) throw fetchError;
        return List.copyOf(store);
    }

    @Override
    public void save(Player player) {
        if (saveError != null) throw saveError;
        store.removeIf(p -> p.getId().equals(player.getId()));
        store.add(player);
    }

    @Override
    public void delete(String id) {
        boolean removed = store.removeIf(p -> p.getId().equals(id));
        if (!removed) throw PlayerException.notFound(id);
    }

    public List<Player> getStore() { return List.copyOf(store); }
}
