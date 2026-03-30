// Presentation/Views/GameRowView.swift

import SwiftUI

/// A single row in the games list. Pure rendering — no logic.
struct GameRowView: View {

    let game: Game
    var onDelete: (() -> Void)?

    var body: some View {
        HStack(spacing: 14) {
            Image(systemName: "gamecontroller")
                .font(.title3)
                .foregroundStyle(.secondary)
                .frame(width: 32)

            VStack(alignment: .leading, spacing: 2) {
                Text(game.name)
                    .font(.body)
                    .fontWeight(.medium)

                Text(game.eventDate, style: .date)
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }

            Spacer()

            if let onDelete {
                Button(action: onDelete) {
                    Image(systemName: "trash")
                        .foregroundStyle(.red)
                }
                .buttonStyle(.borderless)
            }
        }
        .padding(.vertical, 4)
    }
}
