// Core/Repositories/LocalStorageGameRepository.swift

import Foundation

/// Persists games as a JSON file in the app's Documents directory.
/// Survives app restarts; data is private to the app sandbox.
final class LocalStorageGameRepository: GameRepository {

    // MARK: – Storage

    private let fileURL: URL
    private let lock = NSLock()
    private let encoder = JSONEncoder()
    private let decoder = JSONDecoder()

    // MARK: – Init

    init(filename: String = "games.json") {
        let docs = FileManager.default.urls(
            for: .documentDirectory,
            in: .userDomainMask
        ).first!
        fileURL = docs.appendingPathComponent(filename)

        encoder.dateEncodingStrategy = .iso8601
        decoder.dateDecodingStrategy = .iso8601
    }

    // MARK: – GameRepository

    func fetchAll() throws -> [Game] {
        try lock.withLock {
            guard FileManager.default.fileExists(atPath: fileURL.path) else {
                return []
            }
            let data = try Data(contentsOf: fileURL)
            let games = try decoder.decode([Game].self, from: data)
            return games.sorted { $0.eventDate > $1.eventDate }
        }
    }

    func save(_ game: Game) throws {
        try lock.withLock {
            var games = try loadAll()

            let cal = Calendar.current
            let duplicate = games.contains {
                $0.id != game.id &&
                cal.component(.year,  from: $0.eventDate) == cal.component(.year,  from: game.eventDate) &&
                cal.component(.month, from: $0.eventDate) == cal.component(.month, from: game.eventDate)
            }
            guard !duplicate else {
                throw GameError.duplicateDate(game.name)
            }

            // Replace existing entry or append new one
            if let index = games.firstIndex(where: { $0.id == game.id }) {
                games[index] = game
            } else {
                games.append(game)
            }

            try persist(games)
        }
    }

    func delete(id: UUID) throws {
        try lock.withLock {
            var games = try loadAll()
            guard games.contains(where: { $0.id == id }) else {
                throw GameError.gameNotFound(id: id.uuidString)
            }
            games.removeAll { $0.id == id }
            try persist(games)
        }
    }

    // MARK: – Private helpers

    /// Loads the raw array without sorting — internal use only.
    private func loadAll() throws -> [Game] {
        guard FileManager.default.fileExists(atPath: fileURL.path) else {
            return []
        }
        let data = try Data(contentsOf: fileURL)
        return try decoder.decode([Game].self, from: data)
    }

    private func persist(_ games: [Game]) throws {
        let data = try encoder.encode(games)
        try data.write(to: fileURL, options: .atomic)
    }
}
