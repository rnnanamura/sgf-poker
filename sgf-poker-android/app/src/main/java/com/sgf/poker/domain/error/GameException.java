package com.sgf.poker.domain.error;

/** Errors that can occur in the games domain. */
public class GameException extends RuntimeException {

    public GameException(String message) { super(message); }

    public static GameException duplicateDate(String label) {
        return new GameException("A game for " + label + " already exists.");
    }

    public static GameException notFound(String id) {
        return new GameException("No game found with id " + id + ".");
    }

    public static GameException storageFailed(String reason) {
        return new GameException("Storage error: " + reason);
    }
}
