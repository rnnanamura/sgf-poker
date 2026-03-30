// Core/Repositories/InMemoryGameRepository.swift

import Foundation

/// Thread-safe, in-memory implementation of GameRepository.
/// Replace with CoreDataGameRepository or NetworkGameRepository as needed.
final class InMemoryGameRepository: GameRepository {

    private var store: [UUID: Game] = [:]
    private let lock = NSLock()

    func fetchAll() throws -> [Game] {
        lock.withLock {
            store.values.sorted { $0.eventDate > $1.eventDate }
        }
    }

    func save(_ game: Game) throws {
        try lock.withLock {
            let cal = Calendar.current
            let duplicate = store.values.contains {
                $0.id != game.id &&
                cal.component(.year, from: $0.eventDate) == cal.component(.year, from: game.eventDate) &&
                cal.component(.month, from: $0.eventDate) == cal.component(.month,  from: game.eventDate)
            }
            if duplicate {
                throw GameError.duplicateDate(game.name)
            }
            store[game.id] = game
        }
    }

    func delete(id: UUID) throws {
        try lock.withLock {
            guard store[id] != nil else {
                throw GameError.gameNotFound(id: id.uuidString)
            }
            store.removeValue(forKey: id)
        }
    }
}
