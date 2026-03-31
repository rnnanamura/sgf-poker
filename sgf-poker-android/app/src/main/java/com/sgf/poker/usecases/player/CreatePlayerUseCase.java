package com.sgf.poker.usecases.player;

import com.sgf.poker.data.repository.PlayerRepository;
import com.sgf.poker.domain.error.PlayerException;
import com.sgf.poker.domain.model.Player;

/** Validates and persists a new player. */
public class CreatePlayerUseCase {

    public static final int MAX_NAME_LENGTH = 50;

    private final PlayerRepository repository;

    public CreatePlayerUseCase(PlayerRepository repository) {
        this.repository = repository;
    }

    public Player execute(String name, boolean isMember, boolean isFounder) {
        var trimmed = name == null ? "" : name.strip();

        if (trimmed.isEmpty()) throw PlayerException.emptyName();
        if (trimmed.length() > MAX_NAME_LENGTH) throw PlayerException.nameTooLong(MAX_NAME_LENGTH);

        var player = Player.create(trimmed, isMember, isFounder);
        repository.save(player);
        return player;
    }
}
