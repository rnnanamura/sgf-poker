package com.sgf.poker.data.repository;

import com.sgf.poker.domain.model.Player;
import java.util.List;

/** Contract for any players data source. */
public interface PlayerRepository {
    /** Returns all players sorted alphabetically by name. */
    List<Player> fetchAll();

    /** Persists a player. Throws PlayerException.duplicateName if name already exists. */
    void save(Player player);

    /** Removes a player by id. Throws PlayerException.notFound if missing. */
    void delete(String id);
}
