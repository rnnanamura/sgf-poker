// Core/UseCases/DeletePlayerUseCase.swift

import Foundation

/// Removes a player from the repository by their identifier.
final class DeletePlayerUseCase {

    private let repository: PlayerRepository

    init(repository: PlayerRepository) {
        self.repository = repository
    }

    /// - Parameter id: The UUID of the player to remove.
    /// - Throws: `PlayerError.playerNotFound` if no matching player exists.
    func execute(id: UUID) throws {
        try repository.delete(id: id)
    }
}
