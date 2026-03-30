// Core/Domain/Player.swift

import Foundation

/// Represents a player participating in games.
struct Player: Identifiable, Equatable, Hashable, Codable, Sendable {
    let id: UUID
    var name: String
    var isMember: Bool
    var isFounder: Bool
    var currentPoints: Int

    init(
        id: UUID = UUID(),
        name: String,
        isMember: Bool = false,
        isFounder: Bool = false,
        currentPoints: Int = 0
    ) {
        self.id = id
        self.name = name
        self.isMember = isMember
        self.isFounder = isFounder
        self.currentPoints = currentPoints
    }
}
