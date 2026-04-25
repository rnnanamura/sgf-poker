package com.sgf.poker.usecases.prizes;

import com.sgf.poker.domain.model.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** Calculates prize amounts and bounty pool using fixed league rules. */
public class CalculatePrizesUseCase {

    public PrizeCalculation execute(Game game, List<Player> players) {
        var presentGPs = game.getPresentPlayers();
        int presentCount = presentGPs.size();

        int memberCount = (int) presentGPs.stream().filter(gp -> {
            var p = findPlayer(players, gp.getPlayerId());
            return p != null && p.isEligibleForMemberFee();
        }).count();
        int nonMemberCount = presentCount - memberCount;

        double totalCollected = memberCount    * PrizeRules.MEMBER_ENTRY_FEE
                             + nonMemberCount * PrizeRules.NON_MEMBER_ENTRY_FEE;
        double nonMemberFeeTotal = nonMemberCount * (PrizeRules.NON_MEMBER_ENTRY_FEE - PrizeRules.MEMBER_ENTRY_FEE);
        double entryFeeTotal = totalCollected - nonMemberFeeTotal;

        int totalRebuys = presentGPs.stream().mapToInt(GamePlayer::getRebuyCount).sum();
        double rebuyTotal = totalRebuys * PrizeRules.REBUY_AMOUNT;

        double totalPool  = entryFeeTotal + rebuyTotal;
        double bountyPool = totalPool * PrizeRules.BOUNTY_SHARE;
        double prizePool  = totalPool * PrizeRules.PRIZE_SHARE;

        var distribution = PrizeRules.distribution(presentCount);

        var ranked = presentGPs.stream()
                .filter(GamePlayer::hasFinished)
                .sorted((a, b) -> {
                    int pa = a.getFinalPosition() != null ? a.getFinalPosition() : Integer.MAX_VALUE;
                    int pb = b.getFinalPosition() != null ? b.getFinalPosition() : Integer.MAX_VALUE;
                    return Integer.compare(pa, pb);
                })
                .collect(Collectors.toList());

        var prizes = IntStream.range(0, Math.min(ranked.size(), distribution.size()))
                .mapToObj(i -> {
                    var gp = ranked.get(i);
                    var player = findPlayer(players, gp.getPlayerId());
                    String name = player != null ? player.getName() : "Unknown";
                    double fee  = player != null && player.isEligibleForMemberFee()
                            ? PrizeRules.MEMBER_ENTRY_FEE : PrizeRules.NON_MEMBER_ENTRY_FEE;
                    int pos = gp.getFinalPosition() != null ? gp.getFinalPosition() : i + 1;
                    return new PlayerPrize(gp.getId(), name, pos, fee, prizePool * distribution.get(i));
                })
                .collect(Collectors.toList());

        return new PrizeCalculation(
                presentCount, memberCount, nonMemberCount, totalRebuys,
                totalCollected, nonMemberFeeTotal, totalPool,  bountyPool, prizePool, distribution, prizes,
                presentCount - ranked.size());
    }

    private Player findPlayer(List<Player> players, String id) {
        return players.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }
}
