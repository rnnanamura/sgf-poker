package com.sgf.poker.usecases;

import com.sgf.poker.data.MockPlayerRepository;
import com.sgf.poker.domain.error.PlayerException;
import com.sgf.poker.usecases.player.CreatePlayerUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreatePlayerUseCaseTest {

    private MockPlayerRepository repo;
    private CreatePlayerUseCase sut;

    @BeforeEach
    void setUp() {
        repo = new MockPlayerRepository();
        sut  = new CreatePlayerUseCase(repo);
    }

    @Test
    void savesPlayerToRepository() {
        sut.execute("Alice", false, false);
        assertEquals(1, repo.getStore().size());
    }

    @Test
    void returnsPlayerWithTrimmedName() {
        var player = sut.execute("  Alice  ", false, false);
        assertEquals("Alice", player.getName());
    }

    @Test
    void throwsOnEmptyName() {
        assertThrows(PlayerException.class, () -> sut.execute("", false, false));
    }

    @Test
    void throwsOnWhitespaceOnlyName() {
        assertThrows(PlayerException.class, () -> sut.execute("   ", false, false));
    }

    @Test
    void throwsWhenNameExceedsMaxLength() {
        var longName = "A".repeat(CreatePlayerUseCase.MAX_NAME_LENGTH + 1);
        assertThrows(PlayerException.class, () -> sut.execute(longName, false, false));
    }

    @Test
    void acceptsNameAtExactMaxLength() {
        var exactName = "A".repeat(CreatePlayerUseCase.MAX_NAME_LENGTH);
        assertDoesNotThrow(() -> sut.execute(exactName, false, false));
    }

    @Test
    void storesMemberAndFounderFlags() {
        var player = sut.execute("Alice", true, true);
        assertTrue(player.isMember());
        assertTrue(player.isFounder());
    }

    @Test
    void propagatesRepositoryError() {
        repo.saveError = PlayerException.duplicateName("Alice");
        assertThrows(PlayerException.class, () -> sut.execute("Alice", false, false));
    }
}
