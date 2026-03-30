// Presentation/ViewModels/GameDetailViewModel.swift

import Foundation
import Observation

/// Drives GameDetailView. Owns the live game state and resolves player names.
@Observable
final class GameDetailViewModel {

    // MARK: – State
    var game: Game
    var players: [Player] = []
    var errorMessage: String?

    // MARK: – Use cases
    private let fetchGames: FetchGamesUseCase
    private let updateGame: UpdateGameUseCase
    private let fetchPlayers: FetchPlayersUseCase

    // MARK: – Init
    init(game: Game,
         fetchGames: FetchGamesUseCase,
         updateGame: UpdateGameUseCase, fetchPlayers: FetchPlayersUseCase) {
        self.game = game
        self.fetchGames = fetchGames
        self.updateGame = updateGame
        self.fetchPlayers = fetchPlayers
    }

    // MARK: – Lifecycle

    func onAppear() {
        reloadGame()
        loadPlayers()
    }

    // MARK: – Player name resolution

    func playerName(for gamePlayer: GamePlayer) -> String {
        players.first { $0.id == gamePlayer.playerId }?.name ?? "Unknown"
    }

    // MARK: – GamePlayer intents

    func toggleComing(for gamePlayerId: UUID) {
        updateGamePlayer(id: gamePlayerId) { $0.coming.toggle() }
    }

    func togglePresent(for gamePlayerId: UUID) {
        updateGamePlayer(id: gamePlayerId) { $0.present.toggle() }
    }

    func togglePayed(for gamePlayerId: UUID) {
        updateGamePlayer(id: gamePlayerId) { $0.payed.toggle() }
    }

    func incrementRebuy(for gamePlayerId: UUID) {
        updateGamePlayer(id: gamePlayerId) { $0.rebuyCount += 1 }
    }

    func decrementRebuy(for gamePlayerId: UUID) {
        updateGamePlayer(id: gamePlayerId) {
            if $0.rebuyCount > 0 { $0.rebuyCount -= 1 }
        }
    }

    func setFinalPosition(_ position: Int?, for gamePlayerId: UUID) {
        updateGamePlayer(id: gamePlayerId) { $0.finalPosition = position }
    }

    // MARK: – Private
    /// Re-fetches the game from storage so players added after initial creation are visible.
    private func reloadGame() {
        do {
            let games = try fetchGames.execute()
            if let fresh = games.first(where: { $0.id == game.id }) {
                game = fresh
            }
        } catch {
            errorMessage = error.localizedDescription
        }
    }
    private func updateGamePlayer(id: UUID, mutation: (inout GamePlayer) -> Void) {
        do {
            let updated = try updateGame.execute(id: game.id) { game in
                guard let idx = game.players.firstIndex(where: { $0.id == id }) else { return }
                mutation(&game.players[idx])
            }
            game = updated
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    private func loadPlayers() {
        do {
            players = try fetchPlayers.execute()
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}

// MARK: – Convenience factory
extension GameDetailViewModel {
    static func make(for game: Game) -> GameDetailViewModel {
        let gameRepo   = LocalStorageGameRepository()
        let playerRepo = LocalStoragePlayerRepository()
        return GameDetailViewModel(
            game: game,
            fetchGames: FetchGamesUseCase(repository: gameRepo),
            updateGame: UpdateGameUseCase(repository: gameRepo),
            fetchPlayers: FetchPlayersUseCase(repository: playerRepo)
        )
    }
}
