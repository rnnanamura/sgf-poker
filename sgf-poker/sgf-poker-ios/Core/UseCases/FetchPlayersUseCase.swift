// Core/UseCases/FetchPlayersUseCase.swift

/// Retrieves the full ordered player list from the repository.
final class FetchPlayersUseCase {

    private let repository: PlayerRepository

    init(repository: PlayerRepository) {
        self.repository = repository
    }

    func execute() throws -> [Player] {
        try repository.fetchAll()
    }
}
