package com.sgf.poker.domain.model;

import java.util.List;

/** Fixed league prize rules — no user configuration. */
public final class PrizeRules {

    public static final double MEMBER_ENTRY_FEE     = 25.0;
    public static final double NON_MEMBER_ENTRY_FEE = 35.0;
    public static final double REBUY_AMOUNT         = 25.0;
    public static final double BOUNTY_SHARE         = 0.20;
    public static final double PRIZE_SHARE          = 0.80;

    private PrizeRules() {}

    /** Prize distribution by number of present players. */
    public static List<Double> distribution(int playerCount) {
        if (playerCount >=6 && playerCount <= 10) {
            return List.of(0.50, 0.30, 0.20);
        } else if (playerCount >= 11 && playerCount <= 16) {
            return List.of(0.50, 0.30, 0.20);
        } else if (playerCount >= 17 && playerCount <= 22) {
            return List.of(0.45, 0.25, 0.20, 0.10);
        } else {
            // 23+ (also used as fallback for < 11)
            return List.of(0.45, 0.25, 0.15, 0.10, 0.05);
        }
    }


}
