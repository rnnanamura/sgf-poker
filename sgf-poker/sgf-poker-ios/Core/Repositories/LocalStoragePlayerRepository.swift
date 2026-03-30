// Core/Repositories/LocalStoragePlayerRepository.swift

import Foundation

/// Persists players as a JSON file in the app's Documents directory.
final class LocalStoragePlayerRepository: PlayerRepository {

    // MARK: – Storage

    private let fileURL: URL
    private let lock = NSLock()
    private let encoder = JSONEncoder()
    private let decoder = JSONDecoder()

    // MARK: – Init

    init(filename: String = "players.json") {
        let docs = FileManager.default.urls(
            for: .documentDirectory,
            in: .userDomainMask
        ).first!
        fileURL = docs.appendingPathComponent(filename)

        encoder.dateEncodingStrategy = .iso8601
        decoder.dateDecodingStrategy = .iso8601
    }

    // MARK: – PlayerRepository

    func fetchAll() throws -> [Player] {
        try lock.withLock {
            guard FileManager.default.fileExists(atPath: fileURL.path) else {
                return []
            }
            let data = try Data(contentsOf: fileURL)
            let players = try decoder.decode([Player].self, from: data)
            return players.sorted { $0.name.localizedCaseInsensitiveCompare($1.name) == .orderedAscending }
        }
    }

    func save(_ player: Player) throws {
        try lock.withLock {
            var players = try loadAll()

            let duplicate = players.contains {
                $0.id != player.id &&
                $0.name.lowercased() == player.name.lowercased()
            }
            guard !duplicate else {
                throw PlayerError.duplicateName(player.name)
            }

            if let index = players.firstIndex(where: { $0.id == player.id }) {
                players[index] = player
            } else {
                players.append(player)
            }

            try persist(players)
        }
    }

    func delete(id: UUID) throws {
        try lock.withLock {
            var players = try loadAll()
            guard players.contains(where: { $0.id == id }) else {
                throw PlayerError.playerNotFound(id: id.uuidString)
            }
            players.removeAll { $0.id == id }
            try persist(players)
        }
    }

    // MARK: – Private helpers

    private func loadAll() throws -> [Player] {
        guard FileManager.default.fileExists(atPath: fileURL.path) else {
            return []
        }
        let data = try Data(contentsOf: fileURL)
        return try decoder.decode([Player].self, from: data)
    }

    private func persist(_ players: [Player]) throws {
        let data = try encoder.encode(players)
        try data.write(to: fileURL, options: .atomic)
    }
}
