// Core/UseCases/DeleteGameUseCase.swift

import Foundation

/// Removes a game from the repository by its identifier.
final class DeleteGameUseCase {

    private let repository: GameRepository

    init(repository: GameRepository) {
        self.repository = repository
    }

    /// - Parameter id: The UUID of the game to remove.
    /// - Throws: `GameError.gameNotFound` if no matching game exists.
    func execute(id: UUID) throws {
        try repository.delete(id: id)
    }
}
