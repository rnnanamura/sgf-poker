// Core/Domain/PlayerError.swift

import Foundation

/// All errors that can occur within the players domain.
enum PlayerError: Error, Equatable {
    case emptyName
    case nameTooLong(maxLength: Int)
    case duplicateName(String)
    case playerNotFound(id: String)
    case storageFailure(reason: String)
}

extension PlayerError: LocalizedError {
    var errorDescription: String? {
        switch self {
        case .emptyName:
            return "Player name cannot be empty."
        case .nameTooLong(let max):
            return "Player name must be \(max) characters or fewer."
        case .duplicateName(let name):
            return "A player named \(name) already exists."
        case .playerNotFound(let id):
            return "No player found with id \(id)."
        case .storageFailure(let reason):
            return "Storage error: \(reason)"
        }
    }
}
