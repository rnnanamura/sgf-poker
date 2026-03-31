package com.sgf.poker.data.repository;

import com.sgf.poker.domain.model.Game;
import java.util.List;

/** Contract for any games data source. */
public interface GameRepository {
    /** Returns all games sorted by eventDate descending (most recent first). */
    List<Game> fetchAll();

    /** Persists a game. Throws GameException.duplicateDate if month/year already exists. */
    void save(Game game);

    /** Removes a game by id. Throws GameException.notFound if missing. */
    void delete(String id);
}
