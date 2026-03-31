package com.sgf.poker.domain;

import com.sgf.poker.domain.model.Player;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void createSetsDefaultsCorrectly() {
        var player = Player.create("Alice", false, false);
        assertAll(
            () -> assertEquals("Alice", player.getName()),
            () -> assertFalse(player.isMember()),
            () -> assertFalse(player.isFounder()),
            () -> assertEquals(0, player.getCurrentPoints())
        );
    }

    @Test
    void memberIsEligibleForMemberFee() {
        var player = Player.create("Bob", true, false);
        assertTrue(player.isEligibleForMemberFee());
    }

    @Test
    void founderIsEligibleForMemberFee() {
        var player = Player.create("Carol", false, true);
        assertTrue(player.isEligibleForMemberFee());
    }

    @Test
    void guestIsNotEligibleForMemberFee() {
        var player = Player.create("Dave", false, false);
        assertFalse(player.isEligibleForMemberFee());
    }

    @Test
    void withMemberReturnsNewInstance() {
        var original = Player.create("Eve", false, false);
        var updated  = original.withMember(true);
        assertFalse(original.isMember());
        assertTrue(updated.isMember());
        assertEquals(original.getId(), updated.getId());
    }

    @Test
    void uniqueIdPerInstance() {
        var a = Player.create("Alice", false, false);
        var b = Player.create("Alice", false, false);
        assertNotEquals(a.getId(), b.getId());
    }
}
