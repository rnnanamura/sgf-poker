// Presentation/Views/EmptyStateView.swift

import SwiftUI

/// Shown when the game list is empty.
struct EmptyStateView: View {

    let onAdd: () -> Void

    var body: some View {
        ContentUnavailableView {
            Label("No Games Yet", systemImage: "gamecontroller")
        } description: {
            Text("Add your first game to get started.")
        } actions: {
            Button("Add Game", action: onAdd)
                .buttonStyle(.borderedProminent)
        }
    }
}
