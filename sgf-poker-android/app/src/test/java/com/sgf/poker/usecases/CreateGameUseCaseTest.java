package com.sgf.poker.usecases;

import com.sgf.poker.data.MockGameRepository;
import com.sgf.poker.data.MockPlayerRepository;
import com.sgf.poker.domain.error.GameException;
import com.sgf.poker.domain.model.Player;
import com.sgf.poker.usecases.game.CreateGameUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CreateGameUseCaseTest {

    private MockGameRepository gameRepo;
    private MockPlayerRepository playerRepo;
    private CreateGameUseCase sut;

    private static final LocalDate JAN_2025 = LocalDate.of(2025, 1, 1);
    private static final LocalDate MAR_2025 = LocalDate.of(2025, 3, 1);

    @BeforeEach
    void setUp() {
        gameRepo   = new MockGameRepository();
        playerRepo = new MockPlayerRepository();
        sut = new CreateGameUseCase(gameRepo, playerRepo);
    }

    @Test
    void savesGameToRepository() {
        sut.execute(JAN_2025);
        assertEquals(1, gameRepo.getStore().size());
    }

    @Test
    void returnsGameWithCorrectDate() {
        var game = sut.execute(JAN_2025);
        assertEquals(1, game.getEventMonth());
        assertEquals(2025, game.getEventYear());
    }

    @Test
    void derivedNameIsCorrect() {
        var game = sut.execute(JAN_2025);
        assertEquals("Jan 2025", game.getName());
    }

    @Test
    void seedsAllExistingPlayersAsGamePlayers() {
        playerRepo.save(Player.create("Alice", true, false));
        playerRepo.save(Player.create("Bob", false, false));

        var game = sut.execute(JAN_2025);

        assertEquals(2, game.getPlayers().size());
    }

    @Test
    void gamePlayerDefaultsToAllFalse() {
        playerRepo.save(Player.create("Alice", true, false));
        var game = sut.execute(JAN_2025);
        var gp = game.getPlayers().get(0);

        assertAll(
            () -> assertFalse(gp.isComing()),
            () -> assertFalse(gp.isPresent()),
            () -> assertFalse(gp.isPayed()),
            () -> assertEquals(0, gp.getRebuyCount()),
            () -> assertNull(gp.getFinalPosition())
        );
    }

    @Test
    void createsEmptyGameWhenNoPlayersExist() {
        var game = sut.execute(JAN_2025);
        assertTrue(game.getPlayers().isEmpty());
    }

    @Test
    void continuesIfPlayerFetchFails() {
        playerRepo.fetchError = new RuntimeException("disk error");
        var game = sut.execute(JAN_2025);
        assertTrue(game.getPlayers().isEmpty());
    }

    @Test
    void throwsDuplicateDateForSameMonthYear() {
        sut.execute(JAN_2025);
        // Same month, different day
        var sameMonth = LocalDate.of(2025, 1, 15);
        assertThrows(GameException.class, () -> sut.execute(sameMonth));
    }

    @Test
    void allowsDifferentMonthSameYear() {
        sut.execute(JAN_2025);
        assertDoesNotThrow(() -> sut.execute(MAR_2025));
    }
}
