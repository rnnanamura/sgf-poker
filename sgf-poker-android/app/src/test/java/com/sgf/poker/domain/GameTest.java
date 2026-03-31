package com.sgf.poker.domain;

import com.sgf.poker.domain.model.Game;
import com.sgf.poker.domain.model.GamePlayer;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private static final LocalDate JAN_2025 = LocalDate.of(2025, 1, 1);
    private static final LocalDate MAR_2025 = LocalDate.of(2025, 3, 15);

    @Test
    void nameFormatsAsMonthYear() {
        var game = Game.create(JAN_2025, List.of());
        assertEquals("Jan 2025", game.getName());
    }

    @Test
    void nameUsesThreeLetterMonth() {
        var game = Game.create(MAR_2025, List.of());
        assertEquals("Mar 2025", game.getName());
    }

    @Test
    void equalsById() {
        var game = Game.create(JAN_2025, List.of());
        var same = new Game(game.getId(), MAR_2025, LocalDate.now(), List.of());
        assertEquals(game, same);
    }

    @Test
    void defaultIdIsUnique() {
        var a = Game.create(JAN_2025, List.of());
        var b = Game.create(JAN_2025, List.of());
        assertNotEquals(a.getId(), b.getId());
    }

    @Test
    void getPresentPlayersFiltersCorrectly() {
        var gp1 = GamePlayer.forPlayer("p1").withPresent(true);
        var gp2 = GamePlayer.forPlayer("p2");
        var game = Game.create(JAN_2025, List.of(gp1, gp2));

        assertEquals(1, game.getPresentPlayers().size());
        assertEquals("p1", game.getPresentPlayers().get(0).getPlayerId());
    }

    @Test
    void getRankedPlayersSortsByPosition() {
        var gp1 = GamePlayer.forPlayer("p1").withPresent(true).withFinalPosition(2);
        var gp2 = GamePlayer.forPlayer("p2").withPresent(true).withFinalPosition(1);
        var game = Game.create(JAN_2025, List.of(gp1, gp2));

        var ranked = game.getRankedPlayers();
        assertEquals(2, ranked.size());
        assertEquals(1, ranked.get(0).getFinalPosition());
        assertEquals(2, ranked.get(1).getFinalPosition());
    }

    @Test
    void isFinishedWhenAllPresentPlayersRanked() {
        var gp1 = GamePlayer.forPlayer("p1").withPresent(true).withFinalPosition(1);
        var gp2 = GamePlayer.forPlayer("p2").withPresent(true).withFinalPosition(2);
        var game = Game.create(JAN_2025, List.of(gp1, gp2));
        assertTrue(game.isFinished());
    }

    @Test
    void notFinishedWhenSomeUnranked() {
        var gp1 = GamePlayer.forPlayer("p1").withPresent(true).withFinalPosition(1);
        var gp2 = GamePlayer.forPlayer("p2").withPresent(true);
        var game = Game.create(JAN_2025, List.of(gp1, gp2));
        assertFalse(game.isFinished());
    }

    @Test
    void notFinishedWhenNoPresentPlayers() {
        var game = Game.create(JAN_2025, List.of());
        assertFalse(game.isFinished());
    }

    @Test
    void withUpdatedPlayerReplacesCorrectEntry() {
        var gp = GamePlayer.forPlayer("p1");
        var game = Game.create(JAN_2025, List.of(gp));

        var updated = game.withUpdatedPlayer(gp.withComing(true));
        assertTrue(updated.getPlayers().get(0).isComing());
    }
}
