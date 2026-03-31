package com.sgf.poker.domain.error;

/** Errors that can occur in the players domain. */
public class PlayerException extends RuntimeException {

    public PlayerException(String message) { super(message); }

    public static PlayerException emptyName() {
        return new PlayerException("Player name cannot be empty.");
    }

    public static PlayerException nameTooLong(int maxLength) {
        return new PlayerException("Player name must be " + maxLength + " characters or fewer.");
    }

    public static PlayerException duplicateName(String name) {
        return new PlayerException("A player named \"" + name + "\" already exists.");
    }

    public static PlayerException notFound(String id) {
        return new PlayerException("No player found with id " + id + ".");
    }

    public static PlayerException storageFailed(String reason) {
        return new PlayerException("Storage error: " + reason);
    }
}
