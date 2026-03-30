// Core/Domain/GameError.swift

import Foundation

/// All errors that can occur within the games domain.
enum GameError: Error, Equatable {
    case duplicateDate(String)
    case gameNotFound(id: String)
    case storageFailure(reason: String)
}

extension GameError: LocalizedError {
    var errorDescription: String? {
        switch self {
        case .duplicateDate(let label):
            return "A game named \(label) already exists."
        case .gameNotFound(let id):
            return "No game found with id \(id)."
        case .storageFailure(let reason):
            return "Storage error: \(reason)"
        }
    }
}
