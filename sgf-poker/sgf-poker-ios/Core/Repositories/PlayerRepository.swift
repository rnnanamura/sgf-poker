// Core/Repositories/PlayerRepository.swift
import Foundation

/// Contract for any players data source.
protocol PlayerRepository: AnyObject {
    /// Returns all stored players, sorted by name ascending.
    func fetchAll() throws -> [Player]

    /// Persists a new player. Throws if a player with the same name exists.
    func save(_ player: Player) throws

    /// Removes a player by its identifier.
    func delete(id: UUID) throws
}
