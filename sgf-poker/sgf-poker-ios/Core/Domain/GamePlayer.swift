// Core/Domain/GamePlayer.swift

import Foundation

/// Represents a player's participation record in a specific game.
/// Links a `Player` to a `Game` and tracks their in-game status and outcome.
struct GamePlayer: Identifiable, Equatable, Hashable, Codable, Sendable {
    let id: UUID
    let playerId: UUID         // Reference to Player — avoids embedding a mutable copy
    var coming: Bool           // RSVP — player indicated they will attend
    var present: Bool          // Player physically showed up
    var payed: Bool            // Player has paid the buy-in
    var rebuyCount: Int        // Number of rebuys taken during the game
    var finalPosition: Int?    // Finishing position (nil = game still in progress or player not ranked)

    init(
        id: UUID = UUID(),
        playerId: UUID,
        coming: Bool = false,
        present: Bool = false,
        payed: Bool = false,
        rebuyCount: Int = 0,
        finalPosition: Int? = nil
    ) {
        self.id = id
        self.playerId = playerId
        self.coming = coming
        self.present = present
        self.payed = payed
        self.rebuyCount = rebuyCount
        self.finalPosition = finalPosition
    }
}

// MARK: – Convenience

extension GamePlayer {
    /// Total buy-ins including the initial entry (1 + rebuys).
    var totalBuyIns: Int {
        1 + rebuyCount
    }

    /// Whether the player has finished (has a final position assigned).
    var hasFinished: Bool {
        finalPosition != nil
    }
}
