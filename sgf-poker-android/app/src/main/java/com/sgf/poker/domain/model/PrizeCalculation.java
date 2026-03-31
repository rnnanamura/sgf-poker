package com.sgf.poker.domain.model;

import java.util.List;

/** Full prize calculation result for a game. */
public record PrizeCalculation(
        int presentCount,
        int memberCount,
        int nonMemberCount,
        int totalRebuys,
        double totalPool,
        double bountyPool,
        double prizePool,
        List<Double> distribution,
        List<PlayerPrize> prizes,
        int unrankedCount) {}
