// Presentation/ViewModels/PlayersViewModel.swift

import Foundation
import Observation

/// Drives ManagePlayersView. Owns UI state and delegates all logic to use cases.
@Observable
final class PlayersViewModel {

    // MARK: – Published state
    var players: [Player] = []
    var errorMessage: String?
    var isShowingAddSheet = false

    // MARK: – Add-sheet state
    var newPlayerName: String = ""
    var newPlayerIsMember: Bool = false
    var newPlayerIsFounder: Bool = false

    // MARK: – Use cases
    private let fetchPlayers: FetchPlayersUseCase
    private let createPlayer: CreatePlayerUseCase
    private let deletePlayer: DeletePlayerUseCase
    private let updatePlayer: UpdatePlayerUseCase

    // MARK: – Init
    init(
        fetchPlayers: FetchPlayersUseCase,
        createPlayer: CreatePlayerUseCase,
        deletePlayer: DeletePlayerUseCase,
        updatePlayer: UpdatePlayerUseCase
    ) {
        self.fetchPlayers = fetchPlayers
        self.createPlayer = createPlayer
        self.deletePlayer = deletePlayer
        self.updatePlayer = updatePlayer
    }

    // MARK: – Intents

    func onAppear() {
        loadPlayers()
    }

    /// Opens the add player sheet with reset fields.
    func didTapAddPlayer() {
        newPlayerName = ""
        newPlayerIsMember = false
        newPlayerIsFounder = false
        isShowingAddSheet = true
    }

    /// Validates and persists the new player from sheet state.
    func confirmAddPlayer() {
        do {
            try createPlayer.execute(
                name: newPlayerName,
                isMember: newPlayerIsMember,
                isFounder: newPlayerIsFounder
            )
            isShowingAddSheet = false
            loadPlayers()
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    func cancelAddPlayer() {
        isShowingAddSheet = false
    }

    /// Toggles membership status for the given player.
    func makePlayerMember(id: UUID) {
        do {
            try updatePlayer.execute(id: id) { $0.isMember.toggle() }
            loadPlayers()
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    /// Toggles founder status for the given player.
    func makePlayerFounder(id: UUID) {
        do {
            try updatePlayer.execute(id: id) { $0.isFounder.toggle() }
            loadPlayers()
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    /// Removes a player by index set (for swipe-to-delete).
    func deletePlayer(at offsets: IndexSet) {
        offsets.forEach { index in
            do {
                try deletePlayer.execute(id: players[index].id)
            } catch {
                errorMessage = error.localizedDescription
            }
        }
        loadPlayers()
    }

    /// Removes a player directly by ID.
    func deletePlayer(id: UUID) {
        do {
            try deletePlayer.execute(id: id)
            loadPlayers()
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    // MARK: – Helpers

    var isAddConfirmEnabled: Bool {
        !newPlayerName.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }

    // MARK: – Private

    private func loadPlayers() {
        do {
            players = try fetchPlayers.execute()
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}

// MARK: – Convenience factory
extension PlayersViewModel {
    static func makeDefault() -> PlayersViewModel {
        let repo = LocalStoragePlayerRepository()
        return PlayersViewModel(
            fetchPlayers: FetchPlayersUseCase(repository: repo),
            createPlayer: CreatePlayerUseCase(repository: repo),
            deletePlayer: DeletePlayerUseCase(repository: repo),
            updatePlayer: UpdatePlayerUseCase(repository: repo)
        )
    }
}
