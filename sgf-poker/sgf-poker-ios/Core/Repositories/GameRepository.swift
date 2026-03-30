// Core/Repositories/GameRepository.swift

import Foundation

/// Contract for any games data source.
/// Conforming types can be in-memory, CoreData, network, etc.
protocol GameRepository: AnyObject {
    /// Returns all stored games, sorted by creation date descending.
    func fetchAll() throws -> [Game]

    /// Persists a new game. Throws if a game with the same name exists.
    func save(_ game: Game) throws

    /// Removes a game by its identifier.
    func delete(id: UUID) throws
}
