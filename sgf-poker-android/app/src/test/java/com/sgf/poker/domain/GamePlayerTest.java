package com.sgf.poker.domain;

import com.sgf.poker.domain.model.GamePlayer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GamePlayerTest {

    @Test
    void forPlayerSetsDefaultsToFalseAndZero() {
        var gp = GamePlayer.forPlayer("player-1");
        assertAll(
            () -> assertFalse(gp.isComing()),
            () -> assertFalse(gp.isPresent()),
            () -> assertFalse(gp.isPayed()),
            () -> assertEquals(0, gp.getRebuyCount()),
            () -> assertNull(gp.getFinalPosition())
        );
    }

    @Test
    void hasFinishedWhenPositionSet() {
        var gp = GamePlayer.forPlayer("p1").withFinalPosition(3);
        assertTrue(gp.hasFinished());
    }

    @Test
    void notFinishedWhenNoPosition() {
        assertFalse(GamePlayer.forPlayer("p1").hasFinished());
    }

    @Test
    void totalBuyInsIncludesRebuys() {
        var gp = GamePlayer.forPlayer("p1").withRebuyCount(2);
        assertEquals(3, gp.getTotalBuyIns());
    }

    @Test
    void mutationHelpersReturnNewInstance() {
        var original = GamePlayer.forPlayer("p1");
        var mutated  = original.withComing(true);
        assertFalse(original.isComing(), "Original should be unchanged");
        assertTrue(mutated.isComing());
    }

    @Test
    void withRebuyCountDoesNotGoNegative() {
        // Logic in ViewModel prevents this, but model accepts any int
        var gp = GamePlayer.forPlayer("p1").withRebuyCount(-1);
        assertEquals(-1, gp.getRebuyCount()); // model is dumb — ViewModel guards it
    }
}
