// Presentation/ViewModels/GamesViewModel.swift

import Foundation
import Observation

/// Drives GamesListView. Owns UI state and delegates all logic to use cases.
@Observable
final class GamesViewModel {
    
    // MARK: – Published state
    var games: [Game] = []
    var errorMessage: String?
    var isShowingCreateSheet = false
    
    // MARK: – Create-sheet state
    var newEventDate: Date = Date()
    
    // MARK: – Use cases
    private let fetchGames: FetchGamesUseCase
    private let createGame: CreateGameUseCase
    private let deleteGame: DeleteGameUseCase
    
    // MARK: – Init
    init(
        fetchGames: FetchGamesUseCase,
        createGame: CreateGameUseCase,
        deleteGame: DeleteGameUseCase
    ) {
        self.fetchGames = fetchGames
        self.createGame = createGame
        self.deleteGame = deleteGame
    }
    
    // MARK: – Intents
    
    func onAppear() {
        loadGames()
    }
    
    func didTapCreateGame() {
        newEventDate = Date()
        isShowingCreateSheet = true
    }
    
    func confirmCreateGame() {
        do {
            try createGame.execute(eventDate: newEventDate)
            isShowingCreateSheet = false
            loadGames()
        } catch {
            errorMessage = error.localizedDescription
        }
    }
    
    func cancelCreateGame() {
        isShowingCreateSheet = false
    }
    
    func didRequestDelete(at offsets: IndexSet) {
        offsets.forEach { index in
            let game = games[index]
            do {
                try deleteGame.execute(id: game.id)
            } catch {
                errorMessage = error.localizedDescription
            }
        }
        loadGames()
    }
    private func loadGames() {
        do {
            games = try fetchGames.execute()
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}
 
// MARK: – Convenience factory
extension GamesViewModel {
    static func makeDefault() -> GamesViewModel {
        let gameRepo = LocalStorageGameRepository()
        let playerRepo = LocalStoragePlayerRepository()
        return GamesViewModel(
            fetchGames: FetchGamesUseCase(repository: gameRepo),
            createGame: CreateGameUseCase(gameRepository: gameRepo, playerRepository: playerRepo),
            deleteGame: DeleteGameUseCase(repository: gameRepo)
        )
    }
}
