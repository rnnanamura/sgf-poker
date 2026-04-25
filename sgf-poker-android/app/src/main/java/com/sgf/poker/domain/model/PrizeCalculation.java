package com.sgf.poker.domain.model;

import java.util.List;

public final class PrizeCalculation {
    private final int presentCount;
    private final int memberCount;
    private final int nonMemberCount;
    private final int totalRebuys;
    private final double totalCollected;
    private final double nonMemberFeeTotal;
    private final double totalPool;
    private final double bountyPool;
    private final double prizePool;
    private final List<Double> distribution;
    private final List<PlayerPrize> prizes;
    private final int unrankedCount;

    public PrizeCalculation(int presentCount, int memberCount, int nonMemberCount, int totalRebuys,
            double totalCollected, double nonMemberFeeTotal, double totalPool,
            double bountyPool, double prizePool, List<Double> distribution,
            List<PlayerPrize> prizes, int unrankedCount) {
        this.presentCount = presentCount;
        this.memberCount = memberCount;
        this.nonMemberCount = nonMemberCount;
        this.totalRebuys = totalRebuys;
        this.totalCollected = totalCollected;
        this.nonMemberFeeTotal = nonMemberFeeTotal;
        this.totalPool = totalPool;
        this.bountyPool = bountyPool;
        this.prizePool = prizePool;
        this.distribution = distribution;
        this.prizes = prizes;
        this.unrankedCount = unrankedCount;
    }

    public int presentCount() { return presentCount; }
    public int memberCount() { return memberCount; }
    public int nonMemberCount() { return nonMemberCount; }
    public int totalRebuys() { return totalRebuys; }
    public double totalCollected() { return totalCollected; }
    public double nonMemberFeeTotal() { return nonMemberFeeTotal; }
    public double totalPool() { return totalPool; }
    public double bountyPool() { return bountyPool; }
    public double prizePool() { return prizePool; }
    public List<Double> distribution() { return distribution; }
    public List<PlayerPrize> prizes() { return prizes; }
    public int unrankedCount() { return unrankedCount; }
}