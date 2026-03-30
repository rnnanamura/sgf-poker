// Core/UseCases/UpdatePlayerUseCase.swift

import Foundation

/// Applies a mutation to an existing player and persists the result.
/// Used for toggling membership, founder status, and updating points.
final class UpdatePlayerUseCase {

    private let repository: PlayerRepository

    init(repository: PlayerRepository) {
        self.repository = repository
    }

    /// Fetches the player, applies the mutation closure, then saves.
    /// - Parameters:
    ///   - id: The UUID of the player to update.
    ///   - mutation: A closure that receives an `inout Player` to mutate.
    /// - Throws: `PlayerError.playerNotFound` if the player does not exist.
    @discardableResult
    func execute(id: UUID, mutation: (inout Player) -> Void) throws -> Player {
        var players = try repository.fetchAll()

        guard let index = players.firstIndex(where: { $0.id == id }) else {
            throw PlayerError.playerNotFound(id: id.uuidString)
        }

        mutation(&players[index])
        try repository.save(players[index])
        return players[index]
    }
}
