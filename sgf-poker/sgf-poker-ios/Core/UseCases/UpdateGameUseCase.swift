// Core/UseCases/UpdateGameUseCase.swift

import Foundation

/// Applies a mutation to an existing game and persists the result.
final class UpdateGameUseCase {

    private let repository: GameRepository

    init(repository: GameRepository) {
        self.repository = repository
    }

    /// Fetches the game, applies the mutation closure, then saves.
    @discardableResult
    func execute(id: UUID, mutation: (inout Game) -> Void) throws -> Game {
        var games = try repository.fetchAll()

        guard let index = games.firstIndex(where: { $0.id == id }) else {
            throw GameError.gameNotFound(id: id.uuidString)
        }

        mutation(&games[index])
        try repository.save(games[index])
        return games[index]
    }
}
