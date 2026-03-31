package com.sgf.poker.domain.model;

import java.util.List;

/** Prize result for a single ranked player. */
public record PlayerPrize(
        String gamePlayerId,
        String playerName,
        int position,
        double entryFee,
        double prizeAmount) {}
