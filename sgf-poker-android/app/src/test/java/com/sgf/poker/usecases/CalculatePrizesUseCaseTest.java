package com.sgf.poker.usecases;

import com.sgf.poker.domain.model.*;
import com.sgf.poker.usecases.prizes.CalculatePrizesUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculatePrizesUseCaseTest {

    private CalculatePrizesUseCase sut;
    private static final LocalDate DATE = LocalDate.of(2025, 1, 1);

    @BeforeEach
    void setUp() { sut = new CalculatePrizesUseCase(); }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Player member(String id)    { return new Player(id, "M-" + id, true,  false, 0); }
    private Player nonMember(String id) { return new Player(id, "G-" + id, false, false, 0); }
    private Player founder(String id)   { return new Player(id, "F-" + id, false, true,  0); }

    private GamePlayer presentFor(String playerId) {
        return GamePlayer.forPlayer(playerId).withPresent(true);
    }

    private Game buildGame(List<GamePlayer> gps) {
        return Game.create(DATE, gps);
    }

    // ── Pool calculation ──────────────────────────────────────────────────────

    @Test
    void poolWithMembersOnly() {
        var players = List.of(member("p1"), member("p2"));
        var gps     = List.of(presentFor("p1"), presentFor("p2"));
        var calc    = sut.execute(buildGame(gps), players);

        // 2 members × $25 = $50 total
        assertEquals(50.0, calc.totalPool(), 0.01);
        assertEquals(10.0, calc.bountyPool(), 0.01); // 20%
        assertEquals(40.0, calc.prizePool(),  0.01); // 80%
    }

    @Test
    void poolWithNonMembersOnly() {
        var players = List.of(nonMember("p1"), nonMember("p2"));
        var gps     = List.of(presentFor("p1"), presentFor("p2"));
        var calc    = sut.execute(buildGame(gps), players);

        // 2 non-members × $30 = $60
        assertEquals(60.0, calc.totalPool(), 0.01);
    }

    @Test
    void poolMixedMembersAndNonMembers() {
        var players = List.of(member("p1"), nonMember("p2"));
        var gps     = List.of(presentFor("p1"), presentFor("p2"));
        var calc    = sut.execute(buildGame(gps), players);

        // $25 + $30 = $55
        assertEquals(55.0, calc.totalPool(), 0.01);
        assertEquals(1, calc.memberCount());
        assertEquals(1, calc.nonMemberCount());
    }

    @Test
    void founderCountsAsMemberForFee() {
        var players = List.of(founder("p1"));
        var gps     = List.of(presentFor("p1"));
        var calc    = sut.execute(buildGame(gps), players);

        assertEquals(25.0, calc.totalPool(), 0.01);
        assertEquals(1, calc.memberCount());
    }

    @Test
    void rebuysAddToPool() {
        var players = List.of(member("p1"));
        var gp      = GamePlayer.forPlayer("p1").withPresent(true).withRebuyCount(2);
        var calc    = sut.execute(buildGame(List.of(gp)), players);

        // $25 entry + 2 × $25 rebuys = $75
        assertEquals(75.0, calc.totalPool(), 0.01);
        assertEquals(2, calc.totalRebuys());
    }

    // ── Distribution tiers ────────────────────────────────────────────────────

    @Test
    void distributionFor13Players() {
        var dist = PrizeRules.distribution(13);
        assertEquals(List.of(0.50, 0.30, 0.20), dist);
    }

    @Test
    void distributionFor20Players() {
        var dist = PrizeRules.distribution(20);
        assertEquals(List.of(0.45, 0.25, 0.20, 0.10), dist);
    }

    @Test
    void distributionFor25Players() {
        var dist = PrizeRules.distribution(25);
        assertEquals(List.of(0.45, 0.25, 0.15, 0.10, 0.05), dist);
    }

    // ── Prize assignment ──────────────────────────────────────────────────────

    @Test
    void prizesOnlyAssignedToRankedPlayers() {
        var players = List.of(member("p1"), member("p2"), member("p3"),
                member("p4"), member("p5"), member("p6"),
                member("p7"), member("p8"), member("p9"),
                member("p10"), member("p11"), member("p12"));

        var gps = new ArrayList<GamePlayer>();
        for (int i = 1; i <= 12; i++) {
            var gp = presentFor("p" + i);
            if (i <= 3) gp = gp.withFinalPosition(i); // only first 3 ranked
            gps.add(gp);
        }

        var calc = sut.execute(buildGame(gps), players);

        assertEquals(3, calc.prizes().size());
        assertEquals(9, calc.unrankedCount());
    }

    @Test
    void firstPlaceGetsFiftyPercentOfPrizePool() {
        // Build 13 members to trigger 50/30/20 distribution
        var players = new ArrayList<Player>();
        var gps     = new ArrayList<GamePlayer>();
        for (int i = 1; i <= 13; i++) {
            players.add(member("p" + i));
            var gp = presentFor("p" + i);
            if (i <= 3) gp = gp.withFinalPosition(i);
            gps.add(gp);
        }

        var calc = sut.execute(buildGame(gps), players);
        // 13 × $25 = $325 total, $260 prize pool (80%)
        // 1st = 50% of $260 = $130
        assertEquals(130.0, calc.prizes().get(0).prizeAmount(), 0.01);
    }

    @Test
    void emptyPrizesWhenNoRankedPlayers() {
        var players = List.of(member("p1"));
        var gps     = List.of(presentFor("p1")); // present but no position
        var calc    = sut.execute(buildGame(gps), players);

        assertTrue(calc.prizes().isEmpty());
        assertEquals(1, calc.unrankedCount());
    }

    @Test
    void emptyCalculationWhenNoPresentPlayers() {
        var players = List.of(member("p1"));
        var gp = GamePlayer.forPlayer("p1"); // not present
        var calc = sut.execute(buildGame(List.of(gp)), players);

        assertEquals(0.0, calc.totalPool(), 0.01);
        assertTrue(calc.prizes().isEmpty());
    }
}
