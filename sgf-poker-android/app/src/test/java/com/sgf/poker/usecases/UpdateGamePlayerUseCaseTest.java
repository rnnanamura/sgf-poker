package com.sgf.poker.usecases;

import com.sgf.poker.data.MockGameRepository;
import com.sgf.poker.domain.error.GameException;
import com.sgf.poker.domain.model.Game;
import com.sgf.poker.domain.model.GamePlayer;
import com.sgf.poker.usecases.game.UpdateGamePlayerUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UpdateGamePlayerUseCaseTest {

    private MockGameRepository repo;
    private UpdateGamePlayerUseCase sut;

    private Game game;
    private GamePlayer gp;

    @BeforeEach
    void setUp() {
        repo = new MockGameRepository();
        sut  = new UpdateGamePlayerUseCase(repo);

        gp   = GamePlayer.forPlayer("player-1");
        game = Game.create(LocalDate.of(2025, 1, 1), List.of(gp));
        repo.save(game);
    }

    @Test
    void togglesComingField() {
        var updated = sut.execute(game.getId(), gp.getId(), p -> p.withComing(true));
        assertTrue(updated.getPlayers().get(0).isComing());
    }

    @Test
    void incrementsRebuyCount() {
        var updated = sut.execute(game.getId(), gp.getId(), p -> p.withRebuyCount(p.getRebuyCount() + 1));
        assertEquals(1, updated.getPlayers().get(0).getRebuyCount());
    }

    @Test
    void setsFinalPosition() {
        var updated = sut.execute(game.getId(), gp.getId(), p -> p.withFinalPosition(2));
        assertEquals(2, updated.getPlayers().get(0).getFinalPosition());
    }

    @Test
    void persistsChangeToRepository() {
        sut.execute(game.getId(), gp.getId(), p -> p.withPayed(true));
        var fromRepo = repo.fetchAll().get(0).getPlayers().get(0);
        assertTrue(fromRepo.isPayed());
    }

    @Test
    void throwsWhenGameNotFound() {
        assertThrows(GameException.class,
                () -> sut.execute("nonexistent-game", gp.getId(), p -> p));
    }

    @Test
    void throwsWhenGamePlayerNotFound() {
        assertThrows(GameException.class,
                () -> sut.execute(game.getId(), "nonexistent-gp", p -> p));
    }
}
