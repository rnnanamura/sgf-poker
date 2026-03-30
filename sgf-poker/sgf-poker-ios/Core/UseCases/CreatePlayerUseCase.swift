// Core/UseCases/CreatePlayerUseCase.swift

import Foundation

/// Validates input and persists a new player.
final class CreatePlayerUseCase {

    static let maxNameLength = 50

    private let repository: PlayerRepository

    init(repository: PlayerRepository) {
        self.repository = repository
    }

    /// - Parameters:
    ///   - name: Display name for the player (trimmed internally).
    ///   - isMember: Whether the player holds membership. Defaults to false.
    ///   - isFounder: Whether the player is a founder. Defaults to false.
    /// - Returns: The newly created `Player`.
    /// - Throws: `PlayerError` describing the validation or storage failure.
    @discardableResult
    func execute(name: String, isMember: Bool = false, isFounder: Bool = false) throws -> Player {
        let trimmed = name.trimmingCharacters(in: .whitespacesAndNewlines)

        guard !trimmed.isEmpty else { throw PlayerError.emptyName }
        guard trimmed.count <= Self.maxNameLength else {
            throw PlayerError.nameTooLong(maxLength: Self.maxNameLength)
        }

        let player = Player(name: trimmed, isMember: isMember, isFounder: isFounder)
        try repository.save(player)
        return player
    }
}
