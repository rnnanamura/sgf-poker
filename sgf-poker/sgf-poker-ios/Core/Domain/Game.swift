// Core/Domain/Game.swift
// Pure domain model — no UIKit, no SwiftUI, no persistence imports.

import Foundation

/// Represents a single game entry in the system.
struct Game: Identifiable, Equatable, Hashable, Codable, Sendable {
    let id: UUID
    let eventDate: Date
    let createdAt: Date
    var players: [GamePlayer]
    
    /// Derived display name: first 3 letters of month + year, e.g. "Jan 2025".
    var name: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "MMM yyyy"
        return formatter.string(from: eventDate)
    }
    
    init(
        id: UUID = UUID(),
        eventDate: Date,
        createdAt: Date = Date(),
        players: [GamePlayer] = []
    ) {
        self.id = id
        self.eventDate = eventDate
        self.createdAt = createdAt
        self.players = players
    }
}
    // MARK: – Helpers
     
    extension Game {
        /// Calendar components of the event date (year + month only — day is ignored for display).
        var eventYear: Int {
            Calendar.current.component(.year, from: eventDate)
        }
        
        var eventMonth: Int {
            Calendar.current.component(.month, from: eventDate)
        }
        /// Players who confirmed they are coming.
        var confirmedPlayers: [GamePlayer] {
            players.filter { $0.coming }
        }
     
        /// Players who physically attended.
        var presentPlayers: [GamePlayer] {
            players.filter { $0.present }
        }
     
        /// Players who have been ranked (game finished for them).
        var rankedPlayers: [GamePlayer] {
            players
                .filter { $0.hasFinished }
                .sorted { ($0.finalPosition ?? Int.max) < ($1.finalPosition ?? Int.max) }
        }
     
        /// Whether all present players have a final position assigned.
        var isFinished: Bool {
            !presentPlayers.isEmpty && presentPlayers.allSatisfy { $0.hasFinished }
        }
    }
    



