package com.sgf.poker.usecases;

import com.sgf.poker.data.MockGameRepository;
import com.sgf.poker.data.MockPlayerRepository;
import com.sgf.poker.domain.error.GameException;
import com.sgf.poker.domain.error.PlayerException;
import com.sgf.poker.domain.model.Game;
import com.sgf.poker.domain.model.Player;
import com.sgf.poker.usecases.game.DeleteGameUseCase;
import com.sgf.poker.usecases.game.FetchGamesUseCase;
import com.sgf.poker.usecases.player.DeletePlayerUseCase;
import com.sgf.poker.usecases.player.FetchPlayersUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FetchAndDeleteUseCaseTest {

    @Nested
    class FetchGames {
        private MockGameRepository repo;
        private FetchGamesUseCase sut;

        @BeforeEach
        void setUp() { repo = new MockGameRepository(); sut = new FetchGamesUseCase(repo); }

        @Test
        void returnsEmptyListInitially() { assertTrue(sut.execute().isEmpty()); }

        @Test
        void returnsAllSavedGames() {
            repo.save(Game.create(LocalDate.of(2025, 1, 1), List.of()));
            repo.save(Game.create(LocalDate.of(2025, 3, 1), List.of()));
            assertEquals(2, sut.execute().size());
        }

        @Test
        void propagatesRepositoryError() {
            repo.fetchError = new RuntimeException("disk error");
            assertThrows(RuntimeException.class, () -> sut.execute());
        }
    }

    @Nested
    class DeleteGame {
        private MockGameRepository repo;
        private DeleteGameUseCase sut;

        @BeforeEach
        void setUp() { repo = new MockGameRepository(); sut = new DeleteGameUseCase(repo); }

        @Test
        void removesGameFromRepository() {
            var game = Game.create(LocalDate.of(2025, 1, 1), List.of());
            repo.save(game);
            sut.execute(game.getId());
            assertTrue(repo.getStore().isEmpty());
        }

        @Test
        void throwsWhenGameNotFound() {
            assertThrows(GameException.class, () -> sut.execute("nonexistent"));
        }
    }

    @Nested
    class FetchPlayers {
        private MockPlayerRepository repo;
        private FetchPlayersUseCase sut;

        @BeforeEach
        void setUp() { repo = new MockPlayerRepository(); sut = new FetchPlayersUseCase(repo); }

        @Test
        void returnsEmptyListInitially() { assertTrue(sut.execute().isEmpty()); }

        @Test
        void returnsAllSavedPlayers() {
            repo.save(Player.create("Alice", false, false));
            repo.save(Player.create("Bob", false, false));
            assertEquals(2, sut.execute().size());
        }
    }

    @Nested
    class DeletePlayer {
        private MockPlayerRepository repo;
        private DeletePlayerUseCase sut;

        @BeforeEach
        void setUp() { repo = new MockPlayerRepository(); sut = new DeletePlayerUseCase(repo); }

        @Test
        void removesPlayerFromRepository() {
            var player = Player.create("Alice", false, false);
            repo.save(player);
            sut.execute(player.getId());
            assertTrue(repo.getStore().isEmpty());
        }

        @Test
        void throwsWhenPlayerNotFound() {
            assertThrows(PlayerException.class, () -> sut.execute("nonexistent"));
        }
    }
}
