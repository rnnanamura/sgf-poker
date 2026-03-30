// Core/UseCases/CalculatePrizesUseCase.swift

import Foundation

/// Calculates prize amounts and bounty pool using the league's fixed rules.
final class CalculatePrizesUseCase {

    func execute(game: Game, players: [Player]) -> PrizeCalculation {
        let presentGamePlayers = game.players.filter { $0.present }
        let presentCount = presentGamePlayers.count

        // Resolve full Player for each GamePlayer to check membership
        let presentPlayers: [(gp: GamePlayer, player: Player?)] = presentGamePlayers.map { gp in
            (gp, players.first { $0.id == gp.playerId })
        }

        // Entry fees: members pay 25, non-members pay 30
        let memberCount    = presentPlayers.filter { $0.player?.isMember == true || $0.player?.isFounder == true }.count
        let nonMemberCount = presentCount - memberCount
        let entryFeeTotal  = Double(memberCount) * PrizeRules.memberEntryFee
                           + Double(nonMemberCount) * PrizeRules.nonMemberEntryFee

        // Rebuys: $25 each
        let totalRebuys = presentGamePlayers.reduce(0) { $0 + $1.rebuyCount }
        let rebuyTotal  = Double(totalRebuys) * PrizeRules.rebuyAmount

        let totalPool  = entryFeeTotal + rebuyTotal
        let bountyPool = totalPool * PrizeRules.bountyShare
        let prizePool  = totalPool * PrizeRules.prizeShare

        // Prize distribution based on present player count
        let distribution = PrizeRules.distribution(for: presentCount)

        // Build prizes for ranked players only, sorted by position
        let ranked = presentGamePlayers
            .filter  { $0.hasFinished }
            .sorted  { ($0.finalPosition ?? Int.max) < ($1.finalPosition ?? Int.max) }

        let prizes: [PlayerPrize] = ranked.enumerated().compactMap { index, gp in
            guard index < distribution.count else { return nil }
            let player    = players.first { $0.id == gp.playerId }
            let name      = player?.name ?? "Unknown"
            let entryFee  = (player?.isMember == true || player?.isFounder == true)
                            ? PrizeRules.memberEntryFee
                            : PrizeRules.nonMemberEntryFee
            let amount    = prizePool * distribution[index]
            return PlayerPrize(
                id: gp.id,
                playerName: name,
                position: gp.finalPosition ?? (index + 1),
                entryFee: entryFee,
                prizeAmount: amount
            )
        }

        let unrankedCount = presentCount - ranked.count

        return PrizeCalculation(
            presentCount: presentCount,
            memberCount: memberCount,
            nonMemberCount: nonMemberCount,
            totalRebuys: totalRebuys,
            totalPool: totalPool,
            bountyPool: bountyPool,
            prizePool: prizePool,
            distribution: distribution,
            prizes: prizes,
            unrankedCount: unrankedCount
        )
    }
}
