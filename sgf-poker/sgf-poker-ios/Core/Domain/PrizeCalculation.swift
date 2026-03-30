// Core/Domain/PrizeCalculation.swift

import Foundation

// MARK: – Fixed rules

/// All prize rules are fixed by the league — no user configuration needed.
enum PrizeRules {
    static let memberEntryFee: Double    = 25
    static let nonMemberEntryFee: Double = 30
    static let rebuyAmount: Double       = 25
    static let bountyShare: Double       = 0.20   // 20% of total pool
    static let prizeShare:  Double       = 0.80   // 80% of total pool

    /// Prize distribution by number of present players.
    static func distribution(for playerCount: Int) -> [Double] {
        switch playerCount {
        case 7...16: return [0.50, 0.30, 0.20]
        case 17...22: return [0.45, 0.25, 0.20, 0.10]
        default:      return [0.45, 0.25, 0.15, 0.10, 0.05] // 23+
        }
    }

    /// Human-readable description of the active distribution.
    static func distributionLabel(for playerCount: Int) -> String {
        let dist = distribution(for: playerCount)
        return dist.enumerated()
            .map { i, pct in "\(i + 1)st: \(Int(pct * 100))%" }
            .joined(separator: "  ·  ")
    }
}

// MARK: – Result types

/// The computed result for a single player's prize.
struct PlayerPrize: Identifiable, Equatable {
    let id: UUID           // GamePlayer.id
    let playerName: String
    let position: Int
    let entryFee: Double   // What this player paid to enter
    let prizeAmount: Double
}

/// Full calculation result for a game.
struct PrizeCalculation: Equatable {
    let presentCount: Int
    let memberCount: Int
    let nonMemberCount: Int
    let totalRebuys: Int
    let totalPool: Double          // All entry fees + rebuys
    let bountyPool: Double         // 20% of total pool
    let prizePool: Double          // 80% of total pool
    let distribution: [Double]     // Active prize split percentages
    let prizes: [PlayerPrize]      // Ranked players with prize amounts
    let unrankedCount: Int         // Present players without a final position
}
