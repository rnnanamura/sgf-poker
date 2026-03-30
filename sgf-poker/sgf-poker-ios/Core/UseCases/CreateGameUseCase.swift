// Core/UseCases/CreateGameUseCase.swift

import Foundation

/// Validates input and persists a new game,
/// pre-populating it with all currently registered players as GamePlayer entries.
final class CreateGameUseCase {

    private let gameRepository: GameRepository
    private let playerRepository: PlayerRepository

    init(gameRepository: GameRepository, playerRepository: PlayerRepository) {
        self.gameRepository = gameRepository
        self.playerRepository = playerRepository
    }

    /// - Parameter eventDate: The date of the game event. Only year and month are significant.
    /// - Returns: The newly created `Game` with all current players attached.
    /// - Throws: `GameError.duplicateDate` if a game for that month/year already exists.
    @discardableResult
    func execute(eventDate: Date) throws -> Game {
        let existingPlayers = (try? playerRepository.fetchAll()) ?? []

        let gamePlayers = existingPlayers.map {
            GamePlayer(playerId: $0.id)
        }

        let game = Game(eventDate: eventDate, players: gamePlayers)
        try gameRepository.save(game)
        return game
    }
}
