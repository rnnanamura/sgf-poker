// Core/UseCases/FetchGamesUseCase.swift

/// Retrieves the full ordered game list from the repository.
final class FetchGamesUseCase {

    private let repository: GameRepository

    init(repository: GameRepository) {
        self.repository = repository
    }

    /// - Returns: Games sorted by creation date, newest first.
    /// - Throws: `GameError.storageFailure` if the repository cannot be read.
    func execute() throws -> [Game] {
        try repository.fetchAll()
    }
}
